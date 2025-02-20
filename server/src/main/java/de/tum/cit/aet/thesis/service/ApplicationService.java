package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final TopicService topicService;
    private final GroupService groupService;
    private final AuthenticationService authenticationService;

    @Transactional(readOnly = true)
    public Page<Application> getAllApplications(UUID groupId, Pageable pageable) {
        return applicationRepository.findByGroupId(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public Application getApplicationById(UUID groupId, UUID id) {
        return applicationRepository.findByGroupIdAndId(groupId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    @Transactional
    public Application createApplication(UUID groupId, UUID topicId, Application application) {
        Group group = groupService.getGroupById(groupId);
        Topic topic = topicService.getTopicById(groupId, topicId);
        User currentUser = authenticationService.getCurrentUser();

        application.setGroup(group);
        application.setTopic(topic);
        application.setUser(currentUser);

        return applicationRepository.save(application);
    }

    @Transactional
    public Application updateApplication(UUID groupId, UUID id, Application applicationDetails) {
        Application application = getApplicationById(groupId, id);
        
        // Update application fields as needed
        // Note: group, topic, and user should not be changed
        
        return applicationRepository.save(application);
    }

    @Transactional
    public void deleteApplication(UUID groupId, UUID id) {
        Application application = getApplicationById(groupId, id);
        applicationRepository.delete(application);
    }

    @Transactional(readOnly = true)
    public Page<Application> getUserApplications(UUID groupId, UUID userId, Pageable pageable) {
        return applicationRepository.findByGroupIdAndUserId(groupId, userId, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isApplicationVisible(UUID groupId, UUID applicationId) {
        return applicationRepository.existsByGroupIdAndId(groupId, applicationId);
    }
}