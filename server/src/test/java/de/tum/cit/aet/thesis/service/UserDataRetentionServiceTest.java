package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ApplicationReviewerRepository;
import de.tum.cit.aet.thesis.repository.NotificationSettingRepository;
import de.tum.cit.aet.thesis.repository.ThesisCommentRepository;
import de.tum.cit.aet.thesis.repository.ThesisPresentationRepository;
import de.tum.cit.aet.thesis.repository.ThesisRoleRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataRetentionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private NotificationSettingRepository notificationSettingRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationReviewerRepository applicationReviewerRepository;

    @Mock
    private ThesisRoleRepository thesisRoleRepository;

    @Mock
    private ThesisCommentRepository thesisCommentRepository;

    @Mock
    private ThesisPresentationRepository thesisPresentationRepository;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UserDataRetentionService userDataRetentionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userDataRetentionService, "userDataRetentionYears", 10);
        ReflectionTestUtils.setField(userDataRetentionService, "batchSize", 100);

        // Set up test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUniversityId("test123");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setGender("Male");
        testUser.setNationality("German");
        testUser.setMatriculationNumber("12345678");
        testUser.setCvFilename("test-cv.pdf");
        testUser.setDegreeFilename("test-degree.pdf");
        testUser.setExaminationFilename("test-exam.pdf");
        testUser.setAvatar("test-avatar.jpg");
        testUser.setJoinedAt(Instant.now().minus(11, ChronoUnit.YEARS));
        testUser.setUpdatedAt(Instant.now().minus(11, ChronoUnit.YEARS));
        testUser.setCustomData(new HashMap<>());
    }

    @Test
    void testIdentifyUsersForDeletion() {
        // Given
        Instant cutoffDate = Instant.now().minus(10, ChronoUnit.YEARS);
        when(userRepository.findByJoinedAtBeforeAndUpdatedAtBefore(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(List.of(testUser));

        // When
        List<User> result = userDataRetentionService.identifyUsersForDeletion();

        // Then
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        verify(userRepository).findByJoinedAtBeforeAndUpdatedAtBefore(any(Instant.class), any(Instant.class), any(Pageable.class));
    }

    @Test
    void testProcessUserDataDeletion() {
        // Given
        when(userRepository.findByJoinedAtBeforeAndUpdatedAtBefore(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(List.of(testUser));

        // When
        int result = userDataRetentionService.processUserDataDeletion();

        // Then
        assertEquals(1, result);
        verify(userRepository).save(testUser);
    }

    @Test
    void testAnonymizeUserData() {
        // Given
        when(uploadService.deleteFile(anyString())).thenReturn(true);

        // When
        userDataRetentionService.anonymizeUserData(testUser);

        // Then
        // Verify personal data is anonymized
        assertEquals("Anonymized", testUser.getFirstName());
        assertEquals("User", testUser.getLastName());
        assertEquals(null, testUser.getGender());
        assertEquals(null, testUser.getNationality());
        assertEquals(null, testUser.getMatriculationNumber());
        assertEquals(null, testUser.getCvFilename());
        assertEquals(null, testUser.getDegreeFilename());
        assertEquals(null, testUser.getExaminationFilename());
        assertEquals(null, testUser.getAvatar());
        assertEquals("true", testUser.getCustomData().get("anonymized"));

        // Verify files are deleted
        verify(uploadService).deleteFile("test-cv.pdf");
        verify(uploadService).deleteFile("test-degree.pdf");
        verify(uploadService).deleteFile("test-exam.pdf");
        verify(uploadService).deleteFile("test-avatar.jpg");

        // Verify notification settings are deleted
        verify(notificationSettingRepository).deleteByUserId(testUser.getId());

        // Verify user is saved
        verify(userRepository).save(testUser);
    }

    @Test
    void testAnonymizeUserData_WithNoFiles() {
        // Given
        testUser.setCvFilename(null);
        testUser.setDegreeFilename(null);
        testUser.setExaminationFilename(null);
        testUser.setAvatar(null);

        // When
        userDataRetentionService.anonymizeUserData(testUser);

        // Then
        // Verify files are not deleted
        verify(uploadService, never()).deleteFile(anyString());

        // Verify notification settings are deleted
        verify(notificationSettingRepository).deleteByUserId(testUser.getId());

        // Verify user is saved
        verify(userRepository).save(testUser);
    }

    @Test
    void testProcessUserDataDeletion_WithNoUsers() {
        // Given
        when(userRepository.findByJoinedAtBeforeAndUpdatedAtBefore(any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(List.of());

        // When
        int result = userDataRetentionService.processUserDataDeletion();

        // Then
        assertEquals(0, result);
        verify(userRepository, never()).save(any(User.class));
    }
}
