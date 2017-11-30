package com.mihai.shorturl.api;

import com.mihai.shorturl.entity.UrlEntity;
import com.mihai.shorturl.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
public class UrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    private final UrlService service;

    @Autowired
    public UrlController(UrlService srvc) {
        this.service = srvc;
    }


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity shortenUrl(@RequestParam(value = "longUrl", defaultValue = "World") String url) {
        LOGGER.debug("Shorten request for: {}", url);
        UrlEntity urlEntity;
        urlEntity = service.create(url);
        LOGGER.debug("Returning url: {}", urlEntity.getHash());
        return new ResponseEntity(urlEntity, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity enlargeUrl(@RequestParam(value = "shortUrl", defaultValue = "World") String shortUrl) {
        UrlEntity urlEntity;
        urlEntity = service.find(shortUrl);
        return new ResponseEntity(urlEntity, HttpStatus.OK);
    }
}
