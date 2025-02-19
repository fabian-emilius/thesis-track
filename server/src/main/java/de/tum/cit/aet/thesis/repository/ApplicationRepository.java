package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Application;
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
}