package com.foodmind.recommendation.repository;

import com.foodmind.recommendation.entity.WantToTryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WantToTryItemRepository extends JpaRepository<WantToTryItem, Long> {
}
