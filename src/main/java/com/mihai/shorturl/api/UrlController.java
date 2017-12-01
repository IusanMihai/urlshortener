package com.mihai.shorturl.api;

import com.mihai.shorturl.entity.UrlEntity;
import com.mihai.shorturl.service.UrlService;
import io.swagger.annotations.ApiOperation;
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

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Shorten a URL")
    public ResponseEntity<UrlEntity> shortenUrl(@RequestParam(value = "longUrl", defaultValue = "https://www.google.com/") String url) {
        logger.debug("Shorten request for: {}", url);
        UrlEntity urlEntity = urlService.create(url);
        logger.debug("Returning url: {}", urlEntity.getKey());
        return new ResponseEntity<>(urlEntity, HttpStatus.OK);
    }

    @GetMapping(
            path = "/",
            consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Retrieve a shortened URL")
    public ResponseEntity<UrlEntity> enlargeUrl(@RequestParam(value = "shortUrl", defaultValue = "ffxgil") String shortUrl) {
        UrlEntity urlEntity = urlService.find(shortUrl);
        return new ResponseEntity<>(urlEntity, HttpStatus.OK);
    }
}
