package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupSettings;
import de.tum.cit.aet.thesis.repository.GroupSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupEmailTemplateService {
    private final GroupSettingsRepository groupSettingsRepository;
    private final SpringTemplateEngine templateEngine;

    public String processTemplate(String templateName, Map<String, Object> variables, UUID groupId) {
        Context context = new Context();
        context.setVariables(variables);

        // Add group-specific variables
        GroupSettings settings = groupSettingsRepository.findById(groupId).orElse(null);
        if (settings != null) {
            context.setVariable("groupAcceptanceTemplate", settings.getAcceptanceEmailTemplate());
            context.setVariable("groupPostAcceptanceInstructions", settings.getPostAcceptanceInstructions());
            context.setVariable("groupEmailFooter", settings.getEmailFooter());
            context.setVariable("group", settings.getGroup());
        }

        return templateEngine.process(templateName, context);
    }

    public String getGroupEmailFooter(UUID groupId) {
        return groupSettingsRepository.findById(groupId)
                .map(GroupSettings::getEmailFooter)
                .orElse("");
    }

    public String getAcceptanceEmailTemplate(UUID groupId) {
        return groupSettingsRepository.findById(groupId)
                .map(GroupSettings::getAcceptanceEmailTemplate)
                .orElse(null);
    }

    public String getPostAcceptanceInstructions(UUID groupId) {
        return groupSettingsRepository.findById(groupId)
                .map(GroupSettings::getPostAcceptanceInstructions)
                .orElse(null);
    }
}