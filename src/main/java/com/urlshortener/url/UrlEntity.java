package com.urlshortener.url;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    String url;

    String hashUrl;

    public UrlEntity(){}

    public UrlEntity(String hashUrl, String url) {
        this.url = url;
        this.hashUrl = hashUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHashUrl() {
        return hashUrl;
    }

    public void setHashUrl(String hashUrl) {
        this.hashUrl = hashUrl;
    }
}
