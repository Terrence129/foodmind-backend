package com.foodmind.meal.dto;

import com.foodmind.common.enums.MealType;
import com.foodmind.common.enums.PrivacyLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class CreateMealRequest {
    private Long groupId;

    @NotBlank(message = "foodName is required")
    @Size(max = 160, message = "foodName must not exceed 160 characters")
    private String foodName;

    @Size(max = 160, message = "restaurantName must not exceed 160 characters")
    private String restaurantName;

    @Size(max = 80, message = "cuisineType must not exceed 80 characters")
    private String cuisineType;

    @NotNull(message = "mealType is required")
    private MealType mealType;

    @DecimalMin(value = "0.00", message = "price must be greater than or equal to 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "rating must be greater than or equal to 0")
    @DecimalMax(value = "5.0", message = "rating must be less than or equal to 5")
    private BigDecimal rating;

    @Size(max = 255, message = "comment must not exceed 255 characters")
    private String comment;

    @Size(max = 255, message = "photoUrl must not exceed 255 characters")
    private String photoUrl;

    private Boolean wouldEatAgain;

    @NotNull(message = "privacyLevel is required")
    private PrivacyLevel privacyLevel;

    @NotNull(message = "consumedAt is required")
    private OffsetDateTime consumedAt;
}
