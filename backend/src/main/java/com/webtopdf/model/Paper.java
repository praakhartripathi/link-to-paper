package com.webtopdf.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * JPA entity backed by the `papers` table.
 * Mirrors the schema defined in V1__create_papers_table.sql.
 */
@Entity
@Table(name = "papers")
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(name = "abstract_text", columnDefinition = "LONGTEXT")
    private String abstractText;

    @Column(columnDefinition = "LONGTEXT")
    private String introduction;

    @Column(columnDefinition = "LONGTEXT")
    private String methodology;

    @Column(columnDefinition = "LONGTEXT")
    private String discussion;

    @Column(columnDefinition = "LONGTEXT")
    private String conclusion;

    @Column(name = "references_json", columnDefinition = "LONGTEXT")
    private String referencesJson;

    @Column(name = "pdf_size_bytes")
    private Integer pdfSizeBytes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "user_id")
    private Long userId;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Paper() {}

    private Paper(Builder b) {
        this.url            = b.url;
        this.title          = b.title;
        this.abstractText   = b.abstractText;
        this.introduction   = b.introduction;
        this.methodology    = b.methodology;
        this.discussion     = b.discussion;
        this.conclusion     = b.conclusion;
        this.referencesJson = b.referencesJson;
        this.pdfSizeBytes   = b.pdfSizeBytes;
        this.userId         = b.userId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String url, title, abstractText, introduction,
                       methodology, discussion, conclusion, referencesJson;
        private Integer pdfSizeBytes;
        private Long userId;

        public Builder url(String v)            { this.url = v; return this; }
        public Builder title(String v)          { this.title = v; return this; }
        public Builder abstractText(String v)   { this.abstractText = v; return this; }
        public Builder introduction(String v)   { this.introduction = v; return this; }
        public Builder methodology(String v)    { this.methodology = v; return this; }
        public Builder discussion(String v)     { this.discussion = v; return this; }
        public Builder conclusion(String v)     { this.conclusion = v; return this; }
        public Builder referencesJson(String v) { this.referencesJson = v; return this; }
        public Builder pdfSizeBytes(Integer v)  { this.pdfSizeBytes = v; return this; }
        public Builder userId(Long v)           { this.userId = v; return this; }
        public Paper build()                    { return new Paper(this); }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long    getId()            { return id; }
    public String  getUrl()           { return url; }
    public String  getTitle()         { return title; }
    public String  getAbstractText()  { return abstractText; }
    public String  getIntroduction()  { return introduction; }
    public String  getMethodology()   { return methodology; }
    public String  getDiscussion()    { return discussion; }
    public String  getConclusion()    { return conclusion; }
    public String  getReferencesJson(){ return referencesJson; }
    public Integer getPdfSizeBytes()  { return pdfSizeBytes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public Long    getUserId()        { return userId; }
}
