package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    Page<Topic> findByGroupIdAndClosedAtIsNull(UUID groupId, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.group.id = :groupId AND t.closedAt IS NULL AND " +
           "EXISTS (SELECT tr FROM TopicRole tr WHERE tr.topic = t AND tr.user.id = :userId)")
    Page<Topic> findByGroupIdAndUserIdAndClosedAtIsNull(UUID groupId, UUID userId, Pageable pageable);
    
    List<Topic> findByGroupId(UUID groupId);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.group.id = :groupId AND t.closedAt IS NULL")
    Long countOpenTopics(UUID groupId);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.group.id = :groupId AND t.closedAt IS NULL")
    Long countOpenTopics(UUID groupId);
}