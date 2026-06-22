package com.foodmind.group.repository;

import com.foodmind.group.entity.FoodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodGroupRepository extends JpaRepository<FoodGroup, Long> {
}
