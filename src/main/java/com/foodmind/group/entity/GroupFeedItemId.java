package com.foodmind.group.entity;

import com.foodmind.common.enums.RecordType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupFeedItemId implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", length = 20)
    private RecordType recordType;

    @Column(name = "record_id")
    private Long recordId;
}
