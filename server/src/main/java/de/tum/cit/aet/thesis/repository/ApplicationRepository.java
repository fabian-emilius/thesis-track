package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.constants.ApplicationState;
import de.tum.cit.aet.thesis.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    @Query("SELECT a FROM Application a " +
            "WHERE a.group.id = :groupId " +
            "AND (:userId IS NULL OR a.user.id = :userId) " +
            "AND (:reviewerId IS NULL OR EXISTS (SELECT r FROM ApplicationReviewer r WHERE r.application = a AND r.user.id = :reviewerId)) " +
            "AND (:searchQuery IS NULL OR LOWER(a.thesisTitle) LIKE %:searchQuery% OR LOWER(a.motivation) LIKE %:searchQuery%) " +
            "AND (:states IS NULL OR a.state IN :states) " +
            "AND (:topics IS NULL OR a.topic.title IN :topics) " +
            "AND (:types IS NULL OR a.thesisType IN :types) " +
            "AND (:includeSuggestedTopics = true OR a.topic IS NOT NULL)")
    Page<Application> searchApplications(
            @Param("userId") UUID userId,
            @Param("reviewerId") UUID reviewerId,
            @Param("searchQuery") String searchQuery,
            @Param("states") Set<ApplicationState> states,
            @Param("previous") Set<String> previous,
            @Param("topics") Set<String> topics,
            @Param("types") Set<String> types,
            @Param("includeSuggestedTopics") boolean includeSuggestedTopics,
            @Param("groupId") Long groupId,
            Pageable pageable
    );

    @Query("SELECT a FROM Application a " +
            "WHERE a.group.id = :groupId " +
            "AND a.topic.id = :topicId " +
            "AND a.state = 'NOT_ASSESSED'")
    List<Application> findPendingApplicationsByTopic(
            @Param("groupId") Long groupId,
            @Param("topicId") UUID topicId
    );

    Optional<Application> findByIdAndGroupId(UUID id, Long groupId);
}