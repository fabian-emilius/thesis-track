package de.tum.cit.aet.thesis.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GroupDto {
    private UUID id;
    private String slug;
    private String name;
    private String description;
    private String logoUrl;
    private String externalLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}