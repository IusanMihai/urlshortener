package com.mihai.shorturl.service.exception;

public class UrlException extends RuntimeException {

    public UrlException(String message) {
        super(message);
    }

    public UrlException(String message, Throwable cause) {
        super(message, cause);
    }

}