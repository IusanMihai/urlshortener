package com.mihai.shorturl.service;

import com.mihai.shorturl.entity.UrlEntity;

import java.util.List;

public interface UrlService {
    List<UrlEntity> find(int offset, int limit);

    UrlEntity find(String shortUrl);

    UrlEntity create(String url);

    String findUrlByKey(String key);

    void deleteByKey(String key);
}