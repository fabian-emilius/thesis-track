package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    Page<Topic> findByGroupIdAndClosedFalse(UUID groupId, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.group.id = :groupId AND t.closed = false AND t.id NOT IN " +
           "(SELECT a.topic.id FROM Application a WHERE a.state = 'ACCEPTED')")
    Page<Topic> findAvailableTopicsByGroupId(UUID groupId, Pageable pageable);
    
    List<Topic> findByGroupId(UUID groupId);
    
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.group.id = :groupId AND t.closed = false")
    long countActiveTopicsByGroupId(UUID groupId);

    // Existing methods that need to be maintained
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.closed = false")
    long countOpenTopics();
    
    Optional<Topic> findByIdAndGroupId(UUID id, UUID groupId);
    
    @Query("SELECT t FROM Topic t WHERE " +
           "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:closed IS NULL OR t.closed = :closed) AND " +
           "t.group.id = :groupId")
    Page<Topic> searchTopics(String search, Boolean closed, UUID groupId, Pageable pageable);
}