package com.foodmind.analytics.repository;

import com.foodmind.analytics.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
