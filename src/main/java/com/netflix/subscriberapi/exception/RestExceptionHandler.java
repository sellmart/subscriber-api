package com.netflix.subscriberapi.exception;

import javassist.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIllegalArgumentException(final IllegalArgumentException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                exception.getMessage(),
                request.getDescription(false));
        return message;
    }

    @ExceptionHandler(value = {
            ResponseStatusException.class,
    })
    public ResponseEntity<ErrorMessage> handleResponseStatusException(final ResponseStatusException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                exception.getStatus().value(),
                LocalDateTime.now(),
                exception.getMessage(),
                request.getDescription(false));
        return ResponseEntity.status(exception.getStatus()).body(message);
    }

    @ExceptionHandler(value = {
            ServerErrorException.class,
            RuntimeException.class,
            Exception.class
    })
    public ResponseEntity<ErrorMessage> handleInternalException(final Exception exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                exception.getMessage(),
                request.getDescription(false));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler(value = SubscriberApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleSubscriberApiException(final SubscriberApiException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                exception.getMessage(),
                request.getDescription(false));
        return message;
    }

    @ExceptionHandler(value = {
            EntityNotFoundException.class,
            NotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false));
        return message;
    }

    @ExceptionHandler(value = NoContentException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ErrorMessage noContentException(NoContentException e, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NO_CONTENT.value(),
                LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false));
        return message;
    }
}
