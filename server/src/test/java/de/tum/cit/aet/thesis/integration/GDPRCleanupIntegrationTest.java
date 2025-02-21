package de.tum.cit.aet.thesis.integration;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.GDPRDeletionLog;
import de.tum.cit.aet.thesis.repository.UserRepository;
import de.tum.cit.aet.thesis.repository.GDPRDeletionLogRepository;
import de.tum.cit.aet.thesis.service.GDPRCleanupService;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class GDPRCleanupIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GDPRDeletionLogRepository gdprDeletionLogRepository;

    @Autowired
    private GDPRCleanupService gdprCleanupService;

    @Test
    @Transactional
    void testCompleteGDPRDeletionWorkflow() {
        // Create test user with old activity date
        User user = new User();
        user.setUniversityId("test123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setLastActivityDate(LocalDateTime.now().minusYears(11));
        userRepository.save(user);

        // Find eligible users
        List<User> eligibleUsers = gdprCleanupService.findUsersEligibleForDeletion();
        assertFalse(eligibleUsers.isEmpty());
        assertTrue(eligibleUsers.stream().anyMatch(u -> u.getUniversityId().equals("test123")));

        // Schedule deletion
        gdprCleanupService.scheduleUsersForDeletion(eligibleUsers);
        User scheduledUser = userRepository.findByUniversityId("test123").orElseThrow();
        assertNotNull(scheduledUser.getScheduledDeletionDate());
        assertNotNull(scheduledUser.getDeletionNotifiedAt());

        // Execute deletion
        gdprCleanupService.executeScheduledDeletions();

        // Verify user is anonymized
        User deletedUser = userRepository.findByUniversityId("test123").orElseThrow();
        assertEquals("[DELETED]", deletedUser.getFirstName());
        assertEquals("[DELETED]", deletedUser.getLastName());
        assertTrue(deletedUser.getEmail().startsWith("deleted-"));

        // Verify deletion log
        List<GDPRDeletionLog> logs = gdprDeletionLogRepository.findAll();
        assertFalse(logs.isEmpty());
        GDPRDeletionLog log = logs.get(0);
        assertEquals(deletedUser.getId(), log.getUserId());
        assertNotNull(log.getDeletedAt());
    }
}
