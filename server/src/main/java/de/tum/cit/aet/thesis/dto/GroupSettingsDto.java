package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.GroupSettings;
import lombok.Data;

@Data
public class GroupSettingsDto {
    private String acceptanceEmailTemplate;
    private String postAcceptanceInstructions;
    private String emailFooter;

    public static GroupSettingsDto from(GroupSettings settings) {
        if (settings == null) return null;
        GroupSettingsDto dto = new GroupSettingsDto();
        dto.setAcceptanceEmailTemplate(settings.getAcceptanceEmailTemplate());
        dto.setPostAcceptanceInstructions(settings.getPostAcceptanceInstructions());
        dto.setEmailFooter(settings.getEmailFooter());
        return dto;
    }
}
