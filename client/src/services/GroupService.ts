import { doRequest, ApiResponse } from '../requests/request';

export interface IGroup {
  groupId: string;
  name: string;
  description: string;
}

/**
 * Fetch all groups.
 * @returns A promise resolving to an API response containing a list of groups.
 */
export const fetchAllGroups = async (): Promise<ApiResponse<IGroup[]>> => {
  return doRequest<IGroup[]>('/v2/groups', {
    method: 'GET',
    requiresAuth: true,
  });
};

/**
 * Fetch details of a specific group.
 * @param groupId - The ID of the group to fetch details for.
 * @returns A promise resolving to an API response containing the group details.
 */
export const fetchGroupDetails = async (groupId: string): Promise<ApiResponse<IGroup>> => {
  return doRequest<IGroup>(`/v2/groups/${groupId}`, {
    method: 'GET',
    requiresAuth: true,
  });
};
