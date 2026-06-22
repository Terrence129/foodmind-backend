package com.foodmind.recommendation.entity;

import com.foodmind.common.enums.RecommendationSource;
import com.foodmind.common.enums.RecommendationType;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "recommendation_items")
@Getter
@Setter
public class RecommendationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private RecommendationRequest recommendationRequest;

    @Column(name = "rank_position", nullable = false)
    private Short rankPosition;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "restaurant_name", length = 160)
    private String restaurantName;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false, length = 30)
    private RecommendationType recommendationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RecommendationSource source;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(nullable = false)
    private String reason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "candidate_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> candidateJson;

    @Column(nullable = false)
    private Boolean selected;

    @Column(nullable = false)
    private Boolean rejected;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
