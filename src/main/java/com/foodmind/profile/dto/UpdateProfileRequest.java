package com.foodmind.profile.dto;

import com.foodmind.common.enums.FoodGoal;
import com.foodmind.common.enums.PrivacyLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProfileRequest {
    @DecimalMin(value = "0.00", message = "budgetMin must be greater than or equal to 0")
    private BigDecimal budgetMin;

    @DecimalMin(value = "0.00", message = "budgetMax must be greater than or equal to 0")
    private BigDecimal budgetMax;

    @Min(value = 0, message = "spicyTolerance must be greater than or equal to 0")
    @Max(value = 5, message = "spicyTolerance must be less than or equal to 5")
    private Integer spicyTolerance;

    @Size(max = 120, message = "locationArea must not exceed 120 characters")
    private String locationArea;

    private FoodGoal foodGoal;

    private PrivacyLevel defaultPrivacyLevel;
}
