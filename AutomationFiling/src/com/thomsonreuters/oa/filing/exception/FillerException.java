package com.thomsonreuters.oa.filing.exception;

public class FillerException extends Exception{

	/**
     * Instantiates a new validation exception.
     */
    public FillerException() { super(); }

    /**
     * Instantiates a new validation exception.
     *
     * @param message the message
     */
    public FillerException(String message) { super(message); }

    /**
     * Instantiates a new validation exception.
     *
     * @param cause the cause
     */
    public FillerException(Throwable cause) { super(cause); }

    /**
     * Instantiates a new validation exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public FillerException(String message, Throwable cause) { super(message, cause); }
}
