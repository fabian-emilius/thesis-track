package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.key.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    List<GroupMember> findByGroupId(UUID groupId);
    List<GroupMember> findByUserId(UUID userId);
    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
}