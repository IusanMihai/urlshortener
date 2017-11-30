package com.mihai.shorturl.repository;

import com.mihai.shorturl.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    UrlEntity findByHash(String hash);

    UrlEntity findByUrl(String url);

}