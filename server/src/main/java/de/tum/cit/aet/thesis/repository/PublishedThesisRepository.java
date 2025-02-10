package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PublishedThesisRepository extends JpaRepository<PublishedThesis, UUID> {
    @Query(value = """
        SELECT pt FROM PublishedThesis pt
        WHERE :groupId = ANY(pt.visibilityGroups)
        OR pt.visibilityGroups IS NULL OR array_length(pt.visibilityGroups, 1) IS NULL
    """)
    Page<PublishedThesis> findVisibleTheses(
        @Param("groupId") UUID groupId,
        Pageable pageable
    );

    @Query(value = """
        SELECT CASE WHEN COUNT(pt) > 0 THEN true ELSE false END FROM PublishedThesis pt
        WHERE pt.id = :thesisId
        AND (:groupId = ANY(pt.visibilityGroups)
        OR pt.visibilityGroups IS NULL OR array_length(pt.visibilityGroups, 1) IS NULL)
    """)
    boolean isThesisVisibleToGroup(
        @Param("thesisId") UUID thesisId,
        @Param("groupId") UUID groupId
    );
}
