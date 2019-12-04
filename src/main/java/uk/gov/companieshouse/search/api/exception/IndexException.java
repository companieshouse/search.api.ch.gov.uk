package uk.gov.companieshouse.search.api.exception;

import java.io.IOException;

/**
 * IndexException is a wrapper exception that hides
 * lower level exceptions from the caller and prevents them
 * from being propagated up the call stack.
 */
public class IndexException extends IOException {

    /**
     * Constructs a new IndexException with a custom message.
     *
     * @param message a custom message
     */
    public IndexException(String message) {
        super(message);
    }

    /**
     * Constructs a new IndexException with a custom message and the specified
     * cause.
     *
     * @param message a custom message
     * @param cause the cause
     */
    public IndexException(String message, Throwable cause) {
        super(message, cause);
    }
}
