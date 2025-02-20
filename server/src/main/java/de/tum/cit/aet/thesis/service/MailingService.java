package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.utility.MailBuilder;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailingService {
    private final MailBuilder mailBuilder;
    private final GroupEmailTemplateService groupEmailTemplateService;

    public void sendApplicationAcceptedMail(Application application) throws MessagingException {
        Thesis thesis = application.getThesis();
        Map<String, Object> variables = new HashMap<>();
        variables.put("thesis", thesis);
        variables.put("application", application);

        String template = "application-accepted";
        if (thesis.getAdvisors().isEmpty()) {
            template += "-no-advisor";
        }

        String content = groupEmailTemplateService.processTemplate(
                template + ".html", 
                variables,
                thesis.getGroup().getId());

        mailBuilder.to(application.getUser())
                .subject("Application Accepted: " + thesis.getTitle())
                .html(content)
                .send();
    }

    public void sendApplicationRejectedMail(Application application, String reason) throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("thesis", application.getThesis());
        variables.put("application", application);

        String template = "application-rejected";
        if (reason != null) {
            template += "-" + reason.toLowerCase().replace("_", "-");
        }

        String content = groupEmailTemplateService.processTemplate(
                template + ".html", 
                variables,
                application.getThesis().getGroup().getId());

        mailBuilder.to(application.getUser())
                .subject("Application Rejected: " + application.getThesis().getTitle())
                .html(content)
                .send();
    }

    public void sendThesisCreatedMail(Thesis thesis) throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("thesis", thesis);

        String content = groupEmailTemplateService.processTemplate(
                "thesis-created.html", 
                variables,
                thesis.getGroup().getId());

        mailBuilder.to(thesis.getStudents())
                .cc(thesis.getAdvisors())
                .subject("Thesis Created: " + thesis.getTitle())
                .html(content)
                .send();
    }

    // Add other email sending methods with group context...
}