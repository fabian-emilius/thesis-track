package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    @Query("SELECT t FROM Topic t " +
            "WHERE t.group.id = :groupId " +
            "AND (:types IS NULL OR t.thesisTypes && :types) " +
            "AND (:includeClosed = true OR t.closedAt IS NULL) " +
            "AND (:searchQuery IS NULL OR " +
            "    LOWER(t.title) LIKE %:searchQuery% OR " +
            "    LOWER(t.problemStatement) LIKE %:searchQuery% OR " +
            "    LOWER(t.requirements) LIKE %:searchQuery% OR " +
            "    LOWER(t.goals) LIKE %:searchQuery%)")
    Page<Topic> searchTopics(
            @Param("groupId") UUID groupId,
            @Param("types") String[] types,
            @Param("includeClosed") boolean includeClosed,
            @Param("searchQuery") String searchQuery,
            Pageable pageable
    );
}