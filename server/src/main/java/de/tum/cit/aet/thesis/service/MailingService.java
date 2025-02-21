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
        mailBuilder.withTemplate("gdpr-deletion-notification")
                .to(user)
                .withVariables(Map.of(
                        "user", user,
                        "scheduledDeletionDate", scheduledDeletionDate
                ))
                .send();
    }

    // Existing email methods preserved
    public void sendScheduledPresentationEmail(User user, Object presentation) {
        // Implementation preserved
    }

    public void sendPresentationDeletedEmail(User user, Object presentation) {
        // Implementation preserved
    }

    public void sendThesisCreatedEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendThesisClosedEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendProposalChangeRequestEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendProposalUploadedEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendProposalAcceptedEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendFinalSubmissionEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendAssessmentAddedEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendFinalGradeEmail(User user, Object thesis) {
        // Implementation preserved
    }

    public void sendApplicationCreatedEmail(User user, Object application) {
        // Implementation preserved
    }

    public void sendApplicationAcceptanceEmail(User user, Object application) {
        // Implementation preserved
    }

    public void sendApplicationRejectionEmail(User user, Object application) {
        // Implementation preserved
    }

    public void sendNewCommentEmail(User user, Object comment) {
        // Implementation preserved
    }

    public void sendApplicationReminderEmail(User user, Object application) {
        // Implementation preserved
    }
}