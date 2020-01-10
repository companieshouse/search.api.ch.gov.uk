package uk.gov.companieshouse.search.api.exception;

import java.net.MalformedURLException;

/**
 * EndpointException is a wrapper exception that hides
 * lower level exceptions from the caller and prevents them
 * from being propagated up the call stack.
 */
public class EndpointException extends MalformedURLException {

    /**
     * Constructs a new EndpointException with a custom message.
     *
     * @param message a custom message
     */
    public EndpointException(String message) {
        super(message);
    }
}
