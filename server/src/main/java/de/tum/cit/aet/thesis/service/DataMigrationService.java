package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataMigrationService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final UserGroupRepository userGroupRepository;

    private static final UUID DEFAULT_GROUP_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Transactional
    public void migrateToGroups() {
        log.info("Starting data migration to group structure");

        // Create default group if it doesn't exist
        if (groupRepository.findById(DEFAULT_GROUP_ID).isEmpty()) {
            Group defaultGroup = new Group();
            defaultGroup.setGroupId(DEFAULT_GROUP_ID);
            defaultGroup.setName("Default Group");
            defaultGroup.setDescription("Default group for existing topics");
            defaultGroup.setCreatedAt(OffsetDateTime.now());
            defaultGroup.setUpdatedAt(OffsetDateTime.now());
            groupRepository.save(defaultGroup);
            log.info("Created default group");
        }

        // Migrate topics without group to default group
        List<Topic> topicsWithoutGroup = topicRepository.findByGroupIsNull();
        if (!topicsWithoutGroup.isEmpty()) {
            Group defaultGroup = groupRepository.findById(DEFAULT_GROUP_ID).orElseThrow();
            topicsWithoutGroup.forEach(topic -> topic.setGroup(defaultGroup));
            topicRepository.saveAll(topicsWithoutGroup);
            log.info("Migrated {} topics to default group", topicsWithoutGroup.size());
        }

        // Add all users to default group
        userRepository.findAll().forEach(user -> {
            if (userGroupRepository.findByUserIdAndGroupId(user.getUserId(), DEFAULT_GROUP_ID) == null) {
                UserGroup userGroup = new UserGroup();
                userGroup.setId(new UserGroupId(user.getUserId(), DEFAULT_GROUP_ID));
                userGroup.setUser(user);
                userGroup.setGroup(groupRepository.findById(DEFAULT_GROUP_ID).orElseThrow());
                userGroup.setRole("MEMBER");
                userGroupRepository.save(userGroup);
            }
        });
        log.info("Added all users to default group");
    }

    @Transactional(readOnly = true)
    public MigrationStatus validateMigration() {
        MigrationStatus status = new MigrationStatus();

        // Check if default group exists
        status.setDefaultGroupExists(groupRepository.findById(DEFAULT_GROUP_ID).isPresent());

        // Check if all topics have a group
        status.setTopicsWithoutGroup(topicRepository.countByGroupIsNull());

        // Check if all users are in at least one group
        long usersWithoutGroup = userRepository.count() -
                userGroupRepository.findAll().stream()
                        .map(ug -> ug.getUser().getUserId())
                        .distinct()
                        .count();
        status.setUsersWithoutGroup(usersWithoutGroup);

        return status;
    }

    public record MigrationStatus(
            boolean defaultGroupExists,
            long topicsWithoutGroup,
            long usersWithoutGroup
    ) {
        public MigrationStatus() {
            this(false, 0, 0);
        }

        public void setDefaultGroupExists(boolean exists) {
            this.defaultGroupExists = exists;
        }

        public void setTopicsWithoutGroup(long count) {
            this.topicsWithoutGroup = count;
        }

        public void setUsersWithoutGroup(long count) {
            this.usersWithoutGroup = count;
        }

        public boolean isValid() {
            return defaultGroupExists && topicsWithoutGroup == 0 && usersWithoutGroup == 0;
        }
    }
}