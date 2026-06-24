package com.foodmind.user.dto;

import com.foodmind.common.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * @description:
 * @author: chenyaqi
 * @email: terrence.yaqi.chen@u.nus.edu
 * @date: 23/6/2026 2:02 pm
 */
@Getter
@Builder
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String username;
    private String avatarUrl;
    private UserStatus status;
    private Boolean profileCompleted;
    private OffsetDateTime  createdAt;
}