package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRoleRepository extends JpaRepository<GroupRole, UUID> {
    List<GroupRole> findByGroupId(UUID groupId);

    List<GroupRole> findByUserId(UUID userId);
}
