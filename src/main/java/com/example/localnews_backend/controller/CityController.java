package com.example.localnews_backend.controller;


import com.example.localnews_backend.model.City;
import com.example.localnews_backend.repo.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRepository cityRepo;

    public CityController(CityRepository cityRepo) {
        this.cityRepo = cityRepo;
    }

    /**
     * GET /api/cities?prefix={prefix}&page={page}&size={size}
     * Returns a paginated list of cities whose names start with the given prefix.
     */
    @GetMapping
    public Page<City> getCities(
            @RequestParam(defaultValue = "") String prefix,
            Pageable pageable
    ) {
        return cityRepo.findByNameStartingWith(prefix, pageable);
    }
}
