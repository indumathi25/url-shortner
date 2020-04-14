package com.urlshortener.url;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UrlRepository extends JpaRepository<UrlEntity, String> {
    List<UrlEntity> findByHashUrl(String hashUrl);
}
