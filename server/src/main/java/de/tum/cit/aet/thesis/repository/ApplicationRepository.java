package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.constants.ApplicationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Page<Application> findByGroupId(UUID groupId, Pageable pageable);
    
    @Query("SELECT a FROM Application a WHERE a.group.id = :groupId AND a.state = :state")
    Page<Application> findByGroupIdAndState(UUID groupId, ApplicationState state, Pageable pageable);
    
    Optional<Application> findByIdAndGroupId(UUID id, UUID groupId);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.group.id = :groupId AND a.state = :state")
    long countByGroupIdAndState(UUID groupId, ApplicationState state);
    
    List<Application> findByGroupIdAndStateAndReviewerIsNull(UUID groupId, ApplicationState state);

    // Existing methods that need to be maintained
    List<Application> findAllByUser(User user);
    
    List<Application> findAllByTopic(Topic topic);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.state = 'REVIEW' AND a.group.id = :groupId")
    long countUnreviewedApplications(UUID groupId);
    
    @Query("SELECT a FROM Application a WHERE a.user = :user AND a.topic.id = :topicId AND a.group.id = :groupId")
    Optional<Application> findByUserAndTopicIdAndGroupId(User user, UUID topicId, UUID groupId);
    
    default boolean applicationExists(User user, UUID topicId, UUID groupId) {
        return findByUserAndTopicIdAndGroupId(user, topicId, groupId).isPresent();
    }
    
    @Query("SELECT a FROM Application a WHERE " +
           "(:userId IS NULL OR a.user.id = :userId) AND " +
           "(:state IS NULL OR a.state = :state) AND " +
           "a.group.id = :groupId")
    Page<Application> searchApplications(UUID userId, ApplicationState state, UUID groupId, Pageable pageable);
}