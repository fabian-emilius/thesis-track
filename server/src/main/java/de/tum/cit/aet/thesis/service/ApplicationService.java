package de.tum.cit.aet.thesis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.tum.cit.aet.thesis.constants.ApplicationRejectReason;
import de.tum.cit.aet.thesis.constants.ApplicationReviewReason;
import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.constants.ApplicationState;
import de.tum.cit.aet.thesis.entity.key.ApplicationReviewerId;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ApplicationReviewerRepository;
import de.tum.cit.aet.thesis.repository.TopicRepository;

import java.time.Instant;
import java.util.*;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final MailingService mailingService;
    private final TopicRepository topicRepository;
    private final ThesisService thesisService;
    private final TopicService topicService;
    private final ApplicationReviewerRepository applicationReviewerRepository;
    private final GroupService groupService;

    @Autowired
    public ApplicationService(
            ApplicationRepository applicationRepository,
            MailingService mailingService,
            TopicRepository topicRepository,
            ThesisService thesisService,
            TopicService topicService,
            ApplicationReviewerRepository applicationReviewerRepository,
            GroupService groupService) {
        this.applicationRepository = applicationRepository;
        this.mailingService = mailingService;
        this.topicRepository = topicRepository;
        this.thesisService = thesisService;
        this.topicService = topicService;
        this.applicationReviewerRepository = applicationReviewerRepository;
        this.groupService = groupService;
    }

    public Page<Application> getAll(
            UUID userId,
            UUID reviewerId,
            String searchQuery,
            ApplicationState[] states,
            String[] previous,
            String[] topics,
            String[] types,
            boolean includeSuggestedTopics,
            Long groupId,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) {
        Sort.Order order = new Sort.Order(sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        String searchQueryFilter = searchQuery == null || searchQuery.isEmpty() ? null : searchQuery.toLowerCase();
        Set<ApplicationState> statesFilter = states == null || states.length == 0 ? null : new HashSet<>(Arrays.asList(states));
        Set<String> topicsFilter = topics == null || topics.length == 0 ? null : new HashSet<>(Arrays.asList(topics));
        Set<String> typesFilter = types == null || types.length == 0 ? null : new HashSet<>(Arrays.asList(types));
        Set<String> previousFilter = previous == null || previous.length == 0 ? null : new HashSet<>(Arrays.asList(previous));

        return applicationRepository.searchApplications(
                userId,
                statesFilter != null && !statesFilter.contains(ApplicationState.REJECTED) ? reviewerId : null,
                searchQueryFilter,
                statesFilter,
                previousFilter,
                topicsFilter,
                typesFilter,
                includeSuggestedTopics,
                groupId,
                PageRequest.of(page, limit, Sort.by(order))
        );
    }

    @Transactional
    public Application createApplication(User user, UUID topicId, String thesisTitle, String thesisType, Instant desiredStartDate, String motivation, Long groupId) {
        Topic topic = topicId == null ? null : topicService.findById(topicId);
        Group group = groupService.findById(groupId);

        if (topic != null) {
            if (topic.getClosedAt() != null) {
                throw new ResourceInvalidParametersException("This topic is already closed. You cannot submit new applications for it.");
            }
            if (!topic.getGroup().getId().equals(groupId)) {
                throw new ResourceInvalidParametersException("The selected topic does not belong to the specified group.");
            }
        }

        Application application = new Application();
        application.setUser(user);
        application.setTopic(topic);
        application.setThesisTitle(thesisTitle);
        application.setThesisType(thesisType);
        application.setMotivation(motivation);
        application.setComment("");
        application.setState(ApplicationState.NOT_ASSESSED);
        application.setDesiredStartDate(desiredStartDate);
        application.setCreatedAt(Instant.now());
        application.setGroup(group);

        application = applicationRepository.save(application);

        mailingService.sendApplicationCreatedEmail(application);

        return application;
    }

    @Transactional
    public Application updateApplication(Application application, UUID topicId, String thesisTitle, String thesisType, Instant desiredStartDate, String motivation) {
        Topic topic = topicId == null ? null : topicService.findById(topicId);

        if (topic != null && !topic.getGroup().getId().equals(application.getGroup().getId())) {
            throw new ResourceInvalidParametersException("The selected topic does not belong to the same group as the application.");
        }

        application.setTopic(topic);
        application.setThesisTitle(thesisTitle);
        application.setThesisType(thesisType);
        application.setMotivation(motivation);
        application.setDesiredStartDate(desiredStartDate);

        return applicationRepository.save(application);
    }

    @Transactional
    public List<Application> accept(
            User reviewingUser,
            Application application,
            String thesisTitle,
            String thesisType,
            String language,
            List<UUID> advisorIds,
            List<UUID> supervisorIds,
            boolean notifyUser,
            boolean closeTopic
    ) {
        List<Application> result = new ArrayList<>();

        application.setState(ApplicationState.ACCEPTED);
        application.setReviewedAt(Instant.now());

        application = reviewApplication(application, reviewingUser, ApplicationReviewReason.INTERESTED);

        Thesis thesis = thesisService.createThesis(
                reviewingUser,
                thesisTitle,
                thesisType,
                language,
                supervisorIds,
                advisorIds,
                List.of(application.getUser().getId()),
                application,
                application.getGroup(),
                false
        );

        application = applicationRepository.save(application);

        Topic topic = application.getTopic();

        if (topic != null && closeTopic) {
            topic.setClosedAt(Instant.now());

            result.addAll(rejectApplicationsForTopic(reviewingUser, topic, ApplicationRejectReason.TOPIC_FILLED, true));

            application.setTopic(topicRepository.save(topic));
        }

        if (notifyUser) {
            mailingService.sendApplicationAcceptanceEmail(application, thesis);
        }

        result.add(applicationRepository.save(application));

        return result;
    }

    // ... rest of the methods remain the same ...
}