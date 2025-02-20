package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
public class EmailTemplateValidator {
    private final SpringTemplateEngine templateEngine;

    public EmailTemplateValidator(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void validateTemplate(String template) {
        if (template == null || template.isBlank()) {
            return;
        }

        try {
            // Try to process the template with empty context to validate syntax
            Context context = new Context();
            templateEngine.process(template, context);
        } catch (Exception e) {
            throw new ResourceInvalidParametersException("Invalid email template format: " + e.getMessage());
        }
    }

    public void validateTemplates(String acceptanceTemplate, String postAcceptanceInstructions, String emailFooter) {
        validateTemplate(acceptanceTemplate);
        validateTemplate(postAcceptanceInstructions);
        validateTemplate(emailFooter);
    }
}
