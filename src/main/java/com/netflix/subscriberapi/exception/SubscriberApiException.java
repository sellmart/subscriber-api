package com.netflix.subscriberapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import java.util.List;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class SubscriberApiException extends RuntimeException {
    private HttpStatus statusCode;
    private String thrownByMethod;
    private String[] thrownByMethodArgs;
    private List<ObjectError> errors;

    /**
     * Create an instance with the specified message and HttpStatus.INTERNAL_SERVER_ERROR.
     *
     * @param statusMessage
     */
    public SubscriberApiException(final String statusMessage) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, statusMessage);
    }

    /**
     * Create an instance with the specified status code and message.
     *
     * @param statusCode
     * @param statusMessage
     */
    public SubscriberApiException(final HttpStatus statusCode, final String statusMessage) {
        super(statusMessage);
        this.statusCode = statusCode;
    }

    /**
     * Create an instance with the specified status code, message and wrap the exception.
     *
     * @param statusCode
     * @param statusMessage
     * @param e
     */
    public SubscriberApiException(final HttpStatus statusCode, final String statusMessage, final Exception e) {
        super(statusMessage, e);
        this.statusCode = statusCode;
    }

    /**
     * Create an instance with the specified message and exception.
     * Sets the status code to HttpStatus.INTERNAL_SERVER_ERROR.
     *
     * @param statusMessage
     * @param e
     */
    public SubscriberApiException(final String statusMessage, final Exception e) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, statusMessage, e);
    }

    /**
     * Creates an instance with a message "Validation error", using the list of errors.
     *
     * @param statusCode
     * @param errorList
     */
    public SubscriberApiException(final HttpStatus statusCode, final List<ObjectError> errorList) {
        super("Validation error");
        this.statusCode = statusCode;
        this.errors = errorList;
    }
}
