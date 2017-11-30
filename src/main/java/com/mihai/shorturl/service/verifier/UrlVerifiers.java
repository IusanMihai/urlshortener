package com.mihai.shorturl.service.verifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UrlVerifiers {
    private final List<UrlVerifier> verifiers;

    @Autowired
    public UrlVerifiers(List<UrlVerifier> vrfrs) {
        this.verifiers = vrfrs;
    }

    public boolean isSafe(String url) {
        boolean safe = true;

        for (UrlVerifier verifier : verifiers) {
            if (verifier != null) {
                boolean isValidByProvider = verifier.isSafe(url);
                if (!isValidByProvider) {
                    safe = false;
                    break;
                }
            }
        }

        return safe;
    }
}
