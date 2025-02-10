package de.tum.cit.aet.thesis.utility;

import de.tum.cit.aet.thesis.constants.StringLimits;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;

import java.util.Set;
import java.util.UUID;

public class GroupValidator {
    private static final int MAX_GROUP_NAME_LENGTH = 255;
    private static final int MAX_GROUP_DESCRIPTION_LENGTH = 1000;

    public static void validateGroupName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResourceInvalidParametersException("Group name cannot be empty");
        }
        if (name.length() > MAX_GROUP_NAME_LENGTH) {
            throw new ResourceInvalidParametersException("Group name cannot exceed " + MAX_GROUP_NAME_LENGTH + " characters");
        }
    }

    public static void validateGroupDescription(String description) {
        if (description != null && description.length() > MAX_GROUP_DESCRIPTION_LENGTH) {
            throw new ResourceInvalidParametersException("Group description cannot exceed " + MAX_GROUP_DESCRIPTION_LENGTH + " characters");
        }
    }

    public static void validateVisibilityGroups(Set<UUID> groupIds) {
        if (groupIds == null) {
            throw new ResourceInvalidParametersException("Visibility groups cannot be null");
        }
        if (groupIds.size() > StringLimits.MAX_ARRAY_LENGTH) {
            throw new ResourceInvalidParametersException("Too many visibility groups specified");
        }
        if (groupIds.contains(null)) {
            throw new ResourceInvalidParametersException("Visibility groups cannot contain null values");
        }
    }

    public static void validateGroupExists(UUID groupId, String message) {
        if (groupId == null) {
            throw new ResourceInvalidParametersException(message);
        }
    }
}
