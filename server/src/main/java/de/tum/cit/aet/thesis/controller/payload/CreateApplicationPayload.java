package de.tum.cit.aet.thesis.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CreateApplicationPayload {
    private UUID topicId;

    @Size(max = 255)
    private String thesisTitle;

    @NotNull
    private String thesisType;

    @NotNull
    @Size(min = 100, max = 2000)
    private String motivation;

    @NotNull
    private Instant desiredStartDate;

    @NotNull
    private Long groupId;
}