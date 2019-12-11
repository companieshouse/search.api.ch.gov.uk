package uk.gov.companieshouse.search.api.exception;

import java.io.IOException;

/**
 * UpsertException is a wrapper exception that hides
 * lower level exceptions from the caller and prevents them
 * from being propagated up the call stack.
 */
public class UpsertException extends IOException {

    /**
     * Constructs a new UpsertException with a custom message.
     *
     * @param message a custom message
     */
    public UpsertException(String message) {
        super(message);
    }

    /**
     * Constructs a new UpsertException with a custom message and the specified
     * cause.
     *
     * @param message a custom message
     * @param cause the cause
     */
    public UpsertException(String message, Throwable cause) {
        super(message, cause);
    }
}

