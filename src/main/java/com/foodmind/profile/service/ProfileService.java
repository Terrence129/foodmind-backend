package com.foodmind.profile.service;

import com.foodmind.common.enums.CuisinePreferenceType;
import com.foodmind.common.enums.PrivacyLevel;
import com.foodmind.common.enums.RestrictionSeverity;
import com.foodmind.profile.dto.ProfileResponse;
import com.foodmind.profile.dto.UpdateCuisinePreferencesRequest;
import com.foodmind.profile.dto.UpdateDietaryRestrictionsRequest;
import com.foodmind.profile.dto.UpdateProfileRequest;
import com.foodmind.profile.entity.CuisinePreference;
import com.foodmind.profile.entity.DietaryRestriction;
import com.foodmind.profile.entity.UserProfile;
import com.foodmind.profile.repository.CuisinePreferenceRepository;
import com.foodmind.profile.repository.DietaryRestrictionRepository;
import com.foodmind.profile.repository.UserProfileRepository;
import com.foodmind.user.entity.User;
import com.foodmind.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final int MAX_CUISINE_NAME_LENGTH = 80;
    private static final int MAX_RESTRICTION_NAME_LENGTH = 100;

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CuisinePreferenceRepository cuisinePreferenceRepository;
    private final DietaryRestrictionRepository dietaryRestrictionRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User user = getCurrentUser();

        UserProfile profile = userProfileRepository.findByUser_Id(user.getId()).orElse(null);
        List<CuisinePreference> cuisinePreferences = cuisinePreferenceRepository.findByUser_Id(user.getId());
        List<DietaryRestriction> dietaryRestrictions = dietaryRestrictionRepository.findByUser_Id(user.getId());

        return buildProfileResponse(user, profile, cuisinePreferences, dietaryRestrictions);
    }

    @Transactional
    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        validateBudgetRange(request.getBudgetMin(), request.getBudgetMax());

        UserProfile profile = userProfileRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserProfile createdProfile = new UserProfile();
                    createdProfile.setUser(user);
                    createdProfile.setCreatedAt(OffsetDateTime.now());
                    return createdProfile;
                });

        profile.setBudgetMin(request.getBudgetMin());
        profile.setBudgetMax(request.getBudgetMax());
        profile.setSpicyTolerance(resolveSpicyTolerance(request, profile));
        profile.setLocationArea(request.getLocationArea());
        profile.setFoodGoal(request.getFoodGoal());
        profile.setDefaultPrivacyLevel(resolveDefaultPrivacyLevel(request, profile));
        profile.setUpdatedAt(OffsetDateTime.now());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return buildProfileResponse(
                user,
                savedProfile,
                cuisinePreferenceRepository.findByUser_Id(user.getId()),
                dietaryRestrictionRepository.findByUser_Id(user.getId())
        );
    }

    @Transactional
    public ProfileResponse updateCuisinePreferences(UpdateCuisinePreferencesRequest request) {
        User user = getCurrentUser();

        List<String> likedCuisines = normalizeUniqueNames(
                request.getLikedCuisines(),
                "Cuisine name cannot be blank",
                MAX_CUISINE_NAME_LENGTH,
                "Cuisine name must not exceed " + MAX_CUISINE_NAME_LENGTH + " characters"
        );
        List<String> dislikedCuisines = normalizeUniqueNames(
                request.getDislikedCuisines(),
                "Cuisine name cannot be blank",
                MAX_CUISINE_NAME_LENGTH,
                "Cuisine name must not exceed " + MAX_CUISINE_NAME_LENGTH + " characters"
        );

        Set<String> likedKeys = likedCuisines.stream()
                .map(ProfileService::normalizationKey)
                .collect(Collectors.toSet());
        boolean hasConflict = dislikedCuisines.stream()
                .map(ProfileService::normalizationKey)
                .anyMatch(likedKeys::contains);
        if (hasConflict) {
            throw new IllegalArgumentException("The same cuisine cannot appear in both likedCuisines and dislikedCuisines");
        }

        cuisinePreferenceRepository.deleteByUser_Id(user.getId());

        List<CuisinePreference> preferences = new ArrayList<>();
        likedCuisines.forEach(cuisineName ->
                preferences.add(buildCuisinePreference(user, cuisineName, CuisinePreferenceType.LIKE))
        );
        dislikedCuisines.forEach(cuisineName ->
                preferences.add(buildCuisinePreference(user, cuisineName, CuisinePreferenceType.DISLIKE))
        );
        cuisinePreferenceRepository.saveAll(preferences);

        return buildProfileResponse(
                user,
                userProfileRepository.findByUser_Id(user.getId()).orElse(null),
                cuisinePreferenceRepository.findByUser_Id(user.getId()),
                dietaryRestrictionRepository.findByUser_Id(user.getId())
        );
    }

    @Transactional
    public ProfileResponse updateDietaryRestrictions(UpdateDietaryRestrictionsRequest request) {
        User user = getCurrentUser();

        List<String> restrictions = normalizeUniqueNames(
                request.getDietaryRestrictions(),
                "Dietary restriction cannot be blank",
                MAX_RESTRICTION_NAME_LENGTH,
                "Dietary restriction must not exceed " + MAX_RESTRICTION_NAME_LENGTH + " characters"
        );

        dietaryRestrictionRepository.deleteByUser_Id(user.getId());

        List<DietaryRestriction> dietaryRestrictions = restrictions.stream()
                .map(restrictionName -> buildDietaryRestriction(user, restrictionName))
                .toList();
        dietaryRestrictionRepository.saveAll(dietaryRestrictions);

        return buildProfileResponse(
                user,
                userProfileRepository.findByUser_Id(user.getId()).orElse(null),
                cuisinePreferenceRepository.findByUser_Id(user.getId()),
                dietaryRestrictionRepository.findByUser_Id(user.getId())
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required");
        }

        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
    }

    private void validateBudgetRange(BigDecimal budgetMin, BigDecimal budgetMax) {
        if (budgetMin != null && budgetMax != null && budgetMin.compareTo(budgetMax) > 0) {
            throw new IllegalArgumentException("budgetMin must be less than or equal to budgetMax");
        }
    }

    private Short resolveSpicyTolerance(UpdateProfileRequest request, UserProfile profile) {
        if (request.getSpicyTolerance() != null) {
            return request.getSpicyTolerance().shortValue();
        }
        if (profile.getSpicyTolerance() != null) {
            return profile.getSpicyTolerance();
        }
        return 0;
    }

    private PrivacyLevel resolveDefaultPrivacyLevel(UpdateProfileRequest request, UserProfile profile) {
        if (request.getDefaultPrivacyLevel() != null) {
            return request.getDefaultPrivacyLevel();
        }
        if (profile.getDefaultPrivacyLevel() != null) {
            return profile.getDefaultPrivacyLevel();
        }
        return PrivacyLevel.PRIVATE;
    }

    private List<String> normalizeUniqueNames(
            List<String> names,
            String blankMessage,
            int maxLength,
            String maxLengthMessage
    ) {
        if (names == null) {
            return List.of();
        }

        Map<String, String> uniqueNames = new LinkedHashMap<>();
        for (String name : names) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException(blankMessage);
            }

            String trimmedName = name.trim();
            if (trimmedName.length() > maxLength) {
                throw new IllegalArgumentException(maxLengthMessage);
            }
            uniqueNames.putIfAbsent(normalizationKey(trimmedName), trimmedName);
        }

        return new ArrayList<>(uniqueNames.values());
    }

    private CuisinePreference buildCuisinePreference(
            User user,
            String cuisineName,
            CuisinePreferenceType preferenceType
    ) {
        CuisinePreference preference = new CuisinePreference();
        preference.setUser(user);
        preference.setCuisineName(cuisineName);
        preference.setPreferenceType(preferenceType);
        preference.setCreatedAt(OffsetDateTime.now());
        return preference;
    }

    private DietaryRestriction buildDietaryRestriction(User user, String restrictionName) {
        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setUser(user);
        restriction.setRestrictionName(restrictionName);
        restriction.setSeverity(RestrictionSeverity.NORMAL);
        restriction.setCreatedAt(OffsetDateTime.now());
        return restriction;
    }

    private ProfileResponse buildProfileResponse(
            User user,
            UserProfile profile,
            List<CuisinePreference> cuisinePreferences,
            List<DietaryRestriction> dietaryRestrictions
    ) {
        List<String> likedCuisines = cuisinePreferences.stream()
                .filter(preference -> preference.getPreferenceType() == CuisinePreferenceType.LIKE)
                .map(CuisinePreference::getCuisineName)
                .toList();
        List<String> dislikedCuisines = cuisinePreferences.stream()
                .filter(preference -> preference.getPreferenceType() == CuisinePreferenceType.DISLIKE)
                .map(CuisinePreference::getCuisineName)
                .toList();
        List<String> restrictionNames = dietaryRestrictions.stream()
                .map(DietaryRestriction::getRestrictionName)
                .toList();

        return ProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .budgetMin(profile == null ? null : profile.getBudgetMin())
                .budgetMax(profile == null ? null : profile.getBudgetMax())
                .spicyTolerance(profile == null || profile.getSpicyTolerance() == null
                        ? null
                        : profile.getSpicyTolerance().intValue())
                .locationArea(profile == null ? null : profile.getLocationArea())
                .foodGoal(profile == null ? null : profile.getFoodGoal())
                .defaultPrivacyLevel(profile == null ? null : profile.getDefaultPrivacyLevel())
                .likedCuisines(likedCuisines)
                .dislikedCuisines(dislikedCuisines)
                .dietaryRestrictions(restrictionNames)
                .build();
    }

    private static String normalizationKey(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
