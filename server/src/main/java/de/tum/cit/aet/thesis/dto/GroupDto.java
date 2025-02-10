package de.tum.cit.aet.thesis.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class GroupDto {
    private UUID id;

    @NotNull
    private String name;

    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
