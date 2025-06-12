package com.example.localnews_backend.service;

import com.example.localnews_backend.model.Article;
import com.example.localnews_backend.model.City;
import com.example.localnews_backend.repo.ArticleRepository;
import com.example.localnews_backend.repo.CityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Order(1) // make sure cities are loaded first
public class NewsFetchService implements ApplicationRunner {
    private final ArticleRepository articleRepo;
    private final CityRepository cityRepo;
    private final WebClient webClient;

    public NewsFetchService(ArticleRepository articleRepo,
                            CityRepository cityRepo,
                            @Value("${newsapi.key}") String apiKey) {
        this.articleRepo = articleRepo;
        this.cityRepo    = cityRepo;
        this.webClient   = WebClient.builder()
                .baseUrl("https://newsapi.org/v2")
                .defaultHeader("X-Api-Key", apiKey)
                .build();
    }

    @Override
    public void run(ApplicationArguments args) {
        // only seed on an empty DB
        if (articleRepo.countByLocalHintFalse() == 0) {
            fetchGlobalNews();
        }

        // only seed locals if we have none
        if (articleRepo.countByLocalHintTrue() == 0) {
            fetchLocalNews();
        }

        System.out.println("NewsFetchService: total articles = " + articleRepo.count());
    }

    private void fetchGlobalNews() {
        System.out.println("Fetching global news...");
        @SuppressWarnings("unchecked")
        Map<String, ?> resp = webClient.get()
                .uri(u -> u.path("/top-headlines")
                        .queryParam("language", "en")
                        .queryParam("pageSize", "20")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        saveArticles(resp, /*localHint=*/false, /*city=*/null, /*state=*/null);
    }

    private void fetchLocalNews() {
        System.out.println("Fetching local news...");
        // top 40 by population × 2 each = ~80
        List<City> top40 = cityRepo.findAll().stream()
                .sorted((a,b)->b.getPopulation().compareTo(a.getPopulation()))
                .limit(40)
                .toList();

        for (City city : top40) {
            try {
                String q = URLEncoder.encode(city.getName(), StandardCharsets.UTF_8);
                @SuppressWarnings("unchecked")
                var resp = webClient.get()
                        .uri(u -> u.path("/everything")
                                .queryParam("q", city.getName())   // raw name
                                .queryParam("pageSize", "2")
                                .build())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                saveArticles(resp, /*localHint=*/true,
                        /*city=*/city.getName(),
                        /*state=*/city.getStateCode());
            } catch (Exception e) {
                System.err.println("Failed to fetch for city: " + city.getName());
                e.printStackTrace();   // <— add this
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void saveArticles(Map<String, ?> resp,
                              boolean localHint,
                              String cityName,
                              String stateCode) {
        if (resp == null || !"ok".equals(resp.get("status"))) return;

        var list = (List<Map<String, Object>>) resp.get("articles");
        for (var a : list) {
            try {
                Article art = new Article();
                art.setTitle((String) a.get("title"));
                art.setBody( (String) a.getOrDefault("content", "") );
                art.setSource(((Map<String, String>)a.get("source")).get("name"));
                art.setPublishedAt( Instant.parse((String)a.get("publishedAt")) );

                // ★ NEW: set both the hint *and* the actual location columns ★
                art.setLocalHint(localHint);
                art.setCity(cityName);
                art.setStateCode(stateCode);

                articleRepo.save(art);
            } catch (Exception ex) {
                System.err.println("Skipping article: " + a.get("title"));
            }
        }
    }
}
