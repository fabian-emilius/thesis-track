package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.ThesisRoleName;
import de.tum.cit.aet.thesis.constants.ThesisState;
import de.tum.cit.aet.thesis.constants.ThesisVisibility;
import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.entity.jsonb.ThesisMetadata;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.ThesisRepository;
import de.tum.cit.aet.thesis.repository.ThesisRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ThesisService {
    private final ThesisRepository thesisRepository;
    private final ThesisRoleRepository thesisRoleRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<Thesis> searchTheses(
            Long groupId,
            String searchQuery,
            ThesisState state,
            String type,
            ThesisVisibility visibility,
            Pageable pageable
    ) {
        return thesisRepository.searchTheses(
                groupId,
                searchQuery != null ? searchQuery.toLowerCase() : null,
                state,
                type,
                visibility,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Thesis findById(UUID id, Long groupId) {
        return thesisRepository.findByIdAndGroupId(id, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis not found"));
    }

    @Transactional
    public Thesis createThesis(
            User createdBy,
            String title,
            String type,
            String language,
            List<UUID> supervisorIds,
            List<UUID> advisorIds,
            List<UUID> studentIds,
            Application application,
            Group group,
            boolean isDraft
    ) {
        validateRoles(supervisorIds, advisorIds, studentIds);

        Thesis thesis = new Thesis();
        thesis.setTitle(title);
        thesis.setType(type);
        thesis.setLanguage(language);
        thesis.setMetadata(new ThesisMetadata());
        thesis.setInfo("");
        thesis.setAbstractField("");
        thesis.setState(isDraft ? ThesisState.DRAFT : ThesisState.CREATED);
        thesis.setVisibility(ThesisVisibility.INTERNAL);
        thesis.setApplication(application);
        thesis.setGroup(group);
        thesis.setCreatedAt(Instant.now());

        thesis = thesisRepository.save(thesis);

        addRoles(thesis, supervisorIds, ThesisRoleName.SUPERVISOR);
        addRoles(thesis, advisorIds, ThesisRoleName.ADVISOR);
        addRoles(thesis, studentIds, ThesisRoleName.STUDENT);

        return thesis;
    }

    private void validateRoles(List<UUID> supervisorIds, List<UUID> advisorIds, List<UUID> studentIds) {
        if (supervisorIds == null || supervisorIds.isEmpty()) {
            throw new ResourceInvalidParametersException("At least one supervisor is required");
        }

        if (studentIds == null || studentIds.isEmpty()) {
            throw new ResourceInvalidParametersException("At least one student is required");
        }

        // Validate that users exist and have appropriate roles
        supervisorIds.forEach(id -> {
            User user = userService.findById(id);
            if (!user.hasAnyGroup("supervisor")) {
                throw new ResourceInvalidParametersException("User " + user.getEmail() + " is not a supervisor");
            }
        });

        if (advisorIds != null) {
            advisorIds.forEach(id -> {
                User user = userService.findById(id);
                if (!user.hasAnyGroup("advisor")) {
                    throw new ResourceInvalidParametersException("User " + user.getEmail() + " is not an advisor");
                }
            });
        }

        studentIds.forEach(id -> {
            User user = userService.findById(id);
            if (!user.hasAnyGroup("student")) {
                throw new ResourceInvalidParametersException("User " + user.getEmail() + " is not a student");
            }
        });
    }

    private void addRoles(Thesis thesis, List<UUID> userIds, ThesisRoleName roleName) {
        if (userIds == null) return;

        int position = 0;
        for (UUID userId : userIds) {
            User user = userService.findById(userId);
            ThesisRole role = new ThesisRole();
            role.setThesis(thesis);
            role.setUser(user);
            role.setPosition(position++);
            role.getId().setRole(roleName);
            thesisRoleRepository.save(role);
        }
    }

    // ... rest of the methods remain the same ...
}