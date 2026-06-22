package com.foodmind.group.repository;

import com.foodmind.group.entity.GroupFeedItem;
import com.foodmind.group.entity.GroupFeedItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupFeedItemRepository extends JpaRepository<GroupFeedItem, GroupFeedItemId> {
}
