package com.example.localnews_backend.controller;

import com.example.localnews_backend.model.Article;
import com.example.localnews_backend.repo.ArticleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleRepository articleRepo;

    public ArticleController(ArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    /**
     * GET /api/articles/global
     * Returns the latest 20 global articles.
     */
    @GetMapping("/global")
    public List<Article> getGlobalArticles() {
        return articleRepo.findTop20ByLocalHintFalseOrderByPublishedAtDesc();
    }

    /**
     * GET /api/articles/local
     * Returns the latest 20 local articles.
     */
    @GetMapping("/local")
    public List<Article> getLocalArticles() {
        return articleRepo.findTop20ByLocalHintTrueOrderByPublishedAtDesc();
    }

    /**
     * GET /api/articles/local/{city}
     * Returns all local articles for the given city (case-insensitive).
     */
    @GetMapping("/local/{city}")
    public List<Article> getLocalByCity(@PathVariable String city) {
        return articleRepo.findByLocalHintTrueAndTitleContainingIgnoreCase(city);
    }
}

