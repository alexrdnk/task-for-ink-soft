package com.example.localnews_backend.repo;

import com.example.localnews_backend.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
    Page<City> findByNameStartingWith(String prefix, Pageable pageable);
}
