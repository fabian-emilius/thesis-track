package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

class GroupMigrationTest extends BaseIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private ThesisRepository thesisRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    @Sql({"/db/changelog/changes/08_groups.sql", "/db/changelog/changes/09_migrate_existing_data_to_groups.sql"})
    void testDataMigration() {
        // Verify default group was created
        Group defaultGroup = groupRepository.findBySlug("default-group")
                .orElseThrow(() -> new AssertionError("Default group not found"));
        
        assertEquals("Default Research Group", defaultGroup.getName());

        // Verify all theses were migrated
        assertTrue(thesisRepository.findAll().stream()
                .allMatch(thesis -> thesis.getGroupId() != null));

        // Verify all topics were migrated
        assertTrue(topicRepository.findAll().stream()
                .allMatch(topic -> topic.getGroupId() != null));

        // Verify all applications were migrated
        assertTrue(applicationRepository.findAll().stream()
                .allMatch(application -> application.getGroupId() != null));

        // Verify group members were created
        assertFalse(groupMemberRepository.findByGroupId(defaultGroup.getId()).isEmpty());

        // Verify roles were correctly assigned
        assertTrue(groupMemberRepository.findByGroupId(defaultGroup.getId()).stream()
                .anyMatch(member -> member.getRole() == GroupRole.SUPERVISOR));
        assertTrue(groupMemberRepository.findByGroupId(defaultGroup.getId()).stream()
                .anyMatch(member -> member.getRole() == GroupRole.ADVISOR));
    }
}
