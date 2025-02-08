package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.constants.ThesisState;
import de.tum.cit.aet.thesis.constants.ThesisVisibility;
import de.tum.cit.aet.thesis.entity.Thesis;
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
public interface ThesisRepository extends JpaRepository<Thesis, UUID> {
    @Query("SELECT t FROM Thesis t " +
            "WHERE t.group.id = :groupId " +
            "AND (:searchQuery IS NULL OR LOWER(t.title) LIKE %:searchQuery%) " +
            "AND (:state IS NULL OR t.state = :state) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (t.visibility = :visibility OR t.visibility = 'PUBLIC')")
    Page<Thesis> searchTheses(
            @Param("groupId") Long groupId,
            @Param("searchQuery") String searchQuery,
            @Param("state") ThesisState state,
            @Param("type") String type,
            @Param("visibility") ThesisVisibility visibility,
            Pageable pageable
    );

    @Query("SELECT t FROM Thesis t " +
            "WHERE t.group.id = :groupId " +
            "AND t.state = :state " +
            "AND EXISTS (SELECT r FROM ThesisRole r WHERE r.thesis = t AND r.user.id = :userId)")
    List<Thesis> findByUserAndState(
            @Param("groupId") Long groupId,
            @Param("userId") UUID userId,
            @Param("state") ThesisState state
    );

    Optional<Thesis> findByIdAndGroupId(UUID id, Long groupId);
}