package de.tum.cit.aet.thesis.cron;

import de.tum.cit.aet.thesis.service.UserDataRetentionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataRetentionJobTest {

    @Mock
    private UserDataRetentionService userDataRetentionService;

    @InjectMocks
    private UserDataRetentionJob userDataRetentionJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunDataRetentionJob_WhenEnabled() {
        // Given
        ReflectionTestUtils.setField(userDataRetentionJob, "dataRetentionEnabled", true);
        when(userDataRetentionService.processUserDataDeletion()).thenReturn(5);

        // When
        userDataRetentionJob.runDataRetentionJob();

        // Then
        verify(userDataRetentionService, times(1)).processUserDataDeletion();
    }

    @Test
    void testRunDataRetentionJob_WhenDisabled() {
        // Given
        ReflectionTestUtils.setField(userDataRetentionJob, "dataRetentionEnabled", false);

        // When
        userDataRetentionJob.runDataRetentionJob();

        // Then
        verify(userDataRetentionService, never()).processUserDataDeletion();
    }
}
