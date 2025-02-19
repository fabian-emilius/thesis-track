package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ApplicationReviewer;
import de.tum.cit.aet.thesis.entity.key.ApplicationReviewerId;


@Repository
public interface ApplicationReviewerRepository extends JpaRepository<ApplicationReviewer, ApplicationReviewerId> {
}
