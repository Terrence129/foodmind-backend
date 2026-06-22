package com.foodmind.profile.repository;

import com.foodmind.profile.entity.DietaryRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietaryRestrictionRepository extends JpaRepository<DietaryRestriction, Long> {
}
