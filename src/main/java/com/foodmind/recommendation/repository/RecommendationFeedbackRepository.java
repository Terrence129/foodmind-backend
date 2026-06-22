package com.foodmind.recommendation.repository;

import com.foodmind.recommendation.entity.RecommendationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationFeedbackRepository extends JpaRepository<RecommendationFeedback, Long> {
}
