package com.foodmind.profile.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateDietaryRestrictionsRequest {
    private List<String> dietaryRestrictions;
}
