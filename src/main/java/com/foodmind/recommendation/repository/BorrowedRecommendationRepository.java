package com.foodmind.recommendation.repository;

import com.foodmind.recommendation.entity.BorrowedRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowedRecommendationRepository extends JpaRepository<BorrowedRecommendation, Long> {
}
