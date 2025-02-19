package de.tum.cit.aet.thesis.service.interfaces;

import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing research groups.
 */
public interface IGroupService {
    /**
     * Retrieves all research groups.
     *
     * @return List of all research groups
     */
    List<ResearchGroup> getAllGroups();

    /**
     * Retrieves all groups associated with a specific user.
     *
     * @param userId The ID of the user
     * @return List of groups the user is a member of
     */
    List<ResearchGroup> getGroupsByUser(UUID userId);

    /**
     * Retrieves a group by its slug.
     *
     * @param slug The unique slug of the group
     * @return The research group
     * @throws ResourceNotFoundException if group not found
     */
    ResearchGroup getGroupBySlug(String slug);

    /**
     * Creates a new research group.
     *
     * @param group The group to create
     * @param creator The user creating the group
     * @return The created group
     * @throws ResourceAlreadyExistsException if group with slug exists
     */
    ResearchGroup createGroup(ResearchGroup group, User creator);

    /**
     * Updates an existing research group.
     *
     * @param slug The slug of the group to update
     * @param updatedGroup The updated group data
     * @return The updated group
     * @throws ResourceNotFoundException if group not found
     */
    ResearchGroup updateGroup(String slug, ResearchGroup updatedGroup);

    /**
     * Updates the logo of a research group.
     *
     * @param slug The slug of the group
     * @param logoFilename The filename of the new logo
     * @throws ResourceNotFoundException if group not found
     */
    void updateGroupLogo(String slug, String logoFilename);
}