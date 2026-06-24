package com.foodmind.profile.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCuisinePreferencesRequest {
    private List<String> likedCuisines;
    private List<String> dislikedCuisines;
}
