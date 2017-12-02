package com.mihai.shorturl.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UrlNotSavedException extends UrlException {

    public UrlNotSavedException(String message) {
        super(message);
    }

    public UrlNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }
}
