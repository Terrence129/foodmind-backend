package com.foodmind.recommendation.repository;

import com.foodmind.recommendation.entity.RecommendationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {
}
