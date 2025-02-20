import { Group } from '../types/group';
import { doRequest } from '../requests/request';
import { showSimpleError } from '../utils/notification';
import { getApiResponseErrorMessage } from '../requests/handler';

/**
 * Hook providing common group-related API operations
 */
export const useGroupApi = () => {
  /**
   * Fetches a group by its ID
   * @param groupId - The ID of the group to fetch
   * @returns Promise resolving to the group or undefined if not found
   */
  const fetchGroupById = async (groupId: string): Promise<Group | undefined> => {
    try {
      const response = await doRequest(`/v2/groups/${groupId}`, {
        method: 'GET',
        requiresAuth: true,
      });

      if (response.ok) {
        const data = await response.json();
        return data as Group;
      }
      return undefined;
    } catch (error) {
      showSimpleError('Failed to fetch group details');
      return undefined;
    }
  };

  /**
   * Updates a group's settings
   * @param groupId - The ID of the group to update
   * @param data - The updated group data
   * @returns Promise resolving to success status
   */
  const updateGroup = async (groupId: string, data: Partial<Group>): Promise<boolean> => {
    try {
      const response = await doRequest(`/v2/groups/${groupId}`, {
        method: 'PUT',
        requiresAuth: true,
        data,
      });

      if (!response.ok) {
        showSimpleError(getApiResponseErrorMessage(response));
      }

      return response.ok;
    } catch (error) {
      showSimpleError('Failed to update group settings');
      return false;
    }
  };

  /**
   * Creates a new group
   * @param data - The group data
   * @returns Promise resolving to the created group or undefined on failure
   */
  const createGroup = async (data: Partial<Group>): Promise<Group | undefined> => {
    try {
      const response = await doRequest('/v2/groups', {
        method: 'POST',
        requiresAuth: true,
        data,
      });

      if (response.ok) {
        const responseData = await response.json();
        return responseData as Group;
      }
      showSimpleError(getApiResponseErrorMessage(response));
      return undefined;
    } catch (error) {
      showSimpleError('Failed to create group');
      return undefined;
    }
  };

  return {
    fetchGroupById,
    updateGroup,
    createGroup,
  };
};
