package com.webtopdf.repository;

import com.webtopdf.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {

    /** Find all papers generated for a given URL (cache / duplicate detection). */
    List<Paper> findByUrlOrderByCreatedAtDesc(String url);

    /** Find all papers for a given user (Phase 2 — history dashboard). */
    List<Paper> findByUserIdOrderByCreatedAtDesc(Long userId);
}
