package com.mihai.shorturl.service.verifier;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UrlValidatorVerifier implements UrlVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlValidatorVerifier.class);

    @Override
    public final boolean isSafe(final String url) {
        LOGGER.debug("Validating URL: " + url);
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }
}
