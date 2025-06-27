package org.yearup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CustomResourceNotFoundException is a custom runtime exception that signals
 * that a requested resource was not found. It is mapped to HTTP 404 Not Found
 * via the @ResponseStatus annotation, allowing Spring MVC to automatically
 * return the correct HTTP status when this exception is thrown in controllers.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomResourceNotFoundException extends RuntimeException{
    public CustomResourceNotFoundException() {
    }

    public CustomResourceNotFoundException(String message) {
        super(message);
    }

    public CustomResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
