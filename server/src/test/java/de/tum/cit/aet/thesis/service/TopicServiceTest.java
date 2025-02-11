package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.TopicDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TopicService topicService;

    private User currentUser;
    private Group testGroup;
    private Topic testTopic;
    private UserGroup userGroup;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUserId(UUID.randomUUID());

        testGroup = new Group();
        testGroup.setGroupId(UUID.randomUUID());
        testGroup.setName("Test Group");

        testTopic = new Topic();
        testTopic.setId(UUID.randomUUID());
        testTopic.setGroup(testGroup);
        testTopic.setTitle("Test Topic");
        testTopic.setCreatedBy(currentUser);

        userGroup = new UserGroup();
        userGroup.setUser(currentUser);
        userGroup.setGroup(testGroup);
        userGroup.setRole("MEMBER");

        when(authenticationService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void getTopicsByGroup_WithAccess_ReturnsTopics() {
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(userGroup);
        when(topicRepository.findByGroupGroupId(testGroup.getGroupId()))
                .thenReturn(List.of(testTopic));

        List<TopicDto> topics = topicService.getTopicsByGroup(testGroup.getGroupId());

        assertFalse(topics.isEmpty());
        assertEquals(testTopic.getTitle(), topics.get(0).getTitle());
    }

    @Test
    void getTopicsByGroup_WithoutAccess_ThrowsException() {
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(null);

        assertThrows(AccessDeniedException.class, () ->
                topicService.getTopicsByGroup(testGroup.getGroupId()));
    }

    @Test
    void getOpenTopics_ReturnsPageOfTopics() {
        when(userGroupRepository.findByUserId(currentUser.getUserId()))
                .thenReturn(List.of(userGroup));
        when(topicRepository.findByGroupGroupIdInAndClosedAtIsNull(
                any(List.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testTopic)));

        var result = topicService.getOpenTopics(Pageable.unpaged());

        assertFalse(result.isEmpty());
        assertEquals(testTopic.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void createTopic_WithAccess_CreatesTopic() {
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(userGroup);
        when(groupRepository.findById(testGroup.getGroupId()))
                .thenReturn(Optional.of(testGroup));
        when(topicRepository.save(any(Topic.class)))
                .thenReturn(testTopic);

        TopicDto created = topicService.createTopic(
                testGroup.getGroupId(),
                "Test Topic",
                Set.of("BACHELOR"),
                "Problem",
                "Requirements",
                "Goals",
                "References"
        );

        assertNotNull(created);
        assertEquals(testTopic.getTitle(), created.getTitle());
    }

    @Test
    void updateTopic_WithAccess_UpdatesTopic() {
        when(topicRepository.findById(testTopic.getId()))
                .thenReturn(Optional.of(testTopic));
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(userGroup);
        when(topicRepository.save(any(Topic.class)))
                .thenReturn(testTopic);

        TopicDto updated = topicService.updateTopic(
                testTopic.getId(),
                "Updated Topic",
                Set.of("BACHELOR"),
                "Updated Problem",
                "Updated Requirements",
                "Updated Goals",
                "Updated References"
        );

        assertNotNull(updated);
        assertEquals("Updated Topic", testTopic.getTitle());
    }
}