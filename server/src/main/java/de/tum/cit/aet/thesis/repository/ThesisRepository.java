package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Thesis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ThesisRepository extends JpaRepository<Thesis, UUID> {
    Page<Thesis> findByTopicGroup(Group group, Pageable pageable);

    @Query("SELECT t FROM Thesis t WHERE t.topic.group = :group OR t.topic.group IS NULL")
    Page<Thesis> findVisibleTheses(@Param("group") Group group, Pageable pageable);
}
