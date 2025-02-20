package de.tum.cit.aet.thesis.dto;

import lombok.Data;

@Data
public class GroupSettingsDto {
    private String acceptanceEmailTemplate;
    private String postAcceptanceInstructions;
    private String emailFooter;
}