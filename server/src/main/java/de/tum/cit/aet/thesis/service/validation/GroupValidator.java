package de.tum.cit.aet.thesis.service.validation;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;

/**
 * Utility class for group-related validation logic.
 * Centralizes group validation to ensure consistency across the application.
 */
public class GroupValidator {
    /**
     * Validates that a topic belongs to the specified group.
     *
     * @param topic The topic to validate
     * @param groupId The expected group ID
     * @throws ResourceInvalidParametersException if the topic doesn't belong to the group
     */
    public static void validateTopicGroup(Topic topic, Long groupId) {
        if (topic != null && !topic.getGroup().getId().equals(groupId)) {
            throw new ResourceInvalidParametersException("The selected topic does not belong to the specified group.");
        }
    }

    /**
     * Validates that a group is active.
     *
     * @param group The group to validate
     * @throws ResourceInvalidParametersException if the group is inactive
     */
    public static void validateGroupActive(Group group) {
        if (!group.isActive()) {
            throw new ResourceInvalidParametersException("The selected group is inactive.");
        }
    }
}