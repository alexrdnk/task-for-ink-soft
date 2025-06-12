package com.example.localnews_backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    private String source;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "local_hint")
    private boolean localHint;

    private String city;
    private String stateCode;

    // standard getters & setters (or use Lombok @Getter/@Setter)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public boolean isLocalHint() { return localHint; }
    public void setLocalHint(boolean localHint) { this.localHint = localHint; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
}
