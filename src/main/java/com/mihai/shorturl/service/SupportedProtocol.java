package com.mihai.shorturl.service;

import org.springframework.util.StringUtils;

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

    public static boolean contains(String prtcl) {
        boolean supported = false;
        for (SupportedProtocol validProtocol : SupportedProtocol.values()) {
            if (StringUtils.startsWithIgnoreCase(prtcl, validProtocol.getProtocol())) {
                supported = true;
                break;
            }
        }
        return supported;
    }
}