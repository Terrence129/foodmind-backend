package com.foodmind.meal.repository;

import com.foodmind.meal.entity.MealRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {
}
