package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    Page<Topic> findByGroupId(UUID groupId, Pageable pageable);
    Optional<Topic> findByGroupIdAndId(UUID groupId, UUID id);
    boolean existsByGroupIdAndId(UUID groupId, UUID id);
    void deleteByGroupId(UUID groupId);
}