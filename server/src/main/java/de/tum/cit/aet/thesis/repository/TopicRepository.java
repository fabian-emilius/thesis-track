package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    List<Topic> findByGroup(Group group);
    List<Topic> findByGroupId(UUID groupId);
}