package com.foodmind.drink.repository;

import com.foodmind.drink.entity.DrinkRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrinkRecordRepository extends JpaRepository<DrinkRecord, Long> {
}
