import { useState, useCallback } from 'react';
import { Group, GroupMember, GroupRole } from '../types/group';
import { request } from '../requests/request';

/**
 * Custom hook for managing group operations
 * @param groupId - The ID of the group to manage
 */
export function useGroupManagement(groupId: string) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Updates group information
   * @param data - The group data to update
   */
  const updateGroup = useCallback(
    async (data: Partial<Group>) => {
      try {
        setLoading(true);
        setError(null);
        await request<Group>('PUT', `/api/groups/${groupId}`, data);
      } catch (err) {
        setError('Failed to update group');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [groupId]
  );

  /**
   * Adds a member to the group
   * @param userId - The ID of the user to add
   * @param role - The role to assign to the user
   */
  const addMember = useCallback(
    async (userId: string, role: GroupRole) => {
      try {
        setLoading(true);
        setError(null);
        await request<GroupMember>('POST', `/api/groups/${groupId}/members`, {
          userId,
          role,
        });
      } catch (err) {
        setError('Failed to add member');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [groupId]
  );

  /**
   * Removes a member from the group
   * @param memberId - The ID of the member to remove
   */
  const removeMember = useCallback(
    async (memberId: string) => {
      try {
        setLoading(true);
        setError(null);
        await request('DELETE', `/api/groups/${groupId}/members/${memberId}`);
      } catch (err) {
        setError('Failed to remove member');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [groupId]
  );

  /**
   * Updates a member's role in the group
   * @param memberId - The ID of the member to update
   * @param role - The new role to assign
   */
  const updateMemberRole = useCallback(
    async (memberId: string, role: GroupRole) => {
      try {
        setLoading(true);
        setError(null);
        await request<GroupMember>(
          'PUT',
          `/api/groups/${groupId}/members/${memberId}`,
          { role }
        );
      } catch (err) {
        setError('Failed to update member role');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [groupId]
  );

  return {
    loading,
    error,
    updateGroup,
    addMember,
    removeMember,
    updateMemberRole,
  };
}
