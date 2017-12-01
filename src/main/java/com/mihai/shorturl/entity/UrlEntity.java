package com.mihai.shorturl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

@ApiModel(description = "URL Entity")
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "urls")
public class UrlEntity implements Serializable {

    public static final int DEFAULT_VALID_DAYS = 7;
    public static final int MAX_URL_LENGTH = 2084;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private long id;

    @Column(name = "url", nullable = false, unique = true, length = MAX_URL_LENGTH)
    @ApiModelProperty(
            value = "The full URL",
            example = "https://duckduckgo.com",
            required = true,
            readOnly = true)
    private String url;

    @Column(name = "key", nullable = false, unique = true)
    @ApiModelProperty(
            value = "The shortened URL",
            example = "t15px8",
            required = true,
            readOnly = true)
    private String key;

    @JsonIgnore
    @Column(name = "expirationDate", nullable = false)
    @ApiModelProperty(hidden = true)
    private LocalDate expirationDate;

    @JsonIgnore
    @CreatedDate
    @ApiModelProperty(hidden = true)
    private ZonedDateTime createdDate;

    @JsonIgnore
    @LastModifiedDate
    @ApiModelProperty(hidden = true)
    private ZonedDateTime modifiedDate;

    @JsonIgnore
    @Version
    private long version;

    /**
     * Empty constructor for {@link UrlEntity}.
     */
    public UrlEntity() {
    }

    public UrlEntity(@NotNull final String url, @NotNull final String key) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(key);

        this.key = key;
        this.url = url;
        this.expirationDate = LocalDate.now().plusDays(DEFAULT_VALID_DAYS);
    }

    public UrlEntity(@NotNull final String url, @NotNull final String key, @NotNull final LocalDate expirationDate) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(key);
        Objects.requireNonNull(expirationDate);

        this.key = key;
        this.url = url;
        this.expirationDate = expirationDate;
    }

    public long getId() {
        return this.id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.url, this.key);
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
        return Objects.equals(this.id, other.id) && Objects.equals(this.url, other.url) && Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        return "UrlEntity { " +
                "id=" + Objects.toString(this.id) +
                ", url='" + Objects.toString(this.url) +
                ", key='" + Objects.toString(this.key) +
                ", createdDate=" + Objects.toString(this.createdDate) +
                ", modifiedDate=" + Objects.toString(this.modifiedDate) + '}';
    }


}