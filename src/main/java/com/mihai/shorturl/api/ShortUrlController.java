package com.mihai.shorturl.api;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
public class ShortUrlController {

    @RequestMapping(value = "/shorten", method = RequestMethod.GET)
    public String shorten(@RequestParam(value = "longUrl", defaultValue = "World") String name) {
        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(name))
            return name;
        return null;
    }

    @RequestMapping(value = "/enlarge", method = RequestMethod.GET)
    public String enlarge(@RequestParam(value = "longUrl", defaultValue = "World") String name) {
        String enlargerUrl;
        enlargerUrl = name;

        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(enlargerUrl))
            return enlargerUrl;
        return null;
    }
}
