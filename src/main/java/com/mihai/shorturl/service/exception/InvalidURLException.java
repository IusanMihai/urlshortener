package com.mihai.shorturl.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidURLException extends UrlException {

    public InvalidURLException(String message) {
        super(message);
    }

    public InvalidURLException(String message, Throwable cause) {
        super(message, cause);
    }
}
