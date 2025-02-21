package de.tum.cit.aet.thesis.exception;

/**
 * Exception thrown when data retention operations fail.
 * This provides specific error handling for data retention related issues.
 */
public class DataRetentionException extends RuntimeException {

    /**
     * Creates a new DataRetentionException with the specified message.
     *
     * @param message the detail message
     */
    public DataRetentionException(String message) {
        super(message);
    }

    /**
     * Creates a new DataRetentionException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DataRetentionException(String message, Throwable cause) {
        super(message, cause);
    }
}