import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Testing {

	Properties prop = new Properties();
	String propFileName = "ErrorMsg.properties";

	public static void main(String[] args) throws IOException {

		Testing test=new Testing();
	}

Testing() throws IOException {
		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);

		prop.load(inputStream);
		System.out.println(prop.getProperty("PhoneRequestCmt"));
	}

}
