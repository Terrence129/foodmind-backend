package com.foodmind.analytics.repository;

import com.foodmind.analytics.entity.WeeklyRecap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyRecapRepository extends JpaRepository<WeeklyRecap, Long> {
}
