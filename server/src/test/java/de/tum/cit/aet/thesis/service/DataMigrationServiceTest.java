package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataMigrationServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @InjectMocks
    private DataMigrationService migrationService;

    @Captor
    private ArgumentCaptor<Group> groupCaptor;

    @Captor
    private ArgumentCaptor<List<Topic>> topicsCaptor;

    @Captor
    private ArgumentCaptor<UserGroup> userGroupCaptor;

    private static final UUID DEFAULT_GROUP_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private User testUser;
    private Topic testTopic;
    private Group defaultGroup;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());

        testTopic = new Topic();
        testTopic.setId(UUID.randomUUID());

        defaultGroup = new Group();
        defaultGroup.setGroupId(DEFAULT_GROUP_ID);
        defaultGroup.setName("Default Group");
    }

    @Test
    void migrateToGroups_CreatesDefaultGroup_WhenNotExists() {
        when(groupRepository.findById(DEFAULT_GROUP_ID)).thenReturn(Optional.empty());
        when(topicRepository.findByGroupIsNull()).thenReturn(List.of());
        when(userRepository.findAll()).thenReturn(List.of());

        migrationService.migrateToGroups();

        verify(groupRepository).save(groupCaptor.capture());
        Group savedGroup = groupCaptor.getValue();
        assertEquals(DEFAULT_GROUP_ID, savedGroup.getGroupId());
        assertEquals("Default Group", savedGroup.getName());
    }

    @Test
    void migrateToGroups_MigratesTopics_ToDefaultGroup() {
        when(groupRepository.findById(DEFAULT_GROUP_ID)).thenReturn(Optional.of(defaultGroup));
        when(topicRepository.findByGroupIsNull()).thenReturn(List.of(testTopic));
        when(userRepository.findAll()).thenReturn(List.of());

        migrationService.migrateToGroups();

        verify(topicRepository).saveAll(topicsCaptor.capture());
        List<Topic> savedTopics = topicsCaptor.getValue();
        assertFalse(savedTopics.isEmpty());
        assertEquals(defaultGroup, savedTopics.get(0).getGroup());
    }

    @Test
    void migrateToGroups_AddsUsers_ToDefaultGroup() {
        when(groupRepository.findById(DEFAULT_GROUP_ID)).thenReturn(Optional.of(defaultGroup));
        when(topicRepository.findByGroupIsNull()).thenReturn(List.of());
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userGroupRepository.findByUserIdAndGroupId(testUser.getUserId(), DEFAULT_GROUP_ID))
                .thenReturn(null);

        migrationService.migrateToGroups();

        verify(userGroupRepository).save(userGroupCaptor.capture());
        UserGroup savedUserGroup = userGroupCaptor.getValue();
        assertEquals(testUser, savedUserGroup.getUser());
        assertEquals(defaultGroup, savedUserGroup.getGroup());
        assertEquals("MEMBER", savedUserGroup.getRole());
    }

    @Test
    void validateMigration_ReturnsValid_WhenMigrationComplete() {
        when(groupRepository.findById(DEFAULT_GROUP_ID)).thenReturn(Optional.of(defaultGroup));
        when(topicRepository.countByGroupIsNull()).thenReturn(0L);
        when(userRepository.count()).thenReturn(1L);
        when(userGroupRepository.findAll()).thenReturn(List.of(new UserGroup()));

        DataMigrationService.MigrationStatus status = migrationService.validateMigration();

        assertTrue(status.isValid());
        assertTrue(status.defaultGroupExists());
        assertEquals(0, status.topicsWithoutGroup());
        assertEquals(0, status.usersWithoutGroup());
    }

    @Test
    void validateMigration_ReturnsInvalid_WhenMigrationIncomplete() {
        when(groupRepository.findById(DEFAULT_GROUP_ID)).thenReturn(Optional.empty());
        when(topicRepository.countByGroupIsNull()).thenReturn(1L);
        when(userRepository.count()).thenReturn(1L);
        when(userGroupRepository.findAll()).thenReturn(List.of());

        DataMigrationService.MigrationStatus status = migrationService.validateMigration();

        assertFalse(status.isValid());
        assertFalse(status.defaultGroupExists());
        assertEquals(1, status.topicsWithoutGroup());
        assertEquals(1, status.usersWithoutGroup());
    }
}