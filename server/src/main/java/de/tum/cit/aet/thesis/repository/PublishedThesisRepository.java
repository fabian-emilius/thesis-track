package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PublishedThesisRepository extends JpaRepository<PublishedThesis, UUID> {
    @Query("SELECT pt FROM PublishedThesis pt WHERE :groupId = ANY(pt.visibilityGroups) OR pt.visibilityGroups IS NULL")
    List<PublishedThesis> findVisibleTheses(@Param("groupId") UUID groupId);
}
