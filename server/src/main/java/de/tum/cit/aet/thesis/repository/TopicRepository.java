package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing Topic entities.
 * Implements group-based filtering and secure parameter handling.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    @Query(value = "SELECT t FROM Topic t " +
            "WHERE t.group.id = :groupId " +
            "AND t.closedAt IS NULL " +
            "AND (:searchQuery IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
            "    OR LOWER(t.problemStatement) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
            "AND (:thesisType IS NULL OR :thesisType MEMBER OF t.thesisTypes)")
    Page<Topic> searchTopics(
            @Param("groupId") Long groupId,
            @Param("searchQuery") String searchQuery,
            @Param("thesisType") String thesisType,
            Pageable pageable
    );

    @Query("SELECT t FROM Topic t " +
            "WHERE t.group.id = :groupId " +
            "AND t.closedAt IS NULL " +
            "AND :thesisType MEMBER OF t.thesisTypes")
    List<Topic> findAvailableTopicsByType(
            @Param("groupId") Long groupId,
            @Param("thesisType") String thesisType
    );

    Optional<Topic> findByIdAndGroupId(UUID id, Long groupId);
}