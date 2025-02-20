package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.constants.ApplicationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Page<Application> findByGroupId(UUID groupId, Pageable pageable);
    
    Page<Application> findByGroupIdAndUserId(UUID groupId, UUID userId, Pageable pageable);
    
    @Query("SELECT a FROM Application a WHERE a.group.id = :groupId AND " +
           "EXISTS (SELECT ar FROM ApplicationReviewer ar WHERE ar.application = a AND ar.user.id = :userId)")
    Page<Application> findByGroupIdAndReviewerId(UUID groupId, UUID userId, Pageable pageable);
    
    List<Application> findByGroupIdAndState(UUID groupId, ApplicationState state);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.group.id = :groupId AND a.state = :state")
    Long countUnreviewedApplications(UUID groupId, ApplicationState state);
}