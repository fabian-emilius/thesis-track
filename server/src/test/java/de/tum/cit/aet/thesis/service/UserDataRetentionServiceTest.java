package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.config.DataRetentionConfig;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ThesisRepository;
import de.tum.cit.aet.thesis.repository.ThesisRoleRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserDataRetentionServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private ThesisRepository thesisRepository;
    
    @Mock
    private ThesisRoleRepository thesisRoleRepository;
    
    @Mock
    private UploadService uploadService;
    
    @Mock
    private EntityManager entityManager;
    
    @Mock
    private Query query;
    
    @Mock
    private Query updateQuery;
    
    private DataRetentionConfig config;
    private UserDataRetentionService service;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        config = new DataRetentionConfig();
        config.setUserDataYears(10);
        config.setBatchSize(100);
        config.setSchedule("0 0 2 * * *");
        
        service = new UserDataRetentionService(
                userRepository, applicationRepository, thesisRepository, thesisRoleRepository, config, uploadService);
                
        // Use reflection to inject entity manager
        try {
            java.lang.reflect.Field field = service.getClass().getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(service, entityManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject entity manager", e);
        }
        
        // Setup for entity manager queries
        when(entityManager.createNativeQuery(anyString())).thenReturn(updateQuery);
        when(entityManager.createNativeQuery(anyString(), any(Class.class))).thenReturn(query);
        when(updateQuery.setParameter(anyString(), any())).thenReturn(updateQuery);
        when(updateQuery.executeUpdate()).thenReturn(5); // Assume 5 records updated
        when(query.setParameter(anyString(), any())).thenReturn(query);
    }
    
    @Test
    void identifyUsersForDeletion_shouldReturnUsersOlderThanRetentionPeriod() {
        // Arrange
        when(entityManager.createNativeQuery(anyString(), eq(User.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUniversityId("user1");
        user1.setEmail("user1@example.com");
        
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUniversityId("user2");
        user2.setEmail("user2@example.com");
        
        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(query.getResultList()).thenReturn(expectedUsers);
        
        // Act
        List<User> result = service.identifyUsersForDeletion();
        
        // Assert
        assertEquals(expectedUsers.size(), result.size());
        assertEquals(expectedUsers, result);
        verify(entityManager).createNativeQuery(anyString(), eq(User.class));
        verify(query).setParameter(eq("cutoffDate"), any(Instant.class));
    }
    
    @Test
    void anonymizeUserData_shouldAnonymizeUserData() {
        // Arrange
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUniversityId("testUser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setGender("M");
        user.setNationality("German");
        user.setMatriculationNumber("12345678");
        user.setCvFilename("cv.pdf");
        user.setDegreeFilename("degree.pdf");
        user.setExaminationFilename("exam.pdf");
        
        // Mock deleteFile to return true (file was deleted)
        when(uploadService.deleteFile(anyString())).thenReturn(true);
        
        // Act
        service.anonymizeUserData(user);
        
        // Assert
        verify(uploadService).deleteFile("cv.pdf");
        verify(uploadService).deleteFile("degree.pdf");
        verify(uploadService).deleteFile("exam.pdf");
        verify(userRepository).save(user);
        
        // Verify application anonymization
        verify(entityManager, times(3)).createNativeQuery(anyString());
        verify(updateQuery, times(3)).setParameter(eq("userId"), eq(userId));
        verify(updateQuery, times(3)).executeUpdate();
        
        // Verify user data is anonymized
        assertEquals("Anonymized", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals(null, user.getEmail());
        assertEquals(null, user.getGender());
        assertEquals(null, user.getNationality());
        assertEquals(null, user.getMatriculationNumber());
        assertEquals(null, user.getCvFilename());
        assertEquals(null, user.getDegreeFilename());
        assertEquals(null, user.getExaminationFilename());
        assertEquals(true, user.getUniversityId().startsWith("anonymized-"));
    }
    
    @Test
    void runDataRetentionJob_shouldProcessUsersInBatches() {
        // Arrange
        config.setBatchSize(2);
        
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUniversityId("user1");
        
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUniversityId("user2");
        
        User user3 = new User();
        user3.setId(UUID.randomUUID());
        user3.setUniversityId("user3");
        
        User user4 = new User();
        user4.setId(UUID.randomUUID());
        user4.setUniversityId("user4");
        
        List<User> users = Arrays.asList(user1, user2, user3, user4);
        
        // Create spy to test the batch processing
        UserDataRetentionService spy = Mockito.spy(service);
        doReturn(users).when(spy).identifyUsersForDeletion();
        doNothing().when(spy).anonymizeUserData(any(User.class));
        
        // Act
        spy.runDataRetentionJob();
        
        // Assert - verify each user is processed
        verify(spy).anonymizeUserData(user1);
        verify(spy).anonymizeUserData(user2);
        verify(spy).anonymizeUserData(user3);
        verify(spy).anonymizeUserData(user4);
    }
    
    @Test
    void anonymizeUserData_shouldHandleNullFiles() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUniversityId("testUser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setGender("M");
        user.setNationality("German");
        user.setMatriculationNumber("12345678");
        user.setCvFilename(null); // No CV file
        user.setDegreeFilename(""); // Empty degree file
        user.setExaminationFilename(null); // No exam file
        
        // Act
        service.anonymizeUserData(user);
        
        // Assert - no file deletions should happen
        verify(uploadService, never()).deleteFile(anyString());
        verify(userRepository).save(user);
    }
}