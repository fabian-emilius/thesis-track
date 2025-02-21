package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.GDPRDeletionLog;
import de.tum.cit.aet.thesis.repository.UserRepository;
import de.tum.cit.aet.thesis.repository.GDPRDeletionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GDPRCleanupServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GDPRDeletionLogRepository gdprDeletionLogRepository;

    @Mock
    private MailingService mailingService;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private GDPRCleanupService gdprCleanupService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gdprCleanupService, "retentionYears", 10);
        ReflectionTestUtils.setField(gdprCleanupService, "notificationDays", 30);
        ReflectionTestUtils.setField(gdprCleanupService, "uploadBasePath", "/uploads");
    }

    @Test
    void findUsersEligibleForDeletion_ShouldReturnInactiveUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setLastActivityDate(LocalDateTime.now().minusYears(11));

        when(userRepository.findByLastActivityDateBeforeAndScheduledDeletionDateIsNull(any()))
                .thenReturn(Arrays.asList(user1));

        // Act
        List<User> result = gdprCleanupService.findUsersEligibleForDeletion();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository).findByLastActivityDateBeforeAndScheduledDeletionDateIsNull(any());
    }

    @Test
    void scheduleUsersForDeletion_ShouldScheduleAndNotify() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        List<User> users = Arrays.asList(user);

        // Act
        gdprCleanupService.scheduleUsersForDeletion(users);

        // Assert
        verify(userRepository).save(user);
        verify(mailingService).sendGDPRDeletionNotification(eq(user), any());
        assertNotNull(user.getScheduledDeletionDate());
        assertNotNull(user.getDeletionNotifiedAt());
    }

    @Test
    void executeScheduledDeletions_ShouldDeleteAndLog() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");

        when(userRepository.findByScheduledDeletionDateBefore(any()))
                .thenReturn(Arrays.asList(user));

        // Act
        gdprCleanupService.executeScheduledDeletions();

        // Assert
        verify(userRepository).save(user);
        verify(gdprDeletionLogRepository).save(any(GDPRDeletionLog.class));
        assertEquals("[DELETED]", user.getFirstName());
        assertEquals("[DELETED]", user.getLastName());
        assertTrue(user.getEmail().startsWith("deleted-"));
    }
}
