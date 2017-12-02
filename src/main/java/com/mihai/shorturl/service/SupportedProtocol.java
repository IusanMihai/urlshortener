package com.mihai.shorturl.service;

import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * This contains the supported protocols for the service.
 *
 * @author Mihai Iusan
 */
public enum SupportedProtocol {
    HTTP("http"),
    HTTPS("https");

    private String protocol;

    SupportedProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    /**
     * Checks if the input starts with any of the supported protocols
     *
     * @param url the string/url to check
     * @return true if it starts with a supported protocol
     */
    public static boolean contains(String url) {
        return Arrays.stream(SupportedProtocol.values()).anyMatch(validProtocol -> StringUtils.startsWithIgnoreCase(url, validProtocol.getProtocol()));
    }
}