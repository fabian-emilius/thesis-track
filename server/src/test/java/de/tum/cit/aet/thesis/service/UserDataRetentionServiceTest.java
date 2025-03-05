package de.tum.cit.aet.thesis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDataRetentionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UserDataRetentionService userDataRetentionService;

    private User oldUser;
    private User recentUser;
    private Instant cutoffDate;

    @BeforeEach
    public void setup() {
        // Set test configuration values
        ReflectionTestUtils.setField(userDataRetentionService, "userDataRetentionYears", 10);
        ReflectionTestUtils.setField(userDataRetentionService, "batchSize", 50);
        ReflectionTestUtils.setField(userDataRetentionService, "uploadLocation", "uploads");

        cutoffDate = Instant.now().minus(10, ChronoUnit.YEARS);

        // Create test users
        oldUser = new User();
        oldUser.setId(UUID.randomUUID());
        oldUser.setFirstName("Old");
        oldUser.setLastName("User");
        oldUser.setEmail("old.user@example.com");
        oldUser.setUniversityId("old123");
        oldUser.setMatriculationNumber("12345678");
        oldUser.setCvFilename("old-cv.pdf");
        oldUser.setDegreeFilename("old-degree.pdf");
        Instant oldDate = Instant.now().minus(11, ChronoUnit.YEARS);
        oldUser.setJoinedAt(oldDate);
        oldUser.setUpdatedAt(oldDate);

        recentUser = new User();
        recentUser.setId(UUID.randomUUID());
        recentUser.setFirstName("Recent");
        recentUser.setLastName("User");
        recentUser.setEmail("recent.user@example.com");
        recentUser.setUniversityId("recent123");
        recentUser.setMatriculationNumber("87654321");
        recentUser.setJoinedAt(Instant.now().minus(5, ChronoUnit.YEARS));
        recentUser.setUpdatedAt(Instant.now().minus(1, ChronoUnit.MONTHS));
    }

    @Test
    public void testAnonymizeUserData() throws Exception {
        // Mock resource for file deletion
        Resource mockResource = mock(Resource.class);
        when(mockResource.getURI()).thenReturn(new URI("file:/temp/old-cv.pdf"));
        when(uploadService.load(anyString())).thenReturn(mockResource);

        // Capture the saved user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Call the method under test
        userDataRetentionService.anonymizeUserData(oldUser);

        // Verify user was anonymized and saved
        verify(userRepository).save(userCaptor.capture());
        User anonymizedUser = userCaptor.getValue();

        // Verify personal data was anonymized
        assertEquals("Anonymized", anonymizedUser.getFirstName());
        assertEquals("User", anonymizedUser.getLastName());
        assertTrue(anonymizedUser.getEmail().toString().startsWith("anonymized-"));
        assertTrue(anonymizedUser.getEmail().toString().endsWith("@deleted.user"));
        assertNull(anonymizedUser.getGender());
        assertNull(anonymizedUser.getNationality());
        assertNull(anonymizedUser.getMatriculationNumber());
        assertNull(anonymizedUser.getCvFilename());
        assertNull(anonymizedUser.getDegreeFilename());
        assertNull(anonymizedUser.getExaminationFilename());

        // Verify custom data contains anonymization metadata
        assertTrue(anonymizedUser.getCustomData().containsKey("anonymized"));
        assertTrue(anonymizedUser.getCustomData().containsKey("anonymizedAt"));
        assertEquals("true", anonymizedUser.getCustomData().get("anonymized"));
    }

    @Test
    public void testProcessUsersForDeletion() throws Exception {
        // Mock repository to return our test users
        when(userRepository.findUsersInactiveOlderThan(any(Instant.class)))
            .thenReturn(List.of(oldUser));

        // Mock resource for file deletion
        Resource mockResource = mock(Resource.class);
        when(mockResource.getURI()).thenReturn(new URI("file:/temp/test.pdf"));
        when(uploadService.load(anyString())).thenReturn(mockResource);

        // Call the method under test
        int count = userDataRetentionService.processUsersForDeletion();

        // Verify results
        assertEquals(1, count);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
