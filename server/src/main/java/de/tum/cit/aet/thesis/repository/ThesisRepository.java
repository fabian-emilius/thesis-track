package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Thesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThesisRepository extends JpaRepository<Thesis, UUID> {
    List<Thesis> findByTopicGroupGroupId(UUID groupId);
    
    List<Thesis> findByTopicGroupGroupIdIn(List<UUID> groupIds);
}