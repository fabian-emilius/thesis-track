package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.constants.ApplicationState;
import de.tum.cit.aet.thesis.controller.payload.*;
import de.tum.cit.aet.thesis.dto.ApplicationDto;
import de.tum.cit.aet.thesis.dto.PaginationDto;
import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.service.ApplicationService;
import de.tum.cit.aet.thesis.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<PaginationDto<ApplicationDto>> getApplications(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID reviewerId,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) ApplicationState[] states,
            @RequestParam(required = false) String[] previous,
            @RequestParam(required = false) String[] topics,
            @RequestParam(required = false) String[] types,
            @RequestParam(required = false, defaultValue = "false") boolean includeSuggestedTopics,
            @RequestParam(required = false) Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Page<Application> applications = applicationService.getAll(
                userId,
                reviewerId,
                searchQuery,
                states,
                previous,
                topics,
                types,
                includeSuggestedTopics,
                groupId,
                page,
                limit,
                sortBy,
                sortOrder
        );

        return ResponseEntity.ok(PaginationDto.from(
                applications.map(application -> ApplicationDto.fromApplicationEntity(application, true))
        ));
    }

    @PostMapping
    public ResponseEntity<ApplicationDto> createApplication(@Valid @RequestBody CreateApplicationPayload payload) {
        User user = authenticationService.getCurrentUser();

        Application application = applicationService.createApplication(
                user,
                payload.getTopicId(),
                payload.getThesisTitle(),
                payload.getThesisType(),
                payload.getDesiredStartDate(),
                payload.getMotivation(),
                payload.getGroupId()
        );

        return ResponseEntity.ok(ApplicationDto.fromApplicationEntity(application, true));
    }

    @PutMapping("/{applicationId}")
    public ResponseEntity<ApplicationDto> updateApplication(
            @PathVariable UUID applicationId,
            @Valid @RequestBody CreateApplicationPayload payload
    ) {
        User user = authenticationService.getCurrentUser();
        Application application = applicationService.findById(applicationId);

        if (!application.getUser().getId().equals(user.getId())) {
            throw new ResourceInvalidParametersException("You can only update your own applications.");
        }

        if (application.getState() != ApplicationState.NOT_ASSESSED) {
            throw new ResourceInvalidParametersException("You can only update applications that have not been assessed yet.");
        }

        application = applicationService.updateApplication(
                application,
                payload.getTopicId(),
                payload.getThesisTitle(),
                payload.getThesisType(),
                payload.getDesiredStartDate(),
                payload.getMotivation()
        );

        return ResponseEntity.ok(ApplicationDto.fromApplicationEntity(application, true));
    }

    // ... rest of the endpoints remain the same ...
}