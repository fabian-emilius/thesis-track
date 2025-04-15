package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.entity.jsonb.ThesisMetadata;
import de.tum.cit.aet.thesis.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDataRetentionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThesisRepository thesisRepository;

    @Mock
    private ThesisRoleRepository thesisRoleRepository;

    @Mock
    private ThesisCommentRepository thesisCommentRepository;

    @Mock
    private ThesisFileRepository thesisFileRepository;

    @Mock
    private ThesisPresentationRepository thesisPresentationRepository;

    @Mock
    private ThesisAssessmentRepository thesisAssessmentRepository;

    @Mock
    private NotificationSettingRepository notificationSettingRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private DataDeletionAuditRepository dataDeletionAuditRepository;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UserDataRetentionService userDataRetentionService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    public void setup() {
        // Configure retention period for tests
        ReflectionTestUtils.setField(userDataRetentionService, "retentionPeriodYears", 10);
        ReflectionTestUtils.setField(userDataRetentionService, "batchSize", 5);

        // Setup test data
        userId = UUID.randomUUID();
        testUser = createTestUser(userId);
        
        // Mock UploadService.getRootLocation()
        when(uploadService.getRootLocation()).thenReturn(Path.of("/tmp/uploads"));
    }

    @Test
    public void testIdentifyUsersForDeletion() {
        // Given
        Instant thresholdTime = Instant.now().minus(365 * 10L, ChronoUnit.DAYS);
        when(userRepository.findByRetentionStartDateBefore(any(Instant.class), any(PageRequest.class)))
                .thenReturn(List.of(testUser));

        // When
        List<User> result = userDataRetentionService.identifyUsersForDeletion();

        // Then
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getId());
        verify(userRepository).findByRetentionStartDateBefore(any(Instant.class), any(PageRequest.class));
    }

    @Test
    public void testProcessDataRetention_Success() {
        // Given
        when(userRepository.findByRetentionStartDateBefore(any(Instant.class), any(PageRequest.class)))
                .thenReturn(List.of(testUser));
        // Support for all required methods
        mockDeleteOperations();

        // When
        int result = userDataRetentionService.processDataRetention();

        // Then
        assertEquals(1, result);
        verify(dataDeletionAuditRepository).save(any(DataDeletionAudit.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testProcessDataRetention_WithError() {
        // Given
        when(userRepository.findByRetentionStartDateBefore(any(Instant.class), any(PageRequest.class)))
                .thenReturn(List.of(testUser));
        // Make thesisRoleRepository.deleteAll throw an exception
        doThrow(new RuntimeException("Test exception"))
                .when(thesisRoleRepository).deleteAll(anyList());

        // When
        int result = userDataRetentionService.processDataRetention();

        // Then
        assertEquals(0, result); // No users processed due to error
        verify(dataDeletionAuditRepository).save(any(DataDeletionAudit.class));
    }

    @Test
    public void testDeleteUserDataManually_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        mockDeleteOperations();

        // When
        boolean result = userDataRetentionService.deleteUserDataManually(userId);

        // Then
        assertTrue(result);
        verify(dataDeletionAuditRepository).save(any(DataDeletionAudit.class));
        verify(userRepository).save(testUser);
    }

    @Test
    public void testDeleteUserDataManually_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        boolean result = userDataRetentionService.deleteUserDataManually(userId);

        // Then
        assertFalse(result);
        verifyNoInteractions(dataDeletionAuditRepository);
    }

    @Test
    public void testDeleteUserDataManually_Error() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Test exception"))
                .when(thesisRoleRepository).deleteAll(anyList());

        // When
        boolean result = userDataRetentionService.deleteUserDataManually(userId);

        // Then
        assertFalse(result);
        verify(dataDeletionAuditRepository).save(any(DataDeletionAudit.class));
    }

    @Test
    public void testGetAuditRecords() {
        // Given
        when(dataDeletionAuditRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new DataDeletionAudit())));

        // When
        var result = userDataRetentionService.getAuditRecords(0, 10);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(dataDeletionAuditRepository).findAll(any(PageRequest.class));
    }

    @Test
    public void testGetAuditRecordsByType() {
        // Given
        when(dataDeletionAuditRepository.findByDeletionType(any(DataDeletionAudit.DeletionType.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new DataDeletionAudit())));

        // When
        var result = userDataRetentionService.getAuditRecordsByType(
                DataDeletionAudit.DeletionType.ADMIN_TRIGGERED, 0, 10);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(dataDeletionAuditRepository).findByDeletionType(
                eq(DataDeletionAudit.DeletionType.ADMIN_TRIGGERED), any(PageRequest.class));
    }

    @Test
    public void testGetAuditRecordsByStatus() {
        // Given
        when(dataDeletionAuditRepository.findByStatus(any(DataDeletionAudit.DeletionStatus.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new DataDeletionAudit())));

        // When
        var result = userDataRetentionService.getAuditRecordsByStatus(
                DataDeletionAudit.DeletionStatus.SUCCESS, 0, 10);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(dataDeletionAuditRepository).findByStatus(
                eq(DataDeletionAudit.DeletionStatus.SUCCESS), any(PageRequest.class));
    }

    @Test
    public void testUserAnonymization() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        mockDeleteOperations();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        userDataRetentionService.deleteUserDataManually(userId);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User anonymizedUser = userCaptor.getValue();
        
        // Check anonymization
        assertNull(anonymizedUser.getEmail());
        assertNull(anonymizedUser.getFirstName());
        assertNull(anonymizedUser.getLastName());
        assertTrue(anonymizedUser.getMatriculationNumber().startsWith("ANONYMIZED-"));
        assertTrue(anonymizedUser.getCustomData().isEmpty());
    }

    @Test
    public void testThesisAnonymization() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        mockDeleteOperations();
        
        Thesis thesis = new Thesis();
        thesis.setId(UUID.randomUUID());
        Map<String, String> titles = new HashMap<>();
        titles.put("en", "Test Thesis");
        Map<UUID, Number> credits = new HashMap<>();
        thesis.setMetadata(new ThesisMetadata(titles, credits));
        
        when(thesisRepository.findThesesByStudentId(userId)).thenReturn(List.of(thesis));
        ArgumentCaptor<Thesis> thesisCaptor = ArgumentCaptor.forClass(Thesis.class);

        // When
        userDataRetentionService.deleteUserDataManually(userId);

        // Then
        verify(thesisRepository).save(thesisCaptor.capture());
        Thesis anonymizedThesis = thesisCaptor.getValue();
        
        // Check anonymization flags in metadata
        assertEquals("true", anonymizedThesis.getMetadata().titles().get("anonymized"));
        assertEquals("ANONYMIZED", anonymizedThesis.getMetadata().titles().get("studentMatriculationNumber"));
    }

    private User createTestUser(UUID id) {
        User user = new User();
        user.setId(id);
        user.setUniversityId("tu123456");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setMatriculationNumber("12345678");
        user.setRetentionStartDate(Instant.now().minus(11 * 365, ChronoUnit.DAYS)); // 11 years ago
        user.setCvFilename("cv.pdf");
        user.setCustomData(Map.of("program", "Computer Science"));
        user.setGroups(new HashSet<>());
        user.setNotificationSettings(new ArrayList<>());
        return user;
    }

    private void mockDeleteOperations() {
        // Mock file operations
        List<ThesisFile> files = new ArrayList<>();
        ThesisFile file = new ThesisFile();
        file.setFilename("test.pdf");
        files.add(file);
        when(thesisFileRepository.findByUploadedBy(testUser)).thenReturn(files);
        
        // Mock thesis operations
        List<Thesis> theses = new ArrayList<>();
        Thesis thesis = new Thesis();
        thesis.setId(UUID.randomUUID());
        thesis.setMetadata(ThesisMetadata.getEmptyMetadata());
        theses.add(thesis);
        when(thesisRepository.findThesesByStudentId(userId)).thenReturn(theses);
        
        // Mock other operations
        when(thesisCommentRepository.findByCreatedBy(testUser)).thenReturn(new ArrayList<>());
        when(thesisAssessmentRepository.findByCreatedBy(testUser)).thenReturn(new ArrayList<>());
        when(thesisPresentationRepository.findByCreatedBy(testUser)).thenReturn(new ArrayList<>());
        when(thesisRoleRepository.findByUser(testUser)).thenReturn(new ArrayList<>());
    }
}