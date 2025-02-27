package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ThesisCommentRepository;
import de.tum.cit.aet.thesis.repository.ThesisPresentationRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDataRetentionServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private ThesisCommentRepository thesisCommentRepository;
    
    @Mock
    private ThesisPresentationRepository thesisPresentationRepository;
    
    @Mock
    private UploadService uploadService;
    
    @InjectMocks
    private UserDataRetentionService userDataRetentionService;
    
    private User testUser;
    private User emptyUser;
    
    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userDataRetentionService, "userDataRetentionYears", 10);
        ReflectionTestUtils.setField(userDataRetentionService, "batchSize", 100);
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUniversityId("test123");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setMatriculationNumber("12345678");
        testUser.setGender("Male");
        testUser.setNationality("German");
        testUser.setCvFilename("cv-123.pdf");
        testUser.setDegreeFilename("degree-123.pdf");
        testUser.setExaminationFilename("exam-123.pdf");
        
        emptyUser = new User();
        emptyUser.setId(UUID.randomUUID());
        emptyUser.setUniversityId("empty456");
        // User with no files or personal data
    }
    
    @Test
    public void testIdentifyUsersForDeletion() {
        // Given
        Instant cutoffDate = Instant.now().minus(10, ChronoUnit.YEARS);
        when(userRepository.findUsersWithNoRecentActivity(any(Instant.class), anyInt()))
                .thenReturn(Collections.singletonList(testUser));
        
        // When
        List<User> result = userDataRetentionService.identifyUsersForDeletion(100);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository).findUsersWithNoRecentActivity(any(Instant.class), eq(100));
    }
    
    @Test
    public void testIdentifyUsersForDeletionWithCustomYears() {
        // Given
        ReflectionTestUtils.setField(userDataRetentionService, "userDataRetentionYears", 5);
        ArgumentCaptor<Instant> cutoffCaptor = ArgumentCaptor.forClass(Instant.class);
        when(userRepository.findUsersWithNoRecentActivity(any(Instant.class), anyInt()))
                .thenReturn(Collections.singletonList(testUser));
        
        // When
        userDataRetentionService.identifyUsersForDeletion(100);
        
        // Then
        verify(userRepository).findUsersWithNoRecentActivity(cutoffCaptor.capture(), eq(100));
        
        // Check that cutoff is approximately 5 years ago (within 1 day tolerance for test execution time)
        Instant expectedCutoff = Instant.now().minus(5, ChronoUnit.YEARS);
        long differenceInDays = ChronoUnit.DAYS.between(cutoffCaptor.getValue(), expectedCutoff);
        assertTrue(Math.abs(differenceInDays) <= 1, "Cutoff date should be approximately 5 years ago");
    }
    
    @Test
    public void testAnonymizeUserData() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(thesisCommentRepository.anonymizeUserComments(any(UUID.class))).thenReturn(5);
        when(applicationRepository.markApplicationsAnonymized(any(UUID.class))).thenReturn(3);
        when(thesisPresentationRepository.markPresentationsAnonymized(any(UUID.class))).thenReturn(2);
        
        // When
        userDataRetentionService.anonymizeUserData(testUser);
        
        // Then
        // Check that personal data is anonymized
        assertEquals("Anonymized", testUser.getFirstName());
        assertEquals("User", testUser.getLastName());
        assertTrue(testUser.getEmail().toString().contains("anonymized"));
        assertTrue(testUser.getEmail().toString().contains(testUser.getId().toString()));
        assertNull(testUser.getGender());
        assertNull(testUser.getNationality());
        assertNull(testUser.getMatriculationNumber());
        assertNull(testUser.getCvFilename());
        assertNull(testUser.getDegreeFilename());
        assertNull(testUser.getExaminationFilename());
        assertNull(testUser.getAvatar());
        
        // Verify that all files are deleted
        verify(uploadService, times(1)).deleteFile("cv-123.pdf");
        verify(uploadService, times(1)).deleteFile("degree-123.pdf");
        verify(uploadService, times(1)).deleteFile("exam-123.pdf");
        
        // Verify that academic records are anonymized
        verify(thesisCommentRepository).anonymizeUserComments(testUser.getId());
        verify(applicationRepository).markApplicationsAnonymized(testUser.getId());
        verify(thesisPresentationRepository).markPresentationsAnonymized(testUser.getId());
        verify(userRepository).save(testUser);
    }
    
    @Test
    public void testAnonymizeUserWithNoFiles() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(emptyUser);
        
        // When
        userDataRetentionService.anonymizeUserData(emptyUser);
        
        // Then
        // Verify no file deletions were attempted
        verify(uploadService, times(0)).deleteFile(anyString());
        
        // Verify that academic records are still anonymized
        verify(thesisCommentRepository).anonymizeUserComments(emptyUser.getId());
        verify(applicationRepository).markApplicationsAnonymized(emptyUser.getId());
        verify(thesisPresentationRepository).markPresentationsAnonymized(emptyUser.getId());
        verify(userRepository).save(emptyUser);
    }
    
    @Test
    public void testResilientFileHandling() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Make the first file deletion throw an exception
        doThrow(new RuntimeException("File not found")).when(uploadService).deleteFile("cv-123.pdf");
        
        // When
        userDataRetentionService.anonymizeUserData(testUser);
        
        // Then
        // Verify that the process continued and tried to delete the other files
        verify(uploadService, times(1)).deleteFile("degree-123.pdf");
        verify(uploadService, times(1)).deleteFile("exam-123.pdf");
        
        // Verify that academic records are still anonymized
        verify(thesisCommentRepository).anonymizeUserComments(testUser.getId());
        verify(applicationRepository).markApplicationsAnonymized(testUser.getId());
        verify(thesisPresentationRepository).markPresentationsAnonymized(testUser.getId());
        verify(userRepository).save(testUser);
    }
    
    @Test
    public void testProcessDataRetention() {
        // Given
        List<User> userList = new ArrayList<>();
        userList.add(testUser);
        userList.add(emptyUser);
        
        when(userRepository.findUsersWithNoRecentActivity(any(Instant.class), anyInt()))
                .thenReturn(userList);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        userDataRetentionService.processDataRetention();
        
        // Then
        verify(userRepository).findUsersWithNoRecentActivity(any(Instant.class), eq(100));
        verify(thesisCommentRepository, times(2)).anonymizeUserComments(any(UUID.class));
        verify(applicationRepository, times(2)).markApplicationsAnonymized(any(UUID.class));
        verify(thesisPresentationRepository, times(2)).markPresentationsAnonymized(any(UUID.class));
        verify(userRepository, times(2)).save(any(User.class));
    }
    
    @Test
    public void testProcessDataRetentionWithError() {
        // Given
        List<User> userList = new ArrayList<>();
        userList.add(testUser);
        userList.add(emptyUser);
        
        when(userRepository.findUsersWithNoRecentActivity(any(Instant.class), anyInt()))
                .thenReturn(userList);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Make processing the first user throw an exception
        doThrow(new RuntimeException("Database error")).when(thesisCommentRepository)
                .anonymizeUserComments(eq(testUser.getId()));
        
        // When
        userDataRetentionService.processDataRetention();
        
        // Then
        // Verify that the process continued with the second user despite the error with the first
        verify(thesisCommentRepository).anonymizeUserComments(eq(emptyUser.getId()));
        verify(applicationRepository).markApplicationsAnonymized(eq(emptyUser.getId()));
        verify(thesisPresentationRepository).markPresentationsAnonymized(eq(emptyUser.getId()));
        verify(userRepository).save(eq(emptyUser));
    }
}
