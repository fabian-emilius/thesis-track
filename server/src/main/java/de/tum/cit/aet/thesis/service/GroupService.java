package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.enums.GroupRole;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.ResearchGroupRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing research groups within the thesis management system.
 * Handles CRUD operations and role-based access control for research groups.
 */
@Service
@Validated
@RequiredArgsConstructor
public class GroupService implements IGroupService {
    private final ResearchGroupRepository groupRepository;
    private final GroupMemberService groupMemberService;
    private final AuthenticationService authService;

    /**
     * Retrieves all research groups in the system.
     *
     * @return List of all research groups
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResearchGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Retrieves all research groups associated with a specific user.
     *
     * @param userId The UUID of the user
     * @return List of research groups the user is a member of
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResearchGroup> getGroupsByUser(@NotNull UUID userId) {
        return groupRepository.findAllByUserId(userId);
    }

    /**
     * Retrieves a research group by its unique slug.
     *
     * @param slug The unique slug identifier of the group
     * @return The research group
     * @throws ResourceNotFoundException if the group is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ResearchGroup getGroupBySlug(@NotBlank String slug) {
        return groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    /**
     * Creates a new research group and assigns the creator as group admin.
     *
     * @param group The research group to create
     * @param creator The user creating the group
     * @return The created research group
     * @throws ResourceAlreadyExistsException if a group with the same slug already exists
     */
    @Override
    @Transactional
    public ResearchGroup createGroup(@Valid @NotNull ResearchGroup group, @NotNull User creator) {
        if (group.getSlug() == null || group.getSlug().isBlank()) {
            throw new IllegalArgumentException("Group slug cannot be empty");
        }
        if (groupRepository.existsBySlug(group.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with slug '" + group.getSlug() + "' already exists");
        }

        ResearchGroup savedGroup = groupRepository.save(group);
        groupMemberService.addGroupAdmin(savedGroup, creator);

        return savedGroup;
    }

    /**
     * Updates an existing research group's details.
     *
     * @param slug The group's slug
     * @param updatedGroup The updated group details
     * @return The updated research group
     * @throws ResourceNotFoundException if the group is not found
     */
    @Override
    @Transactional
    public ResearchGroup updateGroup(@NotBlank String slug, @Valid @NotNull ResearchGroup updatedGroup) {
        ResearchGroup existingGroup = getGroupBySlug(slug);

        existingGroup.setName(updatedGroup.getName());
        existingGroup.setDescription(updatedGroup.getDescription());
        existingGroup.setWebsiteLink(updatedGroup.getWebsiteLink());
        existingGroup.setMailFooter(updatedGroup.getMailFooter());
        existingGroup.setAcceptanceEmailText(updatedGroup.getAcceptanceEmailText());
        existingGroup.setAcceptanceInstructions(updatedGroup.getAcceptanceInstructions());

        return groupRepository.save(existingGroup);
    }

    /**
     * Updates the logo filename for a research group.
     *
     * @param slug The group's slug
     * @param logoFilename The new logo filename
     * @throws ResourceNotFoundException if the group is not found
     */
    @Override
    @Transactional
    public void updateGroupLogo(@NotBlank String slug, @NotBlank String logoFilename) {
        ResearchGroup group = getGroupBySlug(slug);
        group.setLogoFilename(logoFilename);
        groupRepository.save(group);
    }
}