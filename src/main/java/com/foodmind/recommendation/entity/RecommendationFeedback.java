package com.foodmind.recommendation.entity;

import com.foodmind.common.enums.RecommendationFeedbackAction;
import com.foodmind.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "recommendation_feedback")
@Getter
@Setter
public class RecommendationFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recommendation_item_id", nullable = false)
    private RecommendationItem recommendationItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RecommendationFeedbackAction action;

    @Column(name = "feedback_comment")
    private String feedbackComment;

    @Column(name = "post_meal_rating", precision = 2, scale = 1)
    private BigDecimal postMealRating;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
