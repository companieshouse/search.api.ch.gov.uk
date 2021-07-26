package uk.gov.companieshouse.search.api.exception;

/**
 * MappingException is a wrapper exception that hides
 * lower level exceptions from the caller and prevents them
 * from being propagated up the call stack.
 */
public class MappingException extends Exception {

    /**
     * Constructs a new MappingException with a custom message.
     *
     * @param message a custom message
     */
    public MappingException(String message) {
        super(message);
    }
}
