package com.foodmind.meal.repository;

import com.foodmind.common.enums.MealType;
import com.foodmind.meal.entity.MealRecord;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.Locale;

public final class MealRecordSpecification {
    private MealRecordSpecification() {
    }

    public static Specification<MealRecord> forCurrentUserMeals(
            Long userId,
            MealType mealType,
            String cuisineType,
            OffsetDateTime from,
            OffsetDateTime to,
            String keyword
    ) {
        return userIdEquals(userId)
                .and(notDeleted())
                .and(mealTypeEquals(mealType))
                .and(cuisineTypeEquals(cuisineType))
                .and(consumedAtFrom(from))
                .and(consumedAtTo(to))
                .and(keywordContains(keyword));
    }

    private static Specification<MealRecord> userIdEquals(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<MealRecord> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    private static Specification<MealRecord> mealTypeEquals(MealType mealType) {
        return mealType == null ? null : (root, query, cb) -> cb.equal(root.get("mealType"), mealType);
    }

    private static Specification<MealRecord> cuisineTypeEquals(String cuisineType) {
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            return null;
        }
        String normalizedCuisineType = cuisineType.trim().toLowerCase(Locale.ROOT);
        return (root, query, cb) -> cb.equal(cb.lower(root.get("cuisineType")), normalizedCuisineType);
    }

    private static Specification<MealRecord> consumedAtFrom(OffsetDateTime from) {
        return from == null ? null : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("consumedAt"), from);
    }

    private static Specification<MealRecord> consumedAtTo(OffsetDateTime to) {
        return to == null ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("consumedAt"), to);
    }

    private static Specification<MealRecord> keywordContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("foodName")), pattern),
                cb.like(cb.lower(root.get("restaurantName")), pattern),
                cb.like(cb.lower(root.get("cuisineType")), pattern),
                cb.like(cb.lower(root.get("comment")), pattern)
        );
    }
}
