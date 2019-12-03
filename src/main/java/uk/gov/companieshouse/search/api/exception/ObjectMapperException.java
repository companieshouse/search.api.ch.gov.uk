package uk.gov.companieshouse.search.api.exception;

import java.io.IOException;

/**
 * ObjectMapperException is a wrapper exception that hides
 * lower level exceptions from the caller and prevents them
 * from being propagated up the call stack.
 */
public class ObjectMapperException extends IOException {

    /**
     * Constructs a new ObjectMapperException with a custom message.
     *
     * @param message a custom message
     */
    public ObjectMapperException(String message) {
        super(message);
    }

    /**
     * Constructs a new ObjectMapperException with a custom message and the specified
     * cause.
     *
     * @param message a custom message
     * @param cause the cause
     */
    public ObjectMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
