package it.dinein.api.dineinapi.exception;

import org.springframework.http.HttpStatus;

public class NotAnImageFileException extends Exception {
    public NotAnImageFileException(HttpStatus badRequest, String message) {
        super(message);
    }
}
