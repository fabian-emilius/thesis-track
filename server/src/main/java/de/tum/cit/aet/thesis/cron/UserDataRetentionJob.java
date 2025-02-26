package de.tum.cit.aet.thesis.cron;

import de.tum.cit.aet.thesis.service.UserDataRetentionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job that runs the GDPR data deletion process periodically.
 * Identifies and anonymizes user data that has exceeded the retention period.
 */
@Component
public class UserDataRetentionJob {

    private static final Logger logger = LoggerFactory.getLogger(UserDataRetentionJob.class);
    
    private final UserDataRetentionService userDataRetentionService;
    
    @Value("${thesis-management.data-retention.enabled:true}")
    private boolean dataRetentionEnabled;

    public UserDataRetentionJob(UserDataRetentionService userDataRetentionService) {
        this.userDataRetentionService = userDataRetentionService;
    }

    /**
     * Runs the data retention job at the configured schedule.
     * Default is 2:00 AM daily.
     */
    @Scheduled(cron = "${thesis-management.data-retention.schedule:0 0 2 * * *}")
    public void runDataRetentionJob() {
        if (!dataRetentionEnabled) {
            logger.info("Data retention job is disabled. Skipping execution.");
            return;
        }
        
        logger.info("Starting user data retention job for GDPR compliance");
        
        try {
            int processedUsers = userDataRetentionService.processUserDataDeletion();
            logger.info("User data retention job completed. Processed {} users.", processedUsers);
        } catch (Exception e) {
            logger.error("Error executing user data retention job", e);
        }
    }
}
