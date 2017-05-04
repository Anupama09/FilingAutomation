package com.thomsonreuters.oa.filing.util;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.westgroup.publishingservices.uuidgenerator.UUIDException;
import com.westgroup.publishingservices.uuidgenerator.UUIDFactory;


// TODO: Auto-generated Javadoc
/**
 * Helper class to get one or more UUIDs from the UUID service.
 *
 */
public class UUIDGetter {
    /** Constant ZID */
    protected static final String ZID = "I00000000000000000000000000000000";

    /** Buffer and UUID request size. */
    protected static final int DEFAULT_BUFFER_SIZE = 1000;

    /** Number of trys to attempt before giving up. */
    protected static final int TRY_COUNT = 1;

    /** Amount of time (in millis) to wait between attempts. */
    protected static final int WAIT_TIME = 250;

    /** URL scheme offset. (http://) */
    protected static final int SCHEME_OFFSET = 7;

    /** Static instance of this. */
    private static UUIDGetter instance = null;

    /** Logging mechanism. */
    private static Logger logger = Logger.getLogger(UUIDGetter.class);

    /** Default UUID service URL. */
    protected String defaultURL = "http://uuid.ha.westgroup.com/uuid/UUID";

    /** Bufferd list of UUIDs to reduce the number network trips. */
    private ArrayList uuidBuffer;

    /** URL of the UUID service. */
    private URL url;
    
    /** Size of the UUID buffer. */
    private int myBufferSize = DEFAULT_BUFFER_SIZE;
    
    /**
     * Sets the buffer size.
     *
     * @param theBufferSize the new buffer size
     */
    public void setBufferSize(int theBufferSize) { myBufferSize = theBufferSize; }
    
    /**
     * Gets the buffer size.
     *
     * @return the buffer size
     */
    public int getBufferSize() { return myBufferSize; }

    /**
     * Creates a new UUIDGetter object.
     *
     * @throws RuntimeException TODO throws.
     */
    public UUIDGetter() {
        try {
            // create a new URL with the default buffer size as the number
            // of UUIDs to retrieve at once
            setURL(new URL(defaultURL));
        } catch (MalformedURLException mue) {
            throw new RuntimeException(
                "the code is broken - the default URL should be valid, but it's not.");
        }

        uuidBuffer = new ArrayList();

        if (instance == null) {
            instance = this;
        }
    }
    
    /**
     * Instantiates a new uUID getter.
     *
     * @param bufferSize the buffer size
     */
    public UUIDGetter(int bufferSize) {
        setBufferSize(bufferSize);
    }

    /**
     * Constructs a new UUIDGetter.
     *
     * @param u URL of UUID service without the query string
     *        ('?format=text&count='). If a query is supplied it will be
     *        ignored.
     *
     * @throws MalformedURLException if supplied URL cannot be modified with
     *         query string to create a valid URL.
     */
    public UUIDGetter(URL u) throws MalformedURLException {
        this();

        if (u != null) {
            String urlString = u.toString();

            if (urlString.indexOf("?") > 0) {
                urlString = urlString.substring(0, urlString.indexOf("?"));
            }

            if (urlString.lastIndexOf("UUID") == (urlString.length() -
                    "UUID".length())) {
                urlString = urlString + "?format=text&count=" + myBufferSize;
            } else {
                throw new MalformedURLException(
                    "URL does not conform to an acceptable UUID server address - does not end with 'UUID'");
            }

            URL newURL = new URL(urlString);
            url = newURL;
        }
    }

    /**
     * Gets the static instance of this class;
     *
     * @return DOCUMENT ME!
     */
    public static UUIDGetter getInstance() {
        if (instance == null) {
            new UUIDGetter();
        }

        return instance;
    }

    /**
     * Gets a single UUID.
     *
     * @return Returns one UUID as a string
     */
    public String getUUID() {
        // call the main getUUID method and return the first item
        return (String) getUUIDs(1).get(0);
    }

    /**
     * Gets a ZID (a constant UUID of an I followed by 32 zeros).
     *
     * @return Returns one ZID as a string
     */
    public static final String getZID() {
        return ZID;
    }

