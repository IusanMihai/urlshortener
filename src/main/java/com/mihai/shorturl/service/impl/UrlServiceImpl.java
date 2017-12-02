package com.mihai.shorturl.service.impl;

import com.mihai.shorturl.entity.UrlEntity;
import com.mihai.shorturl.repository.UrlRepository;
import com.mihai.shorturl.service.KeyService;
import com.mihai.shorturl.service.SupportedProtocol;
import com.mihai.shorturl.service.UrlService;
import com.mihai.shorturl.service.exception.InvalidURLException;
import com.mihai.shorturl.service.exception.UrlNotFoundException;
import com.mihai.shorturl.service.exception.UrlNotSavedException;
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

/**
 * This is the URL service which is responsible to create save and find entities from the UrlRepository
 *
 * @author Mihai Iusan
 */
@Service
public class UrlServiceImpl implements UrlService {
    private static final Logger logger = LoggerFactory.getLogger(UrlServiceImpl.class);
    private UrlRepository urlRepository;
    private KeyService shortenService;
    private UrlVerifiers verifiers;

    @Autowired
    public UrlServiceImpl(KeyService shortenService, UrlVerifiers verifiers, UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
        this.shortenService = shortenService;
        this.verifiers = verifiers;
    }

    /**
     * Gets all the items from database base on the offset and the limit
     *
     * @param offset the offset
     * @param limit  the limit
     * @return the list of UrlEntities
     */
    @Override
    @Transactional(readOnly = true)
    public List<UrlEntity> find(int offset, int limit) {
        int page = (int) Math.ceil(offset / limit);
        Pageable pageRequest = new PageRequest(page, limit);
        Page<UrlEntity> foundUrls = urlRepository.findAll(pageRequest);

        logger.debug("Found {} URLs", foundUrls.getTotalElements());
        return foundUrls.getContent();
    }

    /**
     * Find UrlEntity by key
     *
     * @param key the key
     * @return the URL entity
     */
    @Override
    @Transactional(readOnly = true)
    public UrlEntity find(String key) {
        UrlEntity foundUrl = urlRepository.findByKey(key);
        if (foundUrl == null || isUrlExpired(foundUrl)) {
            throw new UrlNotFoundException("URL \'" + key + "\' not found");
        }
        return foundUrl;
    }

    /**
     * Create a new URL entry in the db.
     * <p>
     * Note: If it already exists, it will retrieve the old one and update the expiry date if needed
     * Note: If
     *
     * @param url the url
     * @return new UrlEntity
     */
    @Override
    @Transactional
    public UrlEntity create(@NotNull String url) {
        logger.debug("Request to shortenUrl: {}", url);
        UrlEntity resultUrl;

        String newUrl = url.trim();
        // Check if the URL is longer then the max size that can be saved to DB
        if (newUrl.length() > UrlEntity.MAX_URL_LENGTH) {
            throw new InvalidURLException("URL is too long, max supported size is: " + UrlEntity.MAX_URL_LENGTH);
        }

        // Check if the protocol is supported
        boolean isSupportedProtocol = SupportedProtocol.contains(url);
        if (!isSupportedProtocol) {
            throw new InvalidURLException("URL protocol not supported");
        }
        boolean isSafeUrl = verifiers.isSafe(newUrl);
        if (isSafeUrl) {
            UrlEntity existingUrl = urlRepository.findByUrl(newUrl);
            if (existingUrl != null) {
                logger.debug("URL {} already exists in database: {}", newUrl, existingUrl);
                LocalDate validityPeriod = LocalDate.now().plusDays(UrlEntity.DEFAULT_VALID_DAYS);
                LocalDate expirationDate = existingUrl.getExpirationDate();
                if (validityPeriod.isAfter(expirationDate)) {
                    existingUrl = updateExpirationDateAndSaveUrl(existingUrl, validityPeriod);
                }
                resultUrl = existingUrl;
            } else {
                resultUrl = createAndSaveUrl(newUrl);
            }
            return resultUrl;
        } else {
            throw new InvalidURLException("URL " + newUrl + " might pose a security risk, so we won't process it");
        }
    }

    /**
     * Delete operation by key for the URL entry
     *
     * @param key the key
     */
    @Override
    @Transactional
    public void deleteByKey(String key) {
        // Not in use
        logger.info("Delete request for url with key: ", key);

        UrlEntity foundUrl = urlRepository.findByKey(key);
        if (foundUrl == null) {
            throw new UrlNotFoundException("No url found for key: " + key);
        }

        urlRepository.delete(foundUrl);
        logger.info("Deleted: {}", foundUrl);
    }

    /**
     * Find a URL by key
     *
     * @param key the key
     * @return the found URL or NULL
     */
    @Override
    public String findUrlByKey(String key) {
        logger.trace("Retrieving url for the key: ", key);

        String result = null;
        UrlEntity foundUrl = urlRepository.findByKey(key);

        if (foundUrl != null && !isUrlExpired(foundUrl)) {
            logger.debug("Found url corresponding to the key: {} is {}", foundUrl);
            result = foundUrl.getUrl();
        } else {
            logger.info("No url found for key: {}", key);
        }

        return result;
    }

    /**
     * Creates and saves a new URL entry
     * <p>
     * Note: If there is an expired entry in db, it will be overwritten with the new URL. This is done to reuse rows
     *
     * @param url the url to save
     * @return the saved entry
     */
    private UrlEntity createAndSaveUrl(@NotNull String url) {
        UrlEntity urlToSave;

        // Get entries that expired and overwrite the entry in the database
        UrlEntity foundUrl = urlRepository.findFirstByExpirationDateBefore(LocalDate.now());
        if (foundUrl != null) {
            foundUrl.setUrl(url);
            foundUrl.setExpirationDate(UrlEntity.getDefaultExpirationDate());
            urlToSave = foundUrl;
        } else {
            String key = getKey(url);

            urlToSave = new UrlEntity(url, key);
        }
        UrlEntity savedUrl = urlRepository.save(urlToSave);

        logger.debug("Successfully created new url: {}", savedUrl);
        return savedUrl;
    }

    /**
     * Get unique key to store for the url
     * <p>
     * Note: if there is a database collision with the key, it will try to generate a new one
     * Note: if it cannot generate the key in max 10 tries, it will throw an exception
     *
     * @param url the url to shorten
     * @return the url key
     */
    private String getKey(String url) {
        // shorten the url
        String key = shortenService.shorten(url);

        // Do database check to prevent key collisions
        // while key exists in the database, generate a new key by adding extra empty spaces at the end to the url
        int maxTries = 10;
        while (urlRepository.findByKey(key) != null) {
            logger.debug("key {} already exists in database, generating a new one", key);
            maxTries--;
            url = url.concat(" ");
            key = shortenService.shorten(url);
            if (maxTries <= 10)
                throw new UrlNotSavedException("Could not save the URL");
        }
        return key;
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

        logger.debug("Successfully updated expiration date for url: {}", savedUrl);
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
