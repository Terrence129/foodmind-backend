package com.foodmind.meal.service;

import com.foodmind.common.enums.MealType;
import com.foodmind.common.enums.PrivacyLevel;
import com.foodmind.group.repository.FoodGroupRepository;
import com.foodmind.group.repository.GroupMemberRepository;
import com.foodmind.meal.dto.CreateMealRequest;
import com.foodmind.meal.entity.MealRecord;
import com.foodmind.meal.repository.MealRecordRepository;
import com.foodmind.user.entity.User;
import com.foodmind.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRecordRepository mealRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodGroupRepository foodGroupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    private MealService mealService;
    private User currentUser;

    @BeforeEach
    void setUp() {
        mealService = new MealService(
                mealRecordRepository,
                userRepository,
                foodGroupRepository,
                groupMemberRepository
        );
        currentUser = User.builder()
                .id(1L)
                .email("dadao@example.com")
                .username("Dadao")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser.getEmail(), null, List.of())
        );
        when(userRepository.findByEmailIgnoreCase(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createMealRejectsPrivateMealWithGroupId() {
        CreateMealRequest request = validCreateRequest();
        request.setPrivacyLevel(PrivacyLevel.PRIVATE);
        request.setGroupId(1L);

        assertThatThrownBy(() -> mealService.createMeal(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("groupId must be null when privacyLevel is PRIVATE");
    }

    @Test
    void createMealRejectsGroupMealWithoutGroupId() {
        CreateMealRequest request = validCreateRequest();
        request.setPrivacyLevel(PrivacyLevel.GROUP);
        request.setGroupId(null);

        assertThatThrownBy(() -> mealService.createMeal(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("groupId is required when privacyLevel is GROUP");
    }

    @Test
    void createMealRejectsUnsupportedPrivacyLevel() {
        CreateMealRequest request = validCreateRequest();
        request.setPrivacyLevel(PrivacyLevel.PUBLIC);

        assertThatThrownBy(() -> mealService.createMeal(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("privacyLevel must be PRIVATE or GROUP");
    }

    @Test
    void getMealRejectsPrivateMealOwnedByAnotherUser() {
        MealRecord mealRecord = mealOwnedBy(otherUser());
        mealRecord.setPrivacyLevel(PrivacyLevel.PRIVATE);
        when(mealRecordRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(mealRecord));

        assertThatThrownBy(() -> mealService.getMeal(10L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to view this meal");
    }

    @Test
    void deleteMealSoftDeletesOwnedMeal() {
        MealRecord mealRecord = mealOwnedBy(currentUser);
        when(mealRecordRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(mealRecord));
        when(mealRecordRepository.save(any(MealRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mealService.deleteMeal(10L);

        ArgumentCaptor<MealRecord> captor = ArgumentCaptor.forClass(MealRecord.class);
        verify(mealRecordRepository).save(captor.capture());
        assertThat(captor.getValue().getDeletedAt()).isNotNull();
    }

    private CreateMealRequest validCreateRequest() {
        CreateMealRequest request = new CreateMealRequest();
        request.setFoodName("Chicken Rice");
        request.setMealType(MealType.LUNCH);
        request.setPrivacyLevel(PrivacyLevel.PRIVATE);
        request.setConsumedAt(OffsetDateTime.parse("2026-06-23T12:30:00+08:00"));
        return request;
    }

    private MealRecord mealOwnedBy(User user) {
        MealRecord mealRecord = new MealRecord();
        mealRecord.setId(10L);
        mealRecord.setUser(user);
        mealRecord.setFoodName("Chicken Rice");
        mealRecord.setMealType(MealType.LUNCH);
        mealRecord.setPrivacyLevel(PrivacyLevel.PRIVATE);
        mealRecord.setConsumedAt(OffsetDateTime.parse("2026-06-23T12:30:00+08:00"));
        mealRecord.setCreatedAt(OffsetDateTime.parse("2026-06-23T12:35:00+08:00"));
        mealRecord.setUpdatedAt(OffsetDateTime.parse("2026-06-23T12:35:00+08:00"));
        return mealRecord;
    }

    private User otherUser() {
        return User.builder()
                .id(2L)
                .email("friend@example.com")
                .username("Friend")
                .build();
    }
}
