import React, { useState, useCallback, useEffect, useMemo } from 'react';
import { Group, GroupResponse, GroupSettings, GroupMember } from '../../requests/responses/group';
import { GroupsContext } from './context';
import { request } from '../../requests/request';
import { notifications } from '@mantine/notifications';
import { useDebouncedValue } from '@mantine/hooks';

/**
 * Enum defining possible error types in group operations
 */
enum GroupError {
  FETCH = 'FETCH_ERROR',
  CREATE = 'CREATE_ERROR',
  UPDATE = 'UPDATE_ERROR',
  SETTINGS = 'SETTINGS_ERROR',
  MEMBER = 'MEMBER_ERROR',
}

interface GroupOperationState {
  creating: boolean;
  updating: boolean;
  updatingSettings: boolean;
  addingMember: boolean;
  removingMember: boolean;
}

/**
 * Props for the GroupsProvider component
 */
interface GroupsProviderProps {
  /** Child components that will have access to the groups context */
  children: React.ReactNode;
}

export function GroupsProvider({ children }: GroupsProviderProps) {
  const [groups, setGroups] = useState<Group[]>([]);
  const [currentGroup, setCurrentGroup] = useState<Group>();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<{ type: GroupError; message: string }>();
  const [operationState, setOperationState] = useState<GroupOperationState>({
    creating: false,
    updating: false,
    updatingSettings: false,
    addingMember: false,
    removingMember: false,
  });

  /**
   * Fetches all groups from the API
   * @throws {Error} When the API request fails
   */
  const fetchGroups = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await request<Group[]>('GET', '/v2/groups');
      setGroups(response);
      setError(undefined);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch groups';
      setError({ type: GroupError.FETCH, message: errorMessage });
      notifications.show({
        title: 'Error',
        message: 'Failed to load groups. Please try again later.',
        color: 'red',
      });
    } finally {
      setIsLoading(false);
    }
  }, []);

  /**
   * Fetches detailed information for a specific group
   * @param groupId The ID of the group to fetch details for
   * @returns Detailed group information including settings and members
   */
  const fetchGroupDetails = useCallback(async (groupId: string) => {
    try {
      const response = await request<GroupResponse>('GET', `/v2/groups/${groupId}`);
      return response;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch group details';
      setError({ type: GroupError.FETCH, message: errorMessage });
      notifications.show({
        title: 'Error',
        message: 'Failed to load group details. Please try again later.',
        color: 'red',
      });
      throw err;
    }
  }, []);

  /**
   * Creates a new group
   * @param group Partial group data
   * @returns Created group
   */
  const createGroup = useCallback(async (group: Partial<Group>) => {
    setOperationState(prev => ({ ...prev, creating: true }));
    try {
      const response = await request<Group>('POST', '/v2/groups', { body: group });
      await fetchGroups();
      notifications.show({
        title: 'Success',
        message: 'Group created successfully',
        color: 'green',
      });
      return response;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to create group';
      setError({ type: GroupError.CREATE, message: errorMessage });
      notifications.show({
        title: 'Error',
        message: 'Failed to create group. Please try again.',
        color: 'red',
      });
      throw err;
    } finally {
      setOperationState(prev => ({ ...prev, creating: false }));
    }
  }, [fetchGroups]);

  /**
   * Updates an existing group
   * @param groupId Group ID
   * @param group Updated group data
   * @returns Updated group
   */
  const updateGroup = useCallback(async (groupId: string, group: Partial<Group>) => {
    setOperationState(prev => ({ ...prev, updating: true }));
    try {
      const response = await request<Group>('PUT', `/v2/groups/${groupId}`, { body: group });
      await fetchGroups();
      notifications.show({
        title: 'Success',
        message: 'Group updated successfully',
        color: 'green',
      });
      return response;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to update group';
      setError({ type: GroupError.UPDATE, message: errorMessage });
      notifications.show({
        title: 'Error',
        message: 'Failed to update group. Please try again.',
        color: 'red',
      });
      throw err;
    } finally {
      setOperationState(prev => ({ ...prev, updating: false }));
    }
  }, 500);

  /**
   * Updates group settings
   * @param groupId Group ID
   * @param settings Updated settings
   * @returns Updated settings
   */
  const updateGroupSettings = useCallback(async (groupId: string, settings: Partial<GroupSettings>) => {
    setOperationState(prev => ({ ...prev, updatingSettings: true }));
    try {
      const response = await request<GroupSettings>('PUT', `/v2/groups/${groupId}/settings`, { body: settings });
      notifications.show({
        title: 'Success',
        message: 'Settings updated successfully',
        color: 'green',
      });
      return response;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to update settings';
      setError({ type: GroupError.SETTINGS, message: errorMessage });
      notifications.show({
        title: 'Error',
        message: 'Failed to update settings. Please try again.',
        color: 'red',
      });
      throw err;
    } finally {
      setOperationState(prev => ({ ...prev, updatingSettings: false }));
    }
  }, 500);

  /**
   * Adds a member to a group
   * @param groupId Group ID
   * @param userId User ID
   * @param role Member role
   */
  const addGroupMember = useCallback(async (groupId: string, userId: string, role: GroupMember['role']) => {
    setOperationState(prev => ({ ...prev, addingMember: true }));
    try {
      await request('POST', `/v2/groups/${groupId}/members`, { body: { userId, role } });
      notifications.show({
        title: 'Success',
        message: 'Member added successfully',
        color: 'green',
      });
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to add member';
      setError({ type: GroupError.MEMBER, message: errorMessage });
      notifications.show({
        title: 'Error',
        message: 'Failed to add member. Please try again.',
        color: 'red',
      });
      throw err;
    } finally {
      setOperationState(prev => ({ ...prev, addingMember: false }));
    }
  }, []);

  /**
   * Removes a member from a group
   * @param groupId Group ID
   * @param userId User ID
   */
  const removeGroupMember = useCallback(async (groupId: string, userId: string) => {
    setOperationState(prev => ({ ...prev, removingMember: true }));
    try {
      await request('DELETE', `/v2/groups/${groupId}/members/${userId}`);
      notifications.show({
        title: 'Success',
        message: 'Member removed successfully',
        color: 'green',
      });
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to remove member';
      setError({ type: GroupError.MEMBER, message: errorMessage });
      notifications.show({
        title: 'Error',
        message: 'Failed to remove member. Please try again.',
        color: 'red',
      });
      throw err;
    } finally {
      setOperationState(prev => ({ ...prev, removingMember: false }));
    }
  }, []);

  useEffect(() => {
    fetchGroups();
  }, [fetchGroups]);

  const contextValue = useMemo(() => ({
    groups,
    currentGroup,
    isLoading,
    error,
    operationState,
    fetchGroups,
    fetchGroupDetails,
    createGroup,
    updateGroup,
    updateGroupSettings,
    addGroupMember,
    removeGroupMember,
    setCurrentGroup,
  }), [
    groups,
    currentGroup,
    isLoading,
    error,
    operationState,
    fetchGroups,
    fetchGroupDetails,
    createGroup,
    updateGroup,
    updateGroupSettings,
    addGroupMember,
    removeGroupMember,
  ]);

  return (
    <GroupsContext.Provider value={contextValue}>
      {children}
    </GroupsContext.Provider>
  );
}
