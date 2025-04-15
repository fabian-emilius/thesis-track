package de.tum.cit.aet.thesis.cron;

import de.tum.cit.aet.thesis.service.UserDataRetentionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job that runs the data retention process to automatically delete
 * user data after the retention period has expired.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class DataRetentionJob {

    private final UserDataRetentionService userDataRetentionService;
    
    /**
     * Scheduled job that runs daily at 2 AM to clean up expired user data.
     * This time is chosen to minimize system impact during off-peak hours.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM every day
    public void runDataRetention() {
        log.info("Starting scheduled data retention job");
        
        try {
            int processedCount = userDataRetentionService.processDataRetention();
            log.info("Data retention job completed: {} users processed", processedCount);
        } catch (Exception e) {
            log.error("Error in data retention job", e);
        }
    }
}