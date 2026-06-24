package com.foodmind.profile.repository;

import com.foodmind.profile.entity.DietaryRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DietaryRestrictionRepository extends JpaRepository<DietaryRestriction, Long> {
    List<DietaryRestriction> findByUser_Id(Long userId);

    void deleteByUser_Id(Long userId);
}
