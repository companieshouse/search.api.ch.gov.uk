package uk.gov.companieshouse.search.api.exception;

/**
 * This class {@code EndpointException} is a form of {@link RuntimeException}
 * that is thrown if there are errors with the elastic search instance endpoint
 * the application would not be able to function without the elastic search instance therefore
 * this exception is fatal
 */
public class EndpointException extends RuntimeException {

    /**
     * Constructs a new EndpointException with a custom message.
     *
     * @param message a custom message
     */
    public EndpointException(String message) {
        super(message);
    }
}
