package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.config.DataRetentionConfig;
import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {
    "data-retention.user-data-years=10",
    "data-retention.batch-size=50",
    "data-retention.schedule=0 0 2 * * *"
})
class UserDataRetentionServiceIntegrationTest extends BaseIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DataRetentionConfig dataRetentionConfig() {
            DataRetentionConfig config = new DataRetentionConfig();
            config.setUserDataYears(10);
            config.setBatchSize(50);
            config.setSchedule("0 0 2 * * *");
            return config;
        }
    }

    @Autowired
    private UserDataRetentionService userDataRetentionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UploadService uploadService;

    private User createTestUser(Instant joinedAt, Instant updatedAt) {
        User user = new User();
        user.setUniversityId("test-" + System.currentTimeMillis());
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setJoinedAt(joinedAt);
        user.setUpdatedAt(updatedAt);
        user.setCvFilename("test-cv.pdf");
        return userRepository.save(user);
    }

    private Application createTestApplication(User user, Instant createdAt) {
        Application application = new Application();
        application.setUser(user);
        application.setMotivation("Test motivation");
        application.setCreatedAt(createdAt);
        application.setDesiredStartDate(Instant.now());
        return applicationRepository.save(application);
    }

    @Test
    void findUsersForDeletion_ShouldIdentifyOldInactiveUsers() {
        // Given
        Instant oldDate = Instant.now().minus(11, ChronoUnit.YEARS);
        User oldUser = createTestUser(oldDate, oldDate);
        User recentUser = createTestUser(Instant.now(), Instant.now());

        // When
        List<User> usersForDeletion = userDataRetentionService.findUsersForDeletion();

        // Then
        assertTrue(usersForDeletion.contains(oldUser));
        assertFalse(usersForDeletion.contains(recentUser));
    }

    @Test
    void findUsersForDeletion_ShouldExcludeUsersWithRecentActivity() {
        // Given
        Instant oldDate = Instant.now().minus(11, ChronoUnit.YEARS);
        User userWithRecentActivity = createTestUser(oldDate, oldDate);
        createTestApplication(userWithRecentActivity, Instant.now().minus(6, ChronoUnit.MONTHS));

        // When
        List<User> usersForDeletion = userDataRetentionService.findUsersForDeletion();

        // Then
        assertFalse(usersForDeletion.contains(userWithRecentActivity));
    }

    @Test
    void processDataRetention_ShouldAnonymizeEligibleUsers() {
        // Given
        Instant oldDate = Instant.now().minus(11, ChronoUnit.YEARS);
        User oldUser = createTestUser(oldDate, oldDate);
        String originalEmail = oldUser.getEmail();

        // When
        userDataRetentionService.processDataRetention();

        // Then
        User anonymizedUser = userRepository.findById(oldUser.getId()).orElseThrow();
        assertNotEquals(originalEmail, anonymizedUser.getEmail());
        assertEquals("Anonymized", anonymizedUser.getFirstName());
        assertEquals("User", anonymizedUser.getLastName());
        assertNull(anonymizedUser.getCvFilename());
    }

    @Test
    void processDataRetention_ShouldHandleMultipleUsers() {
        // Given
        Instant oldDate = Instant.now().minus(11, ChronoUnit.YEARS);
        int userCount = 5;
        for (int i = 0; i < userCount; i++) {
            createTestUser(oldDate, oldDate);
        }

        // When
        userDataRetentionService.processDataRetention();

        // Then
        List<User> remainingUsers = userRepository.findAll();
        for (User user : remainingUsers) {
            assertEquals("Anonymized", user.getFirstName());
        }
    }
}
