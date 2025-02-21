package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.utility.MailBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailingService {

    private final MailBuilder mailBuilder;

    /**
     * Sends a notification to a user about upcoming GDPR data deletion
     */
    public void sendGDPRDeletionNotification(User user, LocalDateTime scheduledDeletionDate) {
        mailBuilder.create()
                .to(user)
                .template("gdpr-deletion-notification")
                .variables(Map.of(
                        "user", user,
                        "scheduledDeletionDate", scheduledDeletionDate
                ))
                .send();
    }
}