    /**
     * Gets a specific number of UUIDs.
     *
     * @param howMany number of UUIDs to retrieve.
     *
     * @return Returns a List of UUIDs.
     */
    public synchronized List getUUIDs(int howMany) {
        try {
            // check to see if we have enough UUIDs in the buffer to return
            if (howMany <= uuidBuffer.size()) {
                // yay! we can just return part or all of the buffer.
                if (howMany == uuidBuffer.size()) {
                    // return 'em all and clear the buffer (to prevent duplicates)
                    List uuids = new ArrayList(uuidBuffer);
                    uuidBuffer.clear();
    
                    return uuids;
                } else {
                    // we have less items requested than in our buffer
                    // so we'll get the first n items out of the buffer
                    List uuids = new ArrayList(uuidBuffer.subList(0, howMany));
    
                    // want to make sure they're not used again...
                    uuidBuffer.removeAll(uuids);
    
                    return uuids;
                }
            } else {
                // darn, we'll have to make one or more calls to refillBuffer()
                // in order to satisfy this request.
                List uuids = new ArrayList();
    
                // first empty the current buffer into the new list if it's 
                // not empty...
                if (!uuidBuffer.isEmpty()) {
                    uuids.addAll(uuidBuffer);
    
                    // then clear ...
                    uuidBuffer.clear();
                }
    
                // ...and refill the buffer
                refillBuffer();
    
                // used to determine how many more to get
                int remaining = howMany - uuids.size();
    
                while (remaining > 0) {
                    // now determine how many more UUIDs you need by calculating 
                    // the difference between the uuids.size() and howMany. Then
                    // check that against the current buffer size. Refill as
                    // necessary.
                    // e.g. if the difference between uuids.size() and howMany
                    // is 150 and the buffer size is 100, empty the whole buffer 
                    // and repeat the loop.  Next time, we'll only empty 50 uuids 
                    // from the buffer.
                    //
                    if (remaining >= uuidBuffer.size()) {
                        uuids.addAll(uuidBuffer);
                        uuidBuffer.clear();
                        refillBuffer();
                    } else {
                        List subList = new ArrayList(uuidBuffer.subList(0, remaining));
                        uuids.addAll(subList);
                        uuidBuffer.removeAll(subList);
                    }
    
                    // recalulate before we loop
                    remaining = howMany - uuids.size();
                }
    
                return uuids;
            }
        } catch ( UUIDException ex ) {
            logger.error("failed to get UUID: "+ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets the UUID service URL programmatically.
     *
     * @param u URL to the UUID service without the query string ('?count=X').
     *
     * @throws MalformedURLException if the URL is not valid.
     */
    public void setURL(URL u) throws MalformedURLException {
        if (u != null) {
            String urlString = u.toString();

            if (urlString.indexOf("?") > 0) {
                urlString = urlString.substring(0, urlString.indexOf("?"));
            }

            urlString = urlString + "?format=text&count=" + myBufferSize;

            URL newURL = new URL(urlString);
            url = newURL;
        }
    }

    /**
     * Refills the internal buffer.
     * 
     * <p>
     * This method attempts to connect and refill 10 times before quitting.
     * </p>
     *
     * @throws UUIDException if unable to refill the buffer.
     */
    private void refillBuffer() throws UUIDException {
        // buffer is empty, so we'll have to refill it
        boolean success = false;
        int tryCount = 1;

        // set a loop to keep trying until we succeed or until TRY_COUNT tries fail
        while (!success) {
            try {
                doRefill();
                success = true;
            } catch (IOException ioe) {
                // we tried, no luck, giving up on the service for now...
                if (tryCount == TRY_COUNT) {
                    try {
                        UUIDFactory factory = new UUIDFactory();
                        doRefill(factory);
                        success = true;
                    } catch (IOException ex1) {
                        logger.error(ex1);
                        throw new UUIDException("unable to connect after " +
                            TRY_COUNT + " tries to URL: " + url + ". (" +
                            ioe.getMessage() +
                            ") and failed to get uuids from the UUIDFactory (" +
                            ex1.getMessage() + ")");
                    } catch (InterruptedException ex1) {
                        logger.error(ex1);
                        throw new UUIDException("unable to connect after " +
                            TRY_COUNT + " tries to URL: " + url + ". (" +
                            ioe.getMessage() +
                            ") and failed to get uuids from the UUIDFactory (" +
                            ex1.getMessage() + ")");
                    } catch (UUIDException ex1) {
                        logger.error(ex1);
                        throw new UUIDException("unable to connect after " +
                            TRY_COUNT + " tries to URL: " + url + ". (" +
                            ioe.getMessage() +
                            ") and failed to get uuids from the UUIDFactory (" +
                            ex1.getMessage() + ")");
                    }
                } else {
                    success = false;
                    tryCount += 1;
                }

                // sleep between each try
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException ie) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Reads UUIDs from the supplied UUIDFactory instead of the URL connection.
     *
     * @param factory UUIDFactory to use.
     */
    private void doRefill(UUIDFactory factory) {
        uuidBuffer.clear();

        for (int i = 0; i < myBufferSize; i++)
            uuidBuffer.add(factory.getUUIDThreadSafe().toString());
    }

    /**
     * Performs the connection and reads UUIDs from the URLConnection
     * InputStream. The count of how many to refill is part of the URL
     * request; this method will keep reading uuids until there's no more
     * input.
     *
     * @throws IOException if an error occurs with the URL's InputStream.
     * @throws NullPointerException if URL is null.
     */
    private void doRefill() throws IOException {
        URLConnection connection = null;

        if (url != null) {
            connection = url.openConnection();
        } else {
            throw new NullPointerException("Invalid URL: '" + url + "'");
        }

        // create a buffered reader out of the InputStream for easy reading of UUIDs (one per line)
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(connection.getInputStream())));
        uuidBuffer.clear();

        String nextLine = reader.readLine();

        while ((nextLine != null) && !nextLine.equals("")) {
            uuidBuffer.add(nextLine.trim());
            nextLine = reader.readLine();
        }

        // there doesn't seem to be a way to disconnect from this URL
        // I'll assume the connection is closed when the InputStream is closed.
        reader.close();
    }

    /**
     * Main entry point into this class from a command-line invlpation.
     *
     * @param args command-line argument - how many UUIDs to generate
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: " + "UUIDGetter COUNT\n" +
                "  where\tCOUNT = number of UUIDs to retrieve");
            System.exit(0);
        }
    
        try {
            int count = Integer.parseInt(args[0]);
    
            // create the getter, try to get the list, and print out each on its own line
            UUIDGetter getter = UUIDGetter.getInstance();
            List uuids = getter.getUUIDs(count);
            Iterator iter = uuids.iterator();
    
            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
            
            // now try again with a known bad URL so the UUIDFactory backup is used
            System.out.println("got "+count+" uuids...trying again with a faulty URL..");
            getter = new UUIDGetter(new URL("http://uuid.ha.westgroup.com/UUID"));

            uuids = getter.getUUIDs(DEFAULT_BUFFER_SIZE+1);
            iter = uuids.iterator();
    
            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
        } catch (NumberFormatException nfe) {
            System.out.println("'" + args[0] + "' is not a number");
            System.exit(1);
        }
    }
}
