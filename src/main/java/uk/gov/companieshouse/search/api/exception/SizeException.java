package uk.gov.companieshouse.search.api.exception;

import java.io.IOException;

/**
 * SizeException is a wrapper exception that hides
 * lower level exceptions from the caller and prevents them
 * from being propagated up the call stack.
 */
public class SizeException extends IOException {

    /**
     * Constructs a new SizeException with a custom message.
     *
     * @param message a custom message
     */
    public SizeException(String message) {
        super(message);
    }

    /**
     * Constructs a new SizeException with a custom message and the specified
     * cause.
     *
     * @param message a custom message
     * @param cause the cause
     */
    public SizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
