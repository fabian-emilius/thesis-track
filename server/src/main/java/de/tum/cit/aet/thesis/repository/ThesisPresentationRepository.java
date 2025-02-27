package de.tum.cit.aet.thesis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.constants.ThesisPresentationState;
import de.tum.cit.aet.thesis.constants.ThesisPresentationVisibility;
import de.tum.cit.aet.thesis.entity.ThesisPresentation;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Repository for managing thesis presentations.
 * Provides methods for searching, retrieving, and modifying thesis presentation records.
 */
@Repository
public interface ThesisPresentationRepository extends JpaRepository<ThesisPresentation, UUID> {
    /**
     * Finds upcoming presentations based on the specified criteria.
     * 
     * @param time The reference time (presentations after this time will be returned)
     * @param states Optional set of presentation states to filter by
     * @param visibilities Optional set of presentation visibilities to filter by
     * @param page Pagination parameters
     * @return A page of future presentations matching the criteria
     */
    @Query(
            "SELECT p FROM ThesisPresentation p WHERE " +
                    "p.scheduledAt >= :time AND " +
                    "(:states IS NULL OR p.state IN :states) AND " +
                    "(:visibilities IS NULL OR p.visibility IN :visibilities)"
    )
    Page<ThesisPresentation> findFuturePresentations(
            @Param("time") Instant time,
            @Param("states") Set<ThesisPresentationState> states,
            @Param("visibilities") Set<ThesisPresentationVisibility> visibilities,
            Pageable page
    );

    /**
     * Retrieves all presentations with optional visibility filtering.
     * 
     * @param visibilities Optional set of presentation visibilities to filter by
     * @return List of all presentations matching the visibility criteria
     */
    @Query("SELECT p FROM ThesisPresentation p WHERE (:visibilities IS NULL OR p.visibility IN :visibilities)")
    List<ThesisPresentation> findAllPresentations(
            @Param("visibilities") Set<ThesisPresentationVisibility> visibilities
    );
    
    /**
     * Marks presentations created by the specified user as anonymized.
     * This maintains the academic record while removing personal identifiable information.
     * 
     * The location field is anonymized while preserving the academic significance of the
     * presentation for institutional records. This balances GDPR compliance with the need
     * to maintain academic records for statistical and historical purposes.
     *
     * @param userId The ID of the user whose presentations should be marked as anonymized
     * @return The number of presentations updated
     */
    @Modifying
    @Query("UPDATE ThesisPresentation tp SET tp.location = 'Anonymized location' " +
           "WHERE tp.createdBy.id = :userId")
    int markPresentationsAnonymized(@Param("userId") UUID userId);
}
