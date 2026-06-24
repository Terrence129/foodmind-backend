package com.foodmind.profile.dto;

import com.foodmind.common.enums.FoodGoal;
import com.foodmind.common.enums.PrivacyLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ProfileResponse {
    private Long userId;
    private String username;
    private String avatarUrl;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private Integer spicyTolerance;
    private String locationArea;
    private FoodGoal foodGoal;
    private PrivacyLevel defaultPrivacyLevel;
    private List<String> likedCuisines;
    private List<String> dislikedCuisines;
    private List<String> dietaryRestrictions;
}
