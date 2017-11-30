package com.mihai.shorturl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "URL")
public class UrlEntity implements Serializable {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "url", nullable = false, unique = true)
    private String url;
    @Column(name = "hash", nullable = false, unique = true)
    private String hash;
    @JsonIgnore
    @CreatedDate
    private ZonedDateTime createdDate;
    @JsonIgnore
    @LastModifiedDate
    private ZonedDateTime modifiedDate;
    @JsonIgnore
    @Version
    private long version;

    public UrlEntity() {
    }

    public UrlEntity(final String url, final String hash) {
        this.hash = hash;
        this.url = url;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(ZonedDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getVersion() {
        return this.version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.url, this.hash);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final UrlEntity other = (UrlEntity) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.url, other.url) && Objects.equals(this.hash, other.hash);
    }

    @Override
    public String toString() {
        return "UrlEntity { " +
                "id=" + Objects.toString(this.id) +
                ", url='" + Objects.toString(this.url) +
                ", hash='" + Objects.toString(this.hash) +
                ", createdDate=" + Objects.toString(this.createdDate) +
                ", modifiedDate=" + Objects.toString(this.modifiedDate) + '}';
    }
}