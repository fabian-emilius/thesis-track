package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
    List<UserGroup> findByUserId(UUID userId);
    List<UserGroup> findByGroupId(UUID groupId);
    Optional<UserGroup> findByGroupIdAndUserId(UUID groupId, UUID userId);
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
    void deleteByUserId(UUID userId);
    void deleteByGroupId(UUID groupId);
}