package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.constants.ThesisState;
import de.tum.cit.aet.thesis.entity.Thesis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ThesisRepository extends JpaRepository<Thesis, UUID> {
    @Query("SELECT t FROM Thesis t " +
            "WHERE t.group.id = :groupId " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:state IS NULL OR t.state = :state) " +
            "AND (:searchQuery IS NULL OR " +
            "    LOWER(t.title) LIKE %:searchQuery% OR " +
            "    LOWER(t.abstractField) LIKE %:searchQuery% OR " +
            "    LOWER(t.info) LIKE %:searchQuery% OR " +
            "    t.keywords && ARRAY[:searchQuery])")
    Page<Thesis> searchTheses(
            @Param("groupId") UUID groupId,
            @Param("type") String type,
            @Param("state") ThesisState state,
            @Param("searchQuery") String searchQuery,
            Pageable pageable
    );

    @Query("SELECT t FROM Thesis t " +
            "WHERE t.group.id = :groupId " +
            "AND t.state = :state " +
            "AND EXISTS (" +
            "    SELECT 1 FROM ThesisRole tr " +
            "    WHERE tr.thesis = t " +
            "    AND tr.user.id = :userId)")
    List<Thesis> findByGroupAndStateAndUser(
            @Param("groupId") UUID groupId,
            @Param("state") ThesisState state,
            @Param("userId") UUID userId
    );

    List<Thesis> findByGroupId(UUID groupId);
}