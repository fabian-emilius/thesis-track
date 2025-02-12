```java
package de.tum.cit.aet.thesis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.TopicRole;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.ResourceAccessDeniedException;
import de.tum.cit.aet.thesis.mock.EntityMockFactory;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.TopicRoleRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private TopicRoleRepository topicRoleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;

    private TopicService topicService;
    private User testUser;
    private Topic testTopic;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        topicService = new TopicService(
                topicRepository,
                topicRoleRepository,
                userRepository,
                groupRepository
        );

        testGroup = EntityMockFactory.createGroup("Test Group");
        testUser = EntityMockFactory.createUserWithGroup("Test", "supervisor", testGroup);
        testTopic = EntityMockFactory.createTopic("Test Topic", testGroup);
    }

    @Test
    void getAll_ReturnsPageOfTopicsForGroup() {
        List<Topic> topics = List.of(testTopic);
        Page<Topic> expectedPage = new PageImpl<>(topics);
        when(topicRepository.searchTopics(
                any(),
                anyBoolean(),
                any(),
                eq(testGroup.getId()),
                any(PageRequest.class)
        )).thenReturn(expectedPage);

        Page<Topic> result = topicService.getAll(
                null,
                true,
                null,
                testGroup.getId(),
                0,
                10,
                "title",
                "asc"
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(topicRepository).searchTopics(
                eq(null),
                eq(true),
                eq(null),
                eq(testGroup.getId()),
                eq(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title")))
        );
    }

    @Test
    void createTopic_WithValidDataAndGroup_CreatesTopic() {
        List<UUID> supervisorIds = List.of(UUID.randomUUID());
        List<UUID> advisorIds = List.of(UUID.randomUUID());

        User supervisor = EntityMockFactory.createUserWithGroup("Supervisor", "supervisor", testGroup);
        User advisor = EntityMockFactory.createUserWithGroup("Advisor", "advisor", testGroup);

        when(groupRepository.findById(testGroup.getId())).thenReturn(Optional.of(testGroup));
        when(userRepository.findAllById(supervisorIds)).thenReturn(new ArrayList<>(List.of(supervisor)));
        when(userRepository.findAllById(advisorIds)).thenReturn(new ArrayList<>(List.of(advisor)));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Topic result = topicService.createTopic(
                testUser,
                testGroup.getId(),
                "Test Topic",
                Set.of("Bachelor"),
                "Problem Statement",
                "Requirements",
                "Goals",
                "References",
                supervisorIds,
                advisorIds
        );

        assertNotNull(result);
        assertEquals(testGroup, result.getGroup());
        verify(topicRepository, times(2)).save(any(Topic.class));
        verify(topicRoleRepository, times(2)).save(any(TopicRole.class));
    }

    @Test
    void createTopic_WithInvalidGroup_ThrowsException() {
        List<UUID> supervisorIds = List.of(UUID.randomUUID());
        List<UUID> advisorIds = List.of(UUID.randomUUID());
        UUID invalidGroupId = UUID.randomUUID();

        when(groupRepository.findById(invalidGroupId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                topicService.createTopic(
                        testUser,
                        invalidGroupId,
                        "Test Topic",
                        Set.of("Bachelor"),
                        "Problem Statement",
                        "Requirements",
                        "Goals",
                        "References",
                        supervisorIds,
                        advisorIds
                )
        );
    }

    @Test
    void createTopic_WithUserFromDifferentGroup_ThrowsException() {
        Group differentGroup = EntityMockFactory.createGroup("Different Group");
        User userFromDifferentGroup = EntityMockFactory.createUserWithGroup("Test", "supervisor", differentGroup);
        List<UUID> supervisorIds = List.of(UUID.randomUUID());
        List<UUID> advisorIds = List.of(UUID.randomUUID());

        when(groupRepository.findById(testGroup.getId())).thenReturn(Optional.of(testGroup));

        assertThrows(ResourceAccessDeniedException.class, () ->
                topicService.createTopic(
                        userFromDifferentGroup,
                        testGroup.getId(),
                        "Test Topic",
                        Set.of("Bachelor"),
                        "Problem Statement",
                        "Requirements",
                        "Goals",
                        "References",
                        supervisorIds,
                        advisorIds
                )
        );
    }

    @Test
    void updateTopic_WithValidDataAndGroup_UpdatesTopic() {
        User supervisor = EntityMockFactory.createUserWithGroup("Supervisor", "supervisor", testGroup);
        User advisor = EntityMockFactory.createUserWithGroup("Advisor", "advisor", testGroup);

        List<UUID> supervisorIds = new ArrayList<>(List.of(supervisor.getId()));
        List<UUID> advisorIds = new ArrayList<>(List.of(advisor.getId()));

        when(groupRepository.findById(testGroup.getId())).thenReturn(Optional.of(testGroup));
        when(userRepository.findAllById(supervisorIds)).thenReturn(new ArrayList<>(List.of(supervisor)));
        when(userRepository.findAllById(advisorIds)).thenReturn(new ArrayList<>(List.of(advisor)));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Topic result = topicService.updateTopic(
                testUser,
                testTopic,
                testGroup.getId(),
                "Updated Topic",
                Set.of("Master"),
                "Updated Problem",
                "Updated Requirements",
                "Updated Goals",
                "Updated References",
                supervisorIds,
                advisorIds
        );

        assertNotNull(result);
        assertEquals("Updated Topic", result.getTitle());
        assertEquals(testGroup, result.getGroup());
        verify(topicRoleRepository).deleteByTopicId(testTopic.getId());
        verify(topicRepository).save(testTopic);
    }

    @Test
    void findById_WithValidIdAndGroup_ReturnsTopic() {
        when(topicRepository.findByIdAndGroupId(testTopic.getId(), testGroup.getId()))
                .thenReturn(Optional.of(testTopic));

        Topic result = topicService.findById(testTopic.getId(), testGroup.getId());

        assertNotNull(result);
        assertEquals(testTopic, result);
    }

    @Test
    void findById_WithInvalidIdOrGroup_ThrowsException() {
        when(topicRepository.findByIdAndGroupId(testTopic.getId(), testGroup.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                topicService.findById(testTopic.getId(), testGroup.getId())
        );
    }
}
```