package com.foodmind.profile.service;

import com.foodmind.profile.dto.ProfileResponse;
import com.foodmind.profile.dto.UpdateCuisinePreferencesRequest;
import com.foodmind.profile.dto.UpdateDietaryRestrictionsRequest;
import com.foodmind.profile.dto.UpdateProfileRequest;
import com.foodmind.profile.entity.DietaryRestriction;
import com.foodmind.profile.repository.CuisinePreferenceRepository;
import com.foodmind.profile.repository.DietaryRestrictionRepository;
import com.foodmind.profile.repository.UserProfileRepository;
import com.foodmind.user.entity.User;
import com.foodmind.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private CuisinePreferenceRepository cuisinePreferenceRepository;

    @Mock
    private DietaryRestrictionRepository dietaryRestrictionRepository;

    private ProfileService profileService;
    private User user;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(
                userRepository,
                userProfileRepository,
                cuisinePreferenceRepository,
                dietaryRestrictionRepository
        );
        user = User.builder()
                .id(1L)
                .email("dadao@example.com")
                .username("Dadao")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of())
        );
        when(userRepository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyProfileReturnsNullableFieldsAndEmptyListsWhenProfileMissing() {
        when(userProfileRepository.findByUser_Id(user.getId())).thenReturn(Optional.empty());
        when(cuisinePreferenceRepository.findByUser_Id(user.getId())).thenReturn(List.of());
        when(dietaryRestrictionRepository.findByUser_Id(user.getId())).thenReturn(List.of());

        ProfileResponse response = profileService.getMyProfile();

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("Dadao");
        assertThat(response.getBudgetMin()).isNull();
        assertThat(response.getBudgetMax()).isNull();
        assertThat(response.getSpicyTolerance()).isNull();
        assertThat(response.getLocationArea()).isNull();
        assertThat(response.getFoodGoal()).isNull();
        assertThat(response.getDefaultPrivacyLevel()).isNull();
        assertThat(response.getLikedCuisines()).isEmpty();
        assertThat(response.getDislikedCuisines()).isEmpty();
        assertThat(response.getDietaryRestrictions()).isEmpty();
    }

    @Test
    void updateMyProfileRejectsBudgetMinGreaterThanBudgetMax() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBudgetMin(new BigDecimal("20.00"));
        request.setBudgetMax(new BigDecimal("10.00"));

        assertThatThrownBy(() -> profileService.updateMyProfile(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("budgetMin must be less than or equal to budgetMax");
    }

    @Test
    void updateCuisinePreferencesRejectsCaseInsensitiveConflict() {
        UpdateCuisinePreferencesRequest request = new UpdateCuisinePreferencesRequest();
        request.setLikedCuisines(List.of("Chinese"));
        request.setDislikedCuisines(List.of("chinese"));

        assertThatThrownBy(() -> profileService.updateCuisinePreferences(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The same cuisine cannot appear in both likedCuisines and dislikedCuisines");
    }

    @Test
    void updateDietaryRestrictionsTrimsAndRemovesCaseInsensitiveDuplicates() {
        UpdateDietaryRestrictionsRequest request = new UpdateDietaryRestrictionsRequest();
        request.setDietaryRestrictions(List.of(" No beef ", "no beef", "No peanuts"));

        when(userProfileRepository.findByUser_Id(user.getId())).thenReturn(Optional.empty());
        when(cuisinePreferenceRepository.findByUser_Id(user.getId())).thenReturn(List.of());
        when(dietaryRestrictionRepository.findByUser_Id(user.getId())).thenReturn(List.of());

        profileService.updateDietaryRestrictions(request);

        ArgumentCaptor<Iterable<DietaryRestriction>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(dietaryRestrictionRepository).saveAll(captor.capture());

        List<DietaryRestriction> savedRestrictions = new ArrayList<>();
        captor.getValue().forEach(savedRestrictions::add);

        assertThat(savedRestrictions)
                .extracting(DietaryRestriction::getRestrictionName)
                .containsExactly("No beef", "No peanuts");
    }
}
