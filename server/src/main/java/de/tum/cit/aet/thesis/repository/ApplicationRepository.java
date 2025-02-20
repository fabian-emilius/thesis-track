package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Page<Application> findByGroupId(UUID groupId, Pageable pageable);
    Page<Application> findByGroupIdAndUserId(UUID groupId, UUID userId, Pageable pageable);
    Optional<Application> findByGroupIdAndId(UUID groupId, UUID id);
    boolean existsByGroupIdAndId(UUID groupId, UUID id);
    void deleteByGroupId(UUID groupId);
}