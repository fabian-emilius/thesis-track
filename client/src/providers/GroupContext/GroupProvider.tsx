import { ReactNode, useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Group, GroupContextType, GroupMember, GroupRole } from '../../types/group';
import { GroupContext } from './context';
import { useAuthentication } from '../../hooks/authentication';
import { notifications } from '@mantine/notifications';

interface GroupProviderProps {
  children: ReactNode;
}

/**
 * GroupProvider manages the global group state and provides group-related functionality
 * throughout the application. It handles:
 * - Loading and caching available groups
 * - Managing current group selection
 * - Group role-based permissions
 * - Group CRUD operations
 */
export const GroupProvider = ({ children }: GroupProviderProps) => {
  const { user } = useAuthentication();
  const navigate = useNavigate();
  const { groupSlug } = useParams();

  const [availableGroups, setAvailableGroups] = useState<Group[]>([]);
  const [userGroups, setUserGroups] = useState<GroupMember[]>([]);
  const [currentGroup, setCurrentGroup] = useState<Group>();
  const [isLoading, setIsLoading] = useState(true);

  /**
   * Loads groups and user group memberships from the API
   * Handles error states and updates loading status
   */
  const loadGroups = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await fetch('/api/v2/groups');
      if (!response.ok) {
        throw new Error('Failed to load groups');
      }
      const groups = await response.json();
      setAvailableGroups(groups);

      if (user?.id) {
        const userGroupsResponse = await fetch(`/api/v2/users/${user.id}/groups`);
        if (!userGroupsResponse.ok) {
          throw new Error('Failed to load user groups');
        }
        const userGroupsData = await userGroupsResponse.json();
        setUserGroups(userGroupsData);
      }
    } catch (error) {
      console.error('Failed to load groups:', error);
      notifications.show({
        title: 'Error',
        message: 'Failed to load groups. Please try again.',
        color: 'red',
      });
    } finally {
      setIsLoading(false);
    }
  }, [user?.id]);

  // Load groups on mount and when user changes
  useEffect(() => {
    loadGroups();
  }, [loadGroups]);

  // Update current group when URL changes
  useEffect(() => {
    if (groupSlug && availableGroups.length > 0) {
      const group = availableGroups.find(g => g.slug === groupSlug);
      setCurrentGroup(group);
    }
  }, [groupSlug, availableGroups]);

  /**
   * Checks if the current user has a specific role in a group
   */
  const hasGroupRole = useCallback(
    (groupId: string, role: GroupRole) => {
      return userGroups.some(ug => ug.groupId === groupId && ug.role === role);
    },
    [userGroups]
  );

  /**
   * Checks if the current user is an admin of a specific group
   */
  const isGroupAdmin = useCallback(
    (groupId: string) => hasGroupRole(groupId, 'admin'),
    [hasGroupRole]
  );

  /**
   * Updates a group's information
   * Handles API communication and error states
   */
  const updateGroup = useCallback(async (groupId: string, data: Partial<Group>) => {
    try {
      const response = await fetch(`/api/v2/groups/${groupId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        throw new Error('Failed to update group');
      }

      await loadGroups();
      notifications.show({
        title: 'Success',
        message: 'Group updated successfully',
        color: 'green',
      });
    } catch (error) {
      console.error('Failed to update group:', error);
      notifications.show({
        title: 'Error',
        message: 'Failed to update group. Please try again.',
        color: 'red',
      });
      throw error;
    }
  }, [loadGroups]);

  /**
   * Creates a new group
   * Handles API communication and error states
   */
  const createGroup = useCallback(async (data: Omit<Group, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      const response = await fetch('/api/v2/groups', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        throw new Error('Failed to create group');
      }

      await loadGroups();
      notifications.show({
        title: 'Success',
        message: 'Group created successfully',
        color: 'green',
      });
    } catch (error) {
      console.error('Failed to create group:', error);
      notifications.show({
        title: 'Error',
        message: 'Failed to create group. Please try again.',
        color: 'red',
      });
      throw error;
    }
  }, [loadGroups]);

  const value = useMemo(
    () => ({
      currentGroup,
      setCurrentGroup,
      userGroups,
      availableGroups,
      hasGroupRole,
      isGroupAdmin,
      loadGroups,
      updateGroup,
      createGroup,
      isLoading,
    }),
    [currentGroup, userGroups, availableGroups, hasGroupRole, isGroupAdmin, loadGroups, updateGroup, createGroup, isLoading]
  );

  return <GroupContext.Provider value={value}>{children}</GroupContext.Provider>;
};
