package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.config.DataRetentionConfig;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserDataRetentionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UploadService uploadService;

    @Mock
    private DataRetentionConfig config;

    @InjectMocks
    private UserDataRetentionService userDataRetentionService;

    private User testUser;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCvFilename("cv.pdf");
        testUser.setDegreeFilename("degree.pdf");
        testUser.setExaminationFilename("exam.pdf");
        testUser.setProjects("Test Projects");
        testUser.setInterests("Test Interests");
        testUser.setSpecialSkills("Test Skills");

        when(config.getUserDataYears()).thenReturn(10);
        when(config.getBatchSize()).thenReturn(100);
    }

    @Test
    void findUsersForDeletion_ShouldCallRepository() {
        // Given
        Instant expectedCutoff = Instant.now().minus(10, ChronoUnit.YEARS);
        when(userRepository.findUsersForDeletion(any())).thenReturn(Arrays.asList(testUser));

        // When
        List<User> result = userDataRetentionService.findUsersForDeletion();

        // Then
        assertFalse(result.isEmpty(), "Should return non-empty list");
        assertEquals(1, result.size(), "Should return one user");
        verify(userRepository).findUsersForDeletion(any());
    }

    @Test
    void findUsersForDeletion_WhenNoUsers_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findUsersForDeletion(any())).thenReturn(Collections.emptyList());

        // When
        List<User> result = userDataRetentionService.findUsersForDeletion();

        // Then
        assertTrue(result.isEmpty(), "Should return empty list");
        verify(userRepository).findUsersForDeletion(any());
    }

    @Test
    void anonymizeUserData_ShouldAnonymizeCorrectly() {
        // Given
        when(userRepository.save(any())).thenReturn(testUser);

        // When
        userDataRetentionService.anonymizeUserData(testUser);

        // Then
        assertEquals("anonymized_" + userId + "@deleted.user", testUser.getEmail());
        assertEquals("Anonymized", testUser.getFirstName());
        assertEquals("User", testUser.getLastName());
        assertNull(testUser.getGender(), "Gender should be null");
        assertNull(testUser.getNationality(), "Nationality should be null");
        assertNull(testUser.getMatriculationNumber(), "Matriculation number should be null");
        assertNull(testUser.getCvFilename(), "CV filename should be null");
        assertNull(testUser.getDegreeFilename(), "Degree filename should be null");
        assertNull(testUser.getExaminationFilename(), "Examination filename should be null");
        assertEquals("[Anonymized]", testUser.getProjects(), "Projects should be anonymized");
        assertEquals("[Anonymized]", testUser.getInterests(), "Interests should be anonymized");
        assertEquals("[Anonymized]", testUser.getSpecialSkills(), "Skills should be anonymized");
        
        verify(uploadService).deleteFile("cv.pdf");
        verify(uploadService).deleteFile("degree.pdf");
        verify(uploadService).deleteFile("exam.pdf");
        verify(userRepository).save(testUser);
    }

    @Test
    void anonymizeUserData_WhenNoFiles_ShouldNotCallDeleteFile() {
        // Given
        testUser.setCvFilename(null);
        testUser.setDegreeFilename(null);
        testUser.setExaminationFilename(null);
        when(userRepository.save(any())).thenReturn(testUser);

        // When
        userDataRetentionService.anonymizeUserData(testUser);

        // Then
        verify(uploadService, never()).deleteFile(any());
        verify(userRepository).save(testUser);
    }

    @Test
    void processDataRetention_ShouldHandleBatches() {
        // Given
        List<User> users = Arrays.asList(testUser, new User(), new User());
        when(userRepository.findUsersForDeletion(any())).thenReturn(users);
        when(config.getBatchSize()).thenReturn(2);

        // When
        userDataRetentionService.processDataRetention();

        // Then
        verify(userRepository, times(1)).findUsersForDeletion(any());
        verify(userRepository, times(3)).save(any());
    }

    @Test
    void processDataRetention_WhenFileDeleteFails_ShouldContinueProcessing() {
        // Given
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findUsersForDeletion(any())).thenReturn(users);
        doThrow(new RuntimeException("File delete failed")).when(uploadService).deleteFile("cv.pdf");

        // When
        userDataRetentionService.processDataRetention();

        // Then
        verify(userRepository).save(testUser); // Should still save the user
        verify(userRepository, times(2)).save(any()); // Should process both users
    }
}
