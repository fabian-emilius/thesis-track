package de.tum.cit.aet.thesis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.constants.ThesisCommentType;
import de.tum.cit.aet.thesis.entity.ThesisComment;

import java.util.UUID;

/**
 * Repository for managing thesis comments.
 * Provides methods for searching, anonymizing, and managing comments on theses.
 */
@Repository
public interface ThesisCommentRepository extends JpaRepository<ThesisComment, UUID> {
    /**
     * Searches for comments on a thesis matching the specified type.
     * Results are ordered by creation date in descending order (newest first).
     *
     * @param thesisId The ID of the thesis to search comments for
     * @param commentType The type of comments to retrieve
     * @param page Pagination parameters
     * @return A page of matching thesis comments
     */
    @Query(
            "SELECT DISTINCT c FROM ThesisComment c WHERE " +
            "c.thesis.id = :thesisId AND c.type = :commentType " +
            "ORDER BY c.createdAt DESC"
    )
    Page<ThesisComment> searchComments(
            @Param("thesisId") UUID thesisId,
            @Param("commentType") ThesisCommentType commentType,
            Pageable page
    );
    
    /**
     * Anonymizes all comments created by the specified user by setting a placeholder message.
     * This maintains the comment history while removing personal information.
     *
     * The method preserves the academic value of comments for institutional memory and
     * educational continuity while complying with GDPR requirements by removing personal content.
     *
     * @param userId The ID of the user whose comments should be anonymized
     * @return The number of comments anonymized
     */
    @Modifying
    @Query("UPDATE ThesisComment tc SET tc.message = 'This comment has been anonymized in compliance with data protection regulations.' " +
           "WHERE tc.createdBy.id = :userId")
    int anonymizeUserComments(@Param("userId") UUID userId);
}
