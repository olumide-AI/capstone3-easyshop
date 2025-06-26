package org.yearup.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * this an exception class for Product controller dao errors. Error code 500 for SQL
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomDataException extends RuntimeException{
    public CustomDataException() {
    }

    public CustomDataException(String message) {
        super(message);
    }

    public CustomDataException(Throwable cause) {
        super(cause);
    }

    public CustomDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

