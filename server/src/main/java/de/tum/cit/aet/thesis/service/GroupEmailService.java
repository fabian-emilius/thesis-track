package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupSettings;
import de.tum.cit.aet.thesis.repository.GroupSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupEmailService {
    private final GroupSettingsRepository groupSettingsRepository;

    public Map<String, Object> getGroupEmailContext(UUID groupId) {
        GroupSettings settings = groupSettingsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group settings not found"));
        
        Map<String, Object> context = new HashMap<>();
        context.put("groupAcceptanceTemplate", settings.getAcceptanceEmailTemplate());
        context.put("groupPostAcceptanceInstructions", settings.getPostAcceptanceInstructions());
        context.put("groupEmailFooter", settings.getEmailFooter());
        context.put("group", settings.getGroup());
        
        return context;
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