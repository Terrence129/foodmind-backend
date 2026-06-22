package com.foodmind.recommendation.entity;

import com.foodmind.common.enums.RecordType;
import com.foodmind.common.enums.WantToTryStatus;
import com.foodmind.drink.entity.DrinkRecord;
import com.foodmind.meal.entity.MealRecord;
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

import java.time.OffsetDateTime;

@Entity
@Table(name = "want_to_try_items")
@Getter
@Setter
public class WantToTryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_record_type", nullable = false, length = 20)
    private RecordType sourceRecordType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_meal_record_id")
    private MealRecord sourceMealRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_drink_record_id")
    private DrinkRecord sourceDrinkRecord;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WantToTryStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
