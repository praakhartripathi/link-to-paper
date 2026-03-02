-- ============================================================
-- V1__create_papers_table.sql
-- Initial schema for Link2Paper application (MySQL)
-- ============================================================

CREATE TABLE IF NOT EXISTS papers (
    id               BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    url              TEXT            NOT NULL,
    title            VARCHAR(512)    NOT NULL,
    abstract_text    LONGTEXT,
    introduction     LONGTEXT,
    methodology      LONGTEXT,
    discussion       LONGTEXT,
    conclusion       LONGTEXT,
    references_json  LONGTEXT,
    pdf_size_bytes   INT,
    created_at       DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    user_id          BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index for fast lookups by URL (duplicate detection / caching)
CREATE INDEX idx_papers_url        ON papers (url(255));
-- Index for recent papers dashboard (Phase 2)
CREATE INDEX idx_papers_created_at ON papers (created_at DESC);
-- Index for per-user paper history (Phase 2)
CREATE INDEX idx_papers_user_id    ON papers (user_id);
