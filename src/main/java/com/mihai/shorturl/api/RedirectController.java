package com.mihai.shorturl.api;

import com.mihai.shorturl.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Component
@Controller
@RequestMapping("/redirect")
public class RedirectController {
    private UrlService urlService;

    @Autowired
    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }


    @GetMapping(value = "/{key}")
    public ModelAndView redirect(@PathVariable("key") String key) {
        final String url = urlService.findUrlByKey(key);
        return new ModelAndView("redirect:" + url);
    }
}