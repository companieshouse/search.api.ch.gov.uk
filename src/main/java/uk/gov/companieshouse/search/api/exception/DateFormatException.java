package uk.gov.companieshouse.search.api.exception;

/**
 * DateFormatException is a wrapper exception that hides
 * lower level exceptions from the caller and prevents them
 * from being propagated up the call stack.
 */
public class DateFormatException extends Exception {

    /**
     * Constructs a new DateException with a custom message.
     *
     * @param message a custom message
     */
    public DateFormatException(String message) {
        super(message);
    }
}
