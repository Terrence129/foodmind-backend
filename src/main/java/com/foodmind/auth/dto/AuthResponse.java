package com.foodmind.auth.dto;

import com.foodmind.user.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @description:
 * @author: chenyaqi
 * @email: terrence.yaqi.chen@u.nus.edu
 * @date: 23/6/2026 1:59 pm
 */
@Getter
@Builder
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private UserResponse user;
}