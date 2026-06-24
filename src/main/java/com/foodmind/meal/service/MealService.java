package com.foodmind.meal.service;

import com.foodmind.common.enums.GroupMemberStatus;
import com.foodmind.common.enums.MealType;
import com.foodmind.common.enums.PrivacyLevel;
import com.foodmind.group.entity.FoodGroup;
import com.foodmind.group.repository.FoodGroupRepository;
import com.foodmind.group.repository.GroupMemberRepository;
import com.foodmind.meal.dto.CreateMealRequest;
import com.foodmind.meal.dto.MealRecordDto;
import com.foodmind.meal.dto.PageResponse;
import com.foodmind.meal.dto.UpdateMealRequest;
import com.foodmind.meal.entity.MealRecord;
import com.foodmind.meal.repository.MealRecordRepository;
import com.foodmind.meal.repository.MealRecordSpecification;
import com.foodmind.user.entity.User;
import com.foodmind.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRecordRepository mealRecordRepository;
    private final UserRepository userRepository;
    private final FoodGroupRepository foodGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public MealRecordDto createMeal(CreateMealRequest request) {
        User user = getCurrentUser();
        FoodGroup group = validatePrivacyAndResolveGroup(request.getPrivacyLevel(), request.getGroupId(), user.getId());

        OffsetDateTime now = OffsetDateTime.now();
        MealRecord mealRecord = new MealRecord();
        mealRecord.setUser(user);
        applyCreateRequest(mealRecord, request, group);
        mealRecord.setCreatedAt(now);
        mealRecord.setUpdatedAt(now);

        return toDto(mealRecordRepository.save(mealRecord));
    }

    @Transactional(readOnly = true)
    public PageResponse<MealRecordDto> listMeals(
            MealType mealType,
            String cuisineType,
            OffsetDateTime from,
            OffsetDateTime to,
            String keyword,
            int page,
            int size
    ) {
        User user = getCurrentUser();
        validatePagination(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "consumedAt").and(Sort.by(Sort.Direction.DESC, "id"))
        );

        Page<MealRecordDto> meals = mealRecordRepository.findAll(
                MealRecordSpecification.forCurrentUserMeals(
                        user.getId(),
                        mealType,
                        cuisineType,
                        from,
                        to,
                        keyword
                ),
                pageable
        ).map(this::toDto);

        return PageResponse.from(meals);
    }

    @Transactional(readOnly = true)
    public MealRecordDto getMeal(Long mealId) {
        User user = getCurrentUser();
        MealRecord mealRecord = getExistingMeal(mealId);

        if (mealRecord.getUser().getId().equals(user.getId())) {
            return toDto(mealRecord);
        }

        if (mealRecord.getPrivacyLevel() == PrivacyLevel.GROUP
                && mealRecord.getGroup() != null
                && isActiveGroupMember(mealRecord.getGroup().getId(), user.getId())) {
            return toDto(mealRecord);
        }

        throw new AccessDeniedException("You do not have permission to view this meal");
    }

    @Transactional
    public MealRecordDto updateMeal(Long mealId, UpdateMealRequest request) {
        User user = getCurrentUser();
        MealRecord mealRecord = getExistingMeal(mealId);
        validateOwner(mealRecord, user.getId(), "update");

        FoodGroup group = validatePrivacyAndResolveGroup(request.getPrivacyLevel(), request.getGroupId(), user.getId());
        applyUpdateRequest(mealRecord, request, group);
        mealRecord.setUpdatedAt(OffsetDateTime.now());

        return toDto(mealRecordRepository.save(mealRecord));
    }

    @Transactional
    public void deleteMeal(Long mealId) {
        User user = getCurrentUser();
        MealRecord mealRecord = getExistingMeal(mealId);
        validateOwner(mealRecord, user.getId(), "delete");

        mealRecord.setDeletedAt(OffsetDateTime.now());
        mealRecord.setUpdatedAt(OffsetDateTime.now());
        mealRecordRepository.save(mealRecord);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required");
        }

        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
    }

    private MealRecord getExistingMeal(Long mealId) {
        return mealRecordRepository.findByIdAndDeletedAtIsNull(mealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found"));
    }

    private FoodGroup validatePrivacyAndResolveGroup(PrivacyLevel privacyLevel, Long groupId, Long userId) {
        if (privacyLevel != PrivacyLevel.PRIVATE && privacyLevel != PrivacyLevel.GROUP) {
            throw new IllegalArgumentException("privacyLevel must be PRIVATE or GROUP");
        }

        if (privacyLevel == PrivacyLevel.PRIVATE) {
            if (groupId != null) {
                throw new IllegalArgumentException("groupId must be null when privacyLevel is PRIVATE");
            }
            return null;
        }

        if (groupId == null) {
            throw new IllegalArgumentException("groupId is required when privacyLevel is GROUP");
        }

        if (!isActiveGroupMember(groupId, userId)) {
            throw new IllegalArgumentException("User must be an ACTIVE member of the group");
        }

        return foodGroupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    }

    private boolean isActiveGroupMember(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroup_IdAndUser_IdAndStatus(
                groupId,
                userId,
                GroupMemberStatus.ACTIVE
        );
    }

    private void validateOwner(MealRecord mealRecord, Long userId, String action) {
        if (!mealRecord.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to " + action + " this meal");
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");
        }
    }

    private void applyCreateRequest(MealRecord mealRecord, CreateMealRequest request, FoodGroup group) {
        mealRecord.setGroup(group);
        mealRecord.setFoodName(request.getFoodName().trim());
        mealRecord.setRestaurantName(trimToNull(request.getRestaurantName()));
        mealRecord.setCuisineType(trimToNull(request.getCuisineType()));
        mealRecord.setMealType(request.getMealType());
        mealRecord.setPrice(request.getPrice());
        mealRecord.setRating(request.getRating());
        mealRecord.setComment(trimToNull(request.getComment()));
        mealRecord.setPhotoUrl(trimToNull(request.getPhotoUrl()));
        mealRecord.setWouldEatAgain(request.getWouldEatAgain());
        mealRecord.setPrivacyLevel(request.getPrivacyLevel());
        mealRecord.setConsumedAt(request.getConsumedAt());
    }

    private void applyUpdateRequest(MealRecord mealRecord, UpdateMealRequest request, FoodGroup group) {
        mealRecord.setGroup(group);
        mealRecord.setFoodName(request.getFoodName().trim());
        mealRecord.setRestaurantName(trimToNull(request.getRestaurantName()));
        mealRecord.setCuisineType(trimToNull(request.getCuisineType()));
        mealRecord.setMealType(request.getMealType());
        mealRecord.setPrice(request.getPrice());
        mealRecord.setRating(request.getRating());
        mealRecord.setComment(trimToNull(request.getComment()));
        mealRecord.setPhotoUrl(trimToNull(request.getPhotoUrl()));
        mealRecord.setWouldEatAgain(request.getWouldEatAgain());
        mealRecord.setPrivacyLevel(request.getPrivacyLevel());
        mealRecord.setConsumedAt(request.getConsumedAt());
    }

    private MealRecordDto toDto(MealRecord mealRecord) {
        return MealRecordDto.builder()
                .id(mealRecord.getId())
                .userId(mealRecord.getUser().getId())
                .username(mealRecord.getUser().getUsername())
                .groupId(mealRecord.getGroup() == null ? null : mealRecord.getGroup().getId())
                .foodName(mealRecord.getFoodName())
                .restaurantName(mealRecord.getRestaurantName())
                .cuisineType(mealRecord.getCuisineType())
                .mealType(mealRecord.getMealType())
                .price(mealRecord.getPrice())
                .rating(mealRecord.getRating())
                .comment(mealRecord.getComment())
                .photoUrl(mealRecord.getPhotoUrl())
                .wouldEatAgain(mealRecord.getWouldEatAgain())
                .privacyLevel(mealRecord.getPrivacyLevel())
                .consumedAt(mealRecord.getConsumedAt())
                .createdAt(mealRecord.getCreatedAt())
                .updatedAt(mealRecord.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
