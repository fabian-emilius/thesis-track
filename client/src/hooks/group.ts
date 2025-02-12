import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Group, GroupMember } from '../types/group';
import { request } from '../requests/request';
import { ApiError } from '../types/error';
import { GroupResponse, GroupMemberResponse, CreateGroupRequest, UpdateGroupRequest, AddGroupMemberRequest } from '../requests/responses/group';
import { validateGroupData, validateMemberData } from '../utils/validation';

/**
 * Hook to fetch all groups
 * @returns Query result containing array of groups
 */
export function useGroups() {
  return useQuery<GroupResponse[], ApiError>({
    queryKey: ['groups'],
    queryFn: async () => {
      try {
        const response = await request.get<GroupResponse[]>('/v2/groups');
        return response;
      } catch (error) {
        throw new ApiError('Failed to fetch groups', error);
      }
    },
  });
}

/**
 * Hook to fetch a specific group by ID
 * @param groupId - The ID of the group to fetch
 * @returns Query result containing group data
 */
export function useGroup(groupId: string) {
  return useQuery<GroupResponse, ApiError>({
    queryKey: ['groups', groupId],
    queryFn: async () => {
      try {
        if (!groupId) throw new Error('Group ID is required');
        const response = await request.get<GroupResponse>(`/v2/groups/${groupId}`);
        return response;
      } catch (error) {
        throw new ApiError('Failed to fetch group', error);
      }
    },
    enabled: !!groupId,
  });
}

export function useGroupMembers(groupId: string) {
  return useQuery<GroupMember[]>({
    queryKey: ['groups', groupId, 'members'],
    queryFn: () => request.get(`/v2/groups/${groupId}/members`),
    enabled: !!groupId,
  });
}

/**
 * Hook to create a new group
 * @returns Mutation function for creating a group
 */
export function useCreateGroup() {
  const queryClient = useQueryClient();

  return useMutation<GroupResponse, ApiError, CreateGroupRequest>({
    mutationFn: async (data) => {
      try {
        validateGroupData(data);
        const response = await request.post<GroupResponse>('/v2/groups', data);
        return response;
      } catch (error) {
        throw new ApiError('Failed to create group', error);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['groups'] });
    },
  });
}

/**
 * Hook to update an existing group
 * @param groupId - The ID of the group to update
 * @returns Mutation function for updating a group
 */
export function useUpdateGroup(groupId: string) {
  const queryClient = useQueryClient();

  return useMutation<GroupResponse, ApiError, UpdateGroupRequest>({
    mutationFn: async (data) => {
      try {
        if (!groupId) throw new Error('Group ID is required');
        validateGroupData(data);
        const response = await request.put<GroupResponse>(`/v2/groups/${groupId}`, data);
        return response;
      } catch (error) {
        throw new ApiError('Failed to update group', error);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['groups', groupId] });
    },
  });
}

/**
 * Hook to upload a group logo
 * @param groupId - The ID of the group
 * @returns Mutation function for uploading a logo
 */
export function useUploadGroupLogo(groupId: string) {
  const queryClient = useQueryClient();

  return useMutation<void, ApiError, File>({
    mutationFn: async (file) => {
      try {
        if (!groupId) throw new Error('Group ID is required');
        if (!file || !(file instanceof File)) throw new Error('Valid file is required');
        const formData = new FormData();
        formData.append('logo', file);
        await request.post(`/v2/groups/${groupId}/logo`, formData);
      } catch (error) {
        throw new ApiError('Failed to upload group logo', error);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['groups', groupId] });
    },
  });
}

/**
 * Hook to add a member to a group
 * @param groupId - The ID of the group
 * @returns Mutation function for adding a group member
 */
export function useAddGroupMember(groupId: string) {
  const queryClient = useQueryClient();

  return useMutation<void, ApiError, AddGroupMemberRequest>({
    mutationFn: async (data) => {
      try {
        if (!groupId) throw new Error('Group ID is required');
        validateMemberData(data);
        await request.post(`/v2/groups/${groupId}/members`, data);
      } catch (error) {
        throw new ApiError('Failed to add group member', error);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['groups', groupId, 'members'] });
    },
  });
}

/**
 * Hook to remove a member from a group
 * @param groupId - The ID of the group
 * @returns Mutation function for removing a group member
 */
export function useRemoveGroupMember(groupId: string) {
  const queryClient = useQueryClient();

  return useMutation<void, ApiError, string>({
    mutationFn: async (userId) => {
      try {
        if (!groupId) throw new Error('Group ID is required');
        if (!userId) throw new Error('User ID is required');
        await request.delete(`/v2/groups/${groupId}/members/${userId}`);
      } catch (error) {
        throw new ApiError('Failed to remove group member', error);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['groups', groupId, 'members'] });
    },
  });
}
