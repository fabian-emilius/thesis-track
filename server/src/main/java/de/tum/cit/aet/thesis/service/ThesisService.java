package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.ThesisRoleName;
import de.tum.cit.aet.thesis.constants.ThesisState;
import de.tum.cit.aet.thesis.constants.ThesisVisibility;
import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.entity.key.ThesisRoleId;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
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
    private final GroupContextService groupContextService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<Thesis> searchTheses(
            UUID groupId,
            String type,
            ThesisState state,
            String searchQuery,
            Pageable pageable
    ) {
        // Validate group access
        groupContextService.validateGroupAccess(groupId);

        String searchQueryFilter = searchQuery == null || searchQuery.isEmpty() ? null : searchQuery.toLowerCase();
        return thesisRepository.searchTheses(groupId, type, state, searchQueryFilter, pageable);
    }

    @Transactional(readOnly = true)
    public Thesis findById(UUID thesisId) {
        Thesis thesis = thesisRepository.findById(thesisId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis not found"));

        // Validate group access and thesis visibility
        groupContextService.validateGroupAccess(thesis.getGroup().getId());
        validateThesisAccess(thesis);

        return thesis;
    }

    @Transactional
    public Thesis createThesis(
            UUID groupId,
            String title,
            String type,
            String language,
            String info,
            String abstractText,
            Set<String> keywords,
            ThesisVisibility visibility,
            UUID supervisorId,
            List<UUID> advisorIds,
            UUID studentId
    ) {
        // Validate group access and advisor rights
        Group group = groupContextService.validateGroupAccess(groupId);
        groupContextService.validateGroupAdvisorAccess(groupId);

        Thesis thesis = new Thesis();
        thesis.setTitle(title);
        thesis.setType(type);
        thesis.setLanguage(language);
        thesis.setInfo(info);
        thesis.setAbstractField(abstractText);
        thesis.setKeywords(keywords);
        thesis.setVisibility(visibility);
        thesis.setState(ThesisState.CREATED);
        thesis.setGroup(group);

        thesis = thesisRepository.save(thesis);

        // Assign roles
        User currentUser = userService.getCurrentUser();
        assignThesisRoles(thesis, currentUser, supervisorId, advisorIds, studentId);

        return thesisRepository.save(thesis);
    }

    @Transactional
    public Thesis updateThesis(
            Thesis thesis,
            String title,
            String type,
            String language,
            String info,
            String abstractText,
            Set<String> keywords,
            ThesisVisibility visibility,
            UUID supervisorId,
            List<UUID> advisorIds,
            UUID studentId
    ) {
        // Validate group access and advisor rights
        groupContextService.validateGroupAdvisorAccess(thesis.getGroup().getId());
        validateThesisAccess(thesis);

        thesis.setTitle(title);
        thesis.setType(type);
        thesis.setLanguage(language);
        thesis.setInfo(info);
        thesis.setAbstractField(abstractText);
        thesis.setKeywords(keywords);
        thesis.setVisibility(visibility);

        // Assign roles
        User currentUser = userService.getCurrentUser();
        assignThesisRoles(thesis, currentUser, supervisorId, advisorIds, studentId);

        return thesisRepository.save(thesis);
    }

    private void validateThesisAccess(Thesis thesis) {
        User currentUser = userService.getCurrentUser();
        if (!thesis.hasReadAccess(currentUser)) {
            throw new AccessDeniedException("No access to this thesis");
        }
    }

    private void assignThesisRoles(
            Thesis thesis,
            User assigner,
            UUID supervisorId,
            List<UUID> advisorIds,
            UUID studentId
    ) {
        // Clear existing roles
        thesisRoleRepository.deleteByThesisId(thesis.getId());
        thesis.setRoles(new ArrayList<>());

        // Assign supervisor
        User supervisor = userService.findById(supervisorId);
        if (!supervisor.hasAnyGroup("supervisor")) {
            throw new ResourceInvalidParametersException("User is not a supervisor");
        }
        saveThesisRole(thesis, assigner, supervisor, ThesisRoleName.SUPERVISOR, 0);

        // Assign advisors
        for (int i = 0; i < advisorIds.size(); i++) {
            User advisor = userService.findById(advisorIds.get(i));
            if (!advisor.hasAnyGroup("advisor", "supervisor")) {
                throw new ResourceInvalidParametersException("User is not an advisor");
            }
            saveThesisRole(thesis, assigner, advisor, ThesisRoleName.ADVISOR, i);
        }

        // Assign student
        User student = userService.findById(studentId);
        saveThesisRole(thesis, assigner, student, ThesisRoleName.STUDENT, 0);
    }

    private void saveThesisRole(Thesis thesis, User assigner, User user, ThesisRoleName role, int position) {
        ThesisRole thesisRole = new ThesisRole();
        ThesisRoleId thesisRoleId = new ThesisRoleId();

        thesisRoleId.setThesisId(thesis.getId());
        thesisRoleId.setUserId(user.getId());
        thesisRoleId.setRole(role);

        thesisRole.setId(thesisRoleId);
        thesisRole.setUser(user);
        thesisRole.setAssignedBy(assigner);
        thesisRole.setAssignedAt(Instant.now());
        thesisRole.setThesis(thesis);
        thesisRole.setPosition(position);

        thesisRoleRepository.save(thesisRole);

        List<ThesisRole> roles = thesis.getRoles();
        roles.add(thesisRole);
        roles.sort(Comparator.comparingInt(ThesisRole::getPosition));
        thesis.setRoles(roles);
    }
}