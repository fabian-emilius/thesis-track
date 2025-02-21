package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import de.tum.cit.aet.thesis.exception.DataRetentionException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataRetentionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UploadService uploadService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private DataRetentionAuditService auditService;

    @InjectMocks
    private DataRetentionService dataRetentionService;

    private User inactiveUser;
    private User activeUser;

    @BeforeEach
    void setUp() {
        inactiveUser = new User();
        inactiveUser.setLastActivityAt(Instant.now().minusSeconds(11L * 365 * 24 * 60 * 60)); // 11 years ago
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User");
        inactiveUser.setCvFilename("cv.pdf");

        activeUser = new User();
        activeUser.setLastActivityAt(Instant.now());
        activeUser.setFirstName("Active");
        activeUser.setLastName("User");
    }

    @Test
    void findEligibleUsers_ShouldReturnInactiveUsers() {
        when(userRepository.findByScheduledDeletionAtIsNullAndLastActivityAtBefore(any()))
            .thenReturn(Arrays.asList(inactiveUser));

        List<User> eligibleUsers = dataRetentionService.findEligibleUsers();

        verify(userRepository).findByScheduledDeletionAtIsNullAndLastActivityAtBefore(any());
        assertEquals(1, eligibleUsers.size(), "Should return exactly one eligible user");
        assertEquals("Inactive", eligibleUsers.get(0).getFirstName(), "Should return the inactive user");
    }

    @Test
    void processUserDeletion_ShouldDeleteUserData() {
        when(userRepository.save(any())).thenReturn(inactiveUser);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mock(jakarta.persistence.Query.class));

        dataRetentionService.processUserDeletion(Arrays.asList(inactiveUser));

        verify(uploadService).delete("cv.pdf");
        verify(userRepository).save(argThat(user -> 
            user.getFirstName().equals("[DELETED]") &&
            user.getLastName().equals("[DELETED]") &&
            user.getCvFilename() == null
        ));
        verify(auditService).logUserDeletion(eq(inactiveUser.getId()));
        verifyNoMoreInteractions(auditService);
    }

    @Test
    void deleteUserData_ShouldAnonymizeUserData() {
        when(userRepository.save(any())).thenReturn(inactiveUser);

        dataRetentionService.deleteUserData(inactiveUser);

        verify(uploadService).delete("cv.pdf");
        verify(userRepository).save(argThat(user -> 
            user.getFirstName().equals("[DELETED]") &&
            user.getLastName().equals("[DELETED]") &&
            user.getEmail() == null &&
            user.getCvFilename() == null &&
            user.getScheduledDeletionAt() != null
        ));
        verify(auditService).logUserDeletion(eq(inactiveUser.getId()));
    }

    @Test
    void deleteUserData_ShouldHandleErrors() {
        when(userRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataRetentionException.class, () -> 
            dataRetentionService.deleteUserData(inactiveUser)
        );
        verify(auditService).logDeletionError(eq(inactiveUser.getId()), any());
    }

    @Test
    void deleteUserData_ShouldValidateInput() {
        IllegalArgumentException nullException = assertThrows(IllegalArgumentException.class, () -> 
            dataRetentionService.deleteUserData(null)
        );
        assertEquals("User cannot be null", nullException.getMessage());

        User invalidUser = new User();
        invalidUser.setLastActivityAt(Instant.now());
        IllegalArgumentException activeException = assertThrows(IllegalArgumentException.class, () -> 
            dataRetentionService.deleteUserData(invalidUser)
        );
        assertEquals("Cannot delete data of active user", activeException.getMessage());
        
        verifyNoInteractions(auditService, uploadService, userRepository);
    }

    @Test
    void processUserDeletion_ShouldHandleEmptyList() {
        dataRetentionService.processUserDeletion(List.of());
        verifyNoInteractions(uploadService, userRepository);
    }

    @Test
    void processUserDeletion_ShouldContinueOnError() {
        User errorUser = new User();
        errorUser.setId(2L);
        errorUser.setCvFilename("error.pdf");
        when(userRepository.save(eq(errorUser))).thenThrow(new RuntimeException("Save failed"));

        List<User> users = Arrays.asList(inactiveUser, errorUser, activeUser);
        dataRetentionService.processUserDeletion(users);

        // Verify successful deletion
        verify(uploadService).delete("cv.pdf");
        verify(uploadService).delete("error.pdf");
        verify(auditService).logUserDeletion(eq(inactiveUser.getId()));

        // Verify error handling
        verify(auditService).logDeletionError(eq(errorUser.getId()), any(RuntimeException.class));
        
        // Verify active user was skipped
        verifyNoMoreInteractions(auditService);
    }
}