package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    List<Topic> findByGroupGroupId(UUID groupId);
    
    Page<Topic> findByGroupGroupIdInAndClosedAtIsNull(List<UUID> groupIds, Pageable pageable);
    
    List<Topic> findByGroupGroupIdAndClosedAtIsNull(UUID groupId);
    
    List<Topic> findByGroupIsNull();
    
    long countByGroupIsNull();
}