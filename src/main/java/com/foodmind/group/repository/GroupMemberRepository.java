package com.foodmind.group.repository;

import com.foodmind.common.enums.GroupMemberStatus;
import com.foodmind.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroup_IdAndUser_IdAndStatus(Long groupId, Long userId, GroupMemberStatus status);
}
