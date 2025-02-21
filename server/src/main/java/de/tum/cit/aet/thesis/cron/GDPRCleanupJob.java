package de.tum.cit.aet.thesis.cron;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.service.GDPRCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GDPRCleanupJob {

    private final GDPRCleanupService gdprCleanupService;

    /**
     * Daily job to identify and schedule users for deletion
     */
    @Scheduled(cron = "0 0 1 * * ?") // Run at 1 AM every day
    public void scheduleUserDeletions() {
        try {
            List<User> eligibleUsers = gdprCleanupService.findUsersEligibleForDeletion();
            if (!eligibleUsers.isEmpty()) {
                log.info("Scheduling {} users for GDPR deletion", eligibleUsers.size());
                gdprCleanupService.scheduleUsersForDeletion(eligibleUsers);
            }
        } catch (Exception e) {
            log.error("Error in GDPR user scheduling job", e);
        }
    }

    /**
     * Daily job to execute scheduled deletions
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM every day
    public void executeScheduledDeletions() {
        try {
            log.info("Starting scheduled GDPR deletions");
            gdprCleanupService.executeScheduledDeletions();
        } catch (Exception e) {
            log.error("Error in GDPR deletion execution job", e);
        }
    }
}
