package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    @Query(value = """
        SELECT DISTINCT t FROM Topic t
        LEFT JOIN FETCH t.roles r
        WHERE
            (:types IS NULL OR t.thesisTypes && :types)
            AND (:includeClosed = true OR t.closedAt IS NULL)
            AND (:searchQuery IS NULL 
                OR LOWER(t.title) LIKE %:searchQuery%
                OR LOWER(t.problemStatement) LIKE %:searchQuery%
                OR LOWER(t.requirements) LIKE %:searchQuery%
                OR LOWER(t.goals) LIKE %:searchQuery%)
            AND (:groupId IS NULL OR t.group.id = :groupId)
    """)
    Page<Topic> searchTopics(
        @Param("types") String[] types,
        @Param("includeClosed") boolean includeClosed,
        @Param("searchQuery") String searchQuery,
        @Param("groupId") UUID groupId,
        Pageable pageable
    );

    Page<Topic> findByGroupId(UUID groupId, Pageable pageable);
}
