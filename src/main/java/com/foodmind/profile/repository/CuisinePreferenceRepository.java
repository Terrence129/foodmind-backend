package com.foodmind.profile.repository;

import com.foodmind.profile.entity.CuisinePreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuisinePreferenceRepository extends JpaRepository<CuisinePreference, Long> {
}
