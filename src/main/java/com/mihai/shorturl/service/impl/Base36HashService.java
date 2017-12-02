package com.mihai.shorturl.service.impl;

import com.mihai.shorturl.service.KeyService;
import com.mihai.shorturl.service.SupportedProtocol;
import com.mihai.shorturl.service.exception.InvalidURLException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class Base36HashService implements KeyService {
    private static final int RADIX = 36;
    private static final String PIPE = "-";

    @Override
    public String shorten(String url) {
        return encode(url);
    }

    private String encode(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new InvalidURLException("Supplied invalid url: empty");
        }

        String hexValue = Integer.toString(url.hashCode(), RADIX);
        if (hexValue.startsWith(PIPE)) {
            hexValue = hexValue.substring(1);
        }


        return hexValue;
    }

}