package com.foodmind.group.entity;

import com.foodmind.common.enums.MealType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Immutable
@Table(name = "v_group_feed")
@Getter
@Setter
public class GroupFeedItem {
    @EmbeddedId
    private GroupFeedItemId id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 80)
    private String username;

    @Column(name = "item_name", length = 160)
    private String itemName;

    @Column(name = "place_name", length = 160)
    private String placeName;

    @Column(name = "cuisine_type", length = 80)
    private String cuisineType;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", length = 20)
    private MealType mealType;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    private String comment;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "would_have_again")
    private Boolean wouldHaveAgain;

    @Column(name = "consumed_at")
    private OffsetDateTime consumedAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
