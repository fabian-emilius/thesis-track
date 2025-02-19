package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    List<GroupMember> findAllByGroupId(UUID groupId);
    
    List<GroupMember> findAllByUserId(UUID userId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.user.id = :userId AND gm.role = :role")
    Optional<GroupMember> findByGroupAndUserAndRole(UUID groupId, UUID userId, GroupRole role);
    
    boolean existsByGroupIdAndUserIdAndRole(UUID groupId, UUID userId, GroupRole role);
    
    void deleteByGroupIdAndUserId(UUID groupId, UUID userId);
}