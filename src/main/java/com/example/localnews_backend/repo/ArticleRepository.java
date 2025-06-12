package com.example.localnews_backend.repo;

import com.example.localnews_backend.model.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findTop20ByLocalHintFalseOrderByPublishedAtDesc();
    List<Article> findTop20ByLocalHintTrueOrderByPublishedAtDesc();

    long countByLocalHintTrue();
    long countByLocalHintFalse();

    List<Article> findByLocalHintTrueAndTitleContainingIgnoreCase(String city);
}
