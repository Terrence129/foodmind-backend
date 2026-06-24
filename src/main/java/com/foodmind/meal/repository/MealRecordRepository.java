package com.foodmind.meal.repository;

import com.foodmind.meal.entity.MealRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MealRecordRepository extends JpaRepository<MealRecord, Long>, JpaSpecificationExecutor<MealRecord> {
    Optional<MealRecord> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByIdAndUser_IdAndDeletedAtIsNull(Long id, Long userId);
}
