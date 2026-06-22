package com.foodmind.drink.entity;

import com.foodmind.common.enums.IceLevel;
import com.foodmind.common.enums.PrivacyLevel;
import com.foodmind.common.enums.SweetnessLevel;
import com.foodmind.group.entity.FoodGroup;
import com.foodmind.meal.entity.Restaurant;
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
@Table(name = "drink_records")
@Getter
@Setter
public class DrinkRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private FoodGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Restaurant shop;

    @Column(name = "drink_name", nullable = false, length = 160)
    private String drinkName;

    @Column(name = "shop_name", length = 160)
    private String shopName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sweetness_level", length = 20)
    private SweetnessLevel sweetnessLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "ice_level", length = 20)
    private IceLevel iceLevel;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    private String comment;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "would_buy_again")
    private Boolean wouldBuyAgain;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level", nullable = false, length = 20)
    private PrivacyLevel privacyLevel;

    @Column(name = "consumed_at", nullable = false)
    private OffsetDateTime consumedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
