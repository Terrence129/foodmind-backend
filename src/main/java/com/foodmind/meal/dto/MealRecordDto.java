package com.foodmind.meal.dto;

import com.foodmind.common.enums.MealType;
import com.foodmind.common.enums.PrivacyLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class MealRecordDto {
    private Long id;
    private Long userId;
    private String username;
    private Long groupId;
    private String foodName;
    private String restaurantName;
    private String cuisineType;
    private MealType mealType;
    private BigDecimal price;
    private BigDecimal rating;
    private String comment;
    private String photoUrl;
    private Boolean wouldEatAgain;
    private PrivacyLevel privacyLevel;
    private OffsetDateTime consumedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
