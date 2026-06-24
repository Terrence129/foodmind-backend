package com.foodmind.profile.repository;

import com.foodmind.profile.entity.CuisinePreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuisinePreferenceRepository extends JpaRepository<CuisinePreference, Long> {
    List<CuisinePreference> findByUser_Id(Long userId);

    void deleteByUser_Id(Long userId);
}
