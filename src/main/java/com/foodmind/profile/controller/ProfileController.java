package com.foodmind.profile.controller;

import com.foodmind.profile.dto.ProfileResponse;
import com.foodmind.profile.dto.UpdateCuisinePreferencesRequest;
import com.foodmind.profile.dto.UpdateDietaryRestrictionsRequest;
import com.foodmind.profile.dto.UpdateProfileRequest;
import com.foodmind.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: chenyaqi
 * @email: terrence.yaqi.chen@u.nus.edu
 * @date: 22/6/2026 7:44 pm
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/me")
    public ProfileResponse getMyProfile() {
        return profileService.getMyProfile();
    }

    @PutMapping("/me")
    public ProfileResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateMyProfile(request);
    }

    @PutMapping("/cuisine-preferences")
    public ProfileResponse updateCuisinePreferences(
            @Valid @RequestBody UpdateCuisinePreferencesRequest request
    ) {
        return profileService.updateCuisinePreferences(request);
    }

    @PutMapping("/dietary-restrictions")
    public ProfileResponse updateDietaryRestrictions(
            @Valid @RequestBody UpdateDietaryRestrictionsRequest request
    ) {
        return profileService.updateDietaryRestrictions(request);
    }
}
