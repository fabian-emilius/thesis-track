package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.entity.ApplicationReviewer;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.constants.ApplicationState;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ApplicationReviewerRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationReviewerRepository reviewerRepository;
    private final GroupRepository groupRepository;
    private final AuthenticationService authenticationService;
    private final MailingService mailingService;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository,
                            ApplicationReviewerRepository reviewerRepository,
                            GroupRepository groupRepository,
                            AuthenticationService authenticationService,
                            MailingService mailingService) {
        this.applicationRepository = applicationRepository;
        this.reviewerRepository = reviewerRepository;
        this.groupRepository = groupRepository;
        this.authenticationService = authenticationService;
        this.mailingService = mailingService;
    }

    @Transactional(readOnly = true)
    public Page<Application> getApplications(UUID groupId, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return applicationRepository.findByGroupIdAndTopicTitleContainingIgnoreCase(groupId, search, pageable);
        }
        return applicationRepository.findByGroupId(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnreviewedApplications(UUID groupId) {
        return applicationRepository.countByGroupIdAndState(groupId, ApplicationState.PENDING);
    }

    @Transactional(readOnly = true)
    public Page<Application> getApplicationsByUser(UUID groupId, UUID userId, Pageable pageable) {
        return applicationRepository.findByGroupIdAndUserId(groupId, userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Application> getApplicationsForReview(UUID groupId, UUID userId, Pageable pageable) {
        return applicationRepository.findByGroupIdAndReviewerId(groupId, userId, pageable);
    }

    @Transactional(readOnly = true)
    public Application getApplicationById(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        User currentUser = authenticationService.getCurrentUser();
        if (!application.hasReadAccess(currentUser)) {
            throw new AccessDeniedException("No access to this application");
        }

        return application;
    }

    @Transactional
    public Application createApplication(Application application) {
        User currentUser = authenticationService.getCurrentUser();
        if (application.getTopic() == null) {
            throw new ResourceInvalidParametersException("Topic is required");
        }
        
        application.setUser(currentUser);
        application.setGroup(application.getTopic().getGroup());
        application.setState(ApplicationState.PENDING);
        application.setCreatedAt(Instant.now());

        Application savedApplication = applicationRepository.save(application);
        mailingService.sendApplicationCreatedNotification(savedApplication);

        return savedApplication;
    }

    @Transactional
    public Application updateApplication(UUID id, Application updatedApplication) {
        Application application = getApplicationById(id);

        User currentUser = authenticationService.getCurrentUser();
        if (!application.hasEditAccess(currentUser)) {
            throw new AccessDeniedException("No permission to update this application");
        }

        application.setMotivation(updatedApplication.getMotivation());
        application.setDesiredStartDate(updatedApplication.getDesiredStartDate());
        application.setComment(updatedApplication.getComment());

        return applicationRepository.save(application);
    }

    @Transactional
    public Application reviewApplication(UUID id, ApplicationState newState, String comment) {
        Application application = getApplicationById(id);

        User currentUser = authenticationService.getCurrentUser();
        if (!application.hasManagementAccess(currentUser)) {
            throw new AccessDeniedException("No permission to review this application");
        }

        application.setState(newState);
        application.setComment(comment);
        application.setReviewedAt(Instant.now());

        ApplicationReviewer reviewer = new ApplicationReviewer();
        reviewer.setApplication(application);
        reviewer.setUser(currentUser);
        reviewer.setReviewedAt(Instant.now());
        reviewerRepository.save(reviewer);

        Application savedApplication = applicationRepository.save(application);
        mailingService.sendApplicationReviewedNotification(savedApplication);

        return savedApplication;
    }
}