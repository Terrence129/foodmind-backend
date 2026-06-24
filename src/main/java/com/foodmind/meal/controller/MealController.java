package com.foodmind.meal.controller;

import com.foodmind.common.enums.MealType;
import com.foodmind.meal.dto.CreateMealRequest;
import com.foodmind.meal.dto.MealRecordDto;
import com.foodmind.meal.dto.PageResponse;
import com.foodmind.meal.dto.UpdateMealRequest;
import com.foodmind.meal.service.MealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description:
 * @author: chenyaqi
 * @email: terrence.yaqi.chen@u.nus.edu
 * @date: 22/6/2026 7:44 pm
 */
@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {
    private final MealService mealService;

    @PostMapping
    public ResponseEntity<MealRecordDto> createMeal(@Valid @RequestBody CreateMealRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mealService.createMeal(request));
    }

    @GetMapping
    public PageResponse<MealRecordDto> listMeals(
            @RequestParam(required = false) MealType mealType,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return mealService.listMeals(mealType, cuisineType, from, to, keyword, page, size);
    }

    @GetMapping("/{mealId}")
    public MealRecordDto getMeal(@PathVariable Long mealId) {
        return mealService.getMeal(mealId);
    }

    @PutMapping("/{mealId}")
    public MealRecordDto updateMeal(
            @PathVariable Long mealId,
            @Valid @RequestBody UpdateMealRequest request
    ) {
        return mealService.updateMeal(mealId, request);
    }

    @DeleteMapping("/{mealId}")
    public Map<String, Object> deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Meal deleted successfully");
        response.put("data", null);
        return response;
    }
}
