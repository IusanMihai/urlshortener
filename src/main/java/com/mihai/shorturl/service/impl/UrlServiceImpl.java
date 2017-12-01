package com.mihai.shorturl.service.impl;

import com.mihai.shorturl.entity.UrlEntity;
import com.mihai.shorturl.repository.UrlRepository;
import com.mihai.shorturl.service.KeyService;
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

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Service
public class UrlServiceImpl implements UrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlServiceImpl.class);
    //private static final String URL_ENCODE_REGEX = "[a-zA-Z0-9_~-]+$";
    private UrlRepository urlRepository;
    private KeyService shortenService;
    private UrlVerifiers verifiers;

    @Autowired
    public UrlServiceImpl(KeyService shortenService, UrlVerifiers verifiers, UrlRepository urlRepository) {
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
        UrlEntity foundUrl = urlRepository.findByKey(shortUrl);
        if (foundUrl == null || isUrlExpired(foundUrl)) {
            throw new UrlNotFoundException("URL \'" + shortUrl + "\' not found");
        }
        return foundUrl;
    }

    @Override
    @Transactional
    public UrlEntity create(@NotNull String url) {
        LOGGER.debug("Request to shortenUrl: {}", url);
        UrlEntity resultUrl;

        String newUrl = url.trim();

        boolean isSafeUrl = verifiers.isSafe(newUrl);
        if (isSafeUrl) {
            UrlEntity existingUrl = urlRepository.findByUrl(newUrl);

            if (existingUrl != null) {
                LOGGER.debug("URL {} already exists in database: {}", newUrl, existingUrl);
                LocalDate validityPeriod = LocalDate.now().plusDays(UrlEntity.DEFAULT_VALID_DAYS);
                LocalDate expirationDate = existingUrl.getExpirationDate();
                if (validityPeriod.isAfter(expirationDate)) {
                    existingUrl = updateExpirationDateAndSaveUrl(existingUrl, validityPeriod);
                }
                resultUrl = existingUrl;
            } else {
                //TODO check if there is an expired URL, if there is, overwrite the row with new values
                resultUrl = createAndSaveUrl(newUrl);
            }
            return resultUrl;
        } else {
            throw new InvalidURLException("URL " + newUrl + " might pose a security risk, so we won't process it");
        }
    }

    @Override
    @Transactional
    public void deleteByKey(String key) {
        LOGGER.info("Delete request for url with key: ", key);

        UrlEntity foundUrl = urlRepository.findByKey(key);
        if (foundUrl == null) {
            throw new UrlNotFoundException("No url found for key: " + key);
        }

        urlRepository.delete(foundUrl);
        LOGGER.info("Deleted: {}", foundUrl);
    }

    @Override
    public String findUrlByKey(String key) {
        String url = null;

        LOGGER.trace("Retrieving url for the key: ", key);
        UrlEntity foundUrl = urlRepository.findByKey(key);
        if (foundUrl == null) {
            LOGGER.info("No url found for key: {}", key);
        } else {
            LOGGER.debug("Found url corresponding to the key: {} is {}", foundUrl);
            url = foundUrl.getUrl();
        }

        return url;
    }

    private UrlEntity createAndSaveUrl(@NotNull String url) {
        final String key = shortenService.shorten(url);
        UrlEntity savedUrl = urlRepository.save(new UrlEntity(url, key));

        LOGGER.debug("Successfully created new url: {}", savedUrl);
        return savedUrl;
    }

    /**
     * Update the epiration date and save the new entity.
     *
     * @param urlEntity      the entity to change the expiration date
     * @param expirationDate the new expiration date
     * @return the saved entity
     */
    private UrlEntity updateExpirationDateAndSaveUrl(@NotNull UrlEntity urlEntity, @NotNull LocalDate expirationDate) {
        urlEntity.setExpirationDate(expirationDate);
        UrlEntity savedUrl = urlRepository.save(urlEntity);

        LOGGER.debug("Successfully updated expiration date for url: {}", savedUrl);
        return savedUrl;
    }

    /**
     * If the URL is before
     *
     * @param urlEntity the url entity to check
     * @return true if the url expiration date is before today
     */
    private boolean isUrlExpired(@NotNull UrlEntity urlEntity) {
        return urlEntity.getExpirationDate().isBefore(LocalDate.now());
    }

}
