package com.foodmind.group.repository;

import com.foodmind.group.entity.FoodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodGroupRepository extends JpaRepository<FoodGroup, Long> {
    Optional<FoodGroup> findByIdAndDeletedAtIsNull(Long id);
}
