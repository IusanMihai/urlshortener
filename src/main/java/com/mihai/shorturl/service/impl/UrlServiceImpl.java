package com.mihai.shorturl.service.impl;

import com.mihai.shorturl.entity.UrlEntity;
import com.mihai.shorturl.repository.UrlRepository;
import com.mihai.shorturl.service.HashService;
import com.mihai.shorturl.service.UrlService;
import com.mihai.shorturl.service.exception.InvalidURLException;
import com.mihai.shorturl.service.exception.UrlNotFoundException;
import com.mihai.shorturl.service.verifier.UrlVerifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UrlServiceImpl implements UrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlServiceImpl.class);
    //private static final String URL_ENCODE_REGEX = "[a-zA-Z0-9_~-]+$";
    private UrlRepository urlRepository;
    private HashService shortenService;
    private UrlVerifiers verifiers;

    @Autowired
    public UrlServiceImpl(HashService shortenService, UrlVerifiers verifiers, UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
        this.shortenService = shortenService;
        this.verifiers = verifiers;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UrlEntity> find(int offset, int limit) {
        int page = (int) Math.ceil(offset / limit);
        Pageable pageRequest = new PageRequest(page, limit);
        Page<UrlEntity> foundUrls = urlRepository.findAll(pageRequest);

        LOGGER.debug("Found {} URLs", foundUrls.getTotalElements());
        return foundUrls.getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public UrlEntity find(String shortUrl) {
        UrlEntity foundUrl = urlRepository.findByHash(shortUrl);
        if (foundUrl == null) {
            throw new UrlNotFoundException("URL \'" + shortUrl + "\' not found");
        }
        return foundUrl;
    }

    @Override
    @Transactional
    public UrlEntity create(String url) {
        LOGGER.debug("Request to shortenUrl: {}", url);
        UrlEntity resultUrl;

        boolean isSafeUrl = verifiers.isSafe(url);
        if (isSafeUrl) {
            UrlEntity existingUrl = urlRepository.findByUrl(url);
            if (existingUrl != null) {
                LOGGER.debug("URL {} already exists in database: {}", url, existingUrl);
                resultUrl = existingUrl;
            } else {
                resultUrl = createAndSaveUrl(url);
            }
            return resultUrl;
        } else {
            throw new InvalidURLException("URL " + url + " might pose a security risk, so we won't process it");
        }
    }

    @Override
    @Transactional
    public void deleteByHash(String hash) {
        LOGGER.info("Delete request for url with hash: ", hash);

        UrlEntity foundUrl = urlRepository.findByHash(hash);
        if (foundUrl == null) {
            throw new UrlNotFoundException("No url found for hash: " + hash);
        }

        urlRepository.delete(foundUrl);
        LOGGER.info("Deleted: {}", foundUrl);
    }

    @Override
    public String findUrlByHash(String hash) {
        String url = null;

        LOGGER.trace("Retrieving url for the hash: ", hash);
        UrlEntity foundUrl = urlRepository.findByHash(hash);
        if (foundUrl == null) {
            LOGGER.info("No url found for hash: {}", hash);
        } else {
            LOGGER.debug("Found url corresponding to the hash: {} is {}", foundUrl);
            url = foundUrl.getUrl();
        }

        return url;
    }

    private UrlEntity createAndSaveUrl(String url) {
        final String hash = shortenService.shorten(url);
        UrlEntity savedUrl = urlRepository.save( new UrlEntity(url, hash));

        LOGGER.debug("Successfully created new url: {}", savedUrl);
        return savedUrl;
    }
}
