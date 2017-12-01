package com.mihai.shorturl.api;

import com.mihai.shorturl.entity.UrlEntity;
import com.mihai.shorturl.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
public class UrlController {
    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    private final UrlService service;

    @Autowired
    public UrlController(UrlService srvc) {
        this.service = srvc;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UrlEntity> shortenUrl(@RequestParam(value = "longUrl", defaultValue = "World") String url) {
        logger.debug("Shorten request for: {}", url);
        UrlEntity urlEntity = service.create(url);
        logger.debug("Returning url: {}", urlEntity.getHash());
        return new ResponseEntity<>(urlEntity, HttpStatus.OK);
    }

    @GetMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UrlEntity> enlargeUrl(@RequestParam(value = "shortUrl", defaultValue = "World") String shortUrl) {
        UrlEntity urlEntity = service.find(shortUrl);
        return new ResponseEntity<>(urlEntity, HttpStatus.OK);
    }
}
