package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ThesisAssessment;
import de.tum.cit.aet.thesis.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThesisAssessmentRepository extends JpaRepository<ThesisAssessment, UUID> {
    /**
     * Find all assessments created by a specific user
     * @param createdBy The creator
     * @return List of assessments
     */
    List<ThesisAssessment> findByCreatedBy(User createdBy);
}
