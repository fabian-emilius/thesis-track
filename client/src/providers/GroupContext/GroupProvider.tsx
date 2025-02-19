import { ReactNode, useCallback, useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Group, GroupRole } from '../../types/group';
import { GroupContext } from './context';

/**
 * Props for the GroupProvider component
 */
interface GroupProviderProps {
  children: ReactNode;
}

/**
 * Provider component that manages group-related state and functionality.
 * Handles:
 * - Current group selection and management
 * - User's group memberships and roles
 * - Group-based routing and navigation
 * - Group permission checks
 */
export function GroupProvider({ children }: GroupProviderProps) {
  const [currentGroup, setCurrentGroup] = useState<Group>();
  const [userGroups, setUserGroups] = useState<Group[]>([]);
  const [userGroupRoles, setUserGroupRoles] = useState<GroupRole[]>([]);
  const location = useLocation();
  const navigate = useNavigate();

  // Fetch user groups and roles on mount
  useEffect(() => {
    // TODO: Implement API call to fetch user groups and roles
    const fetchUserGroups = async () => {
      try {
        // const response = await fetch('/api/v2/groups/me');
        // const data = await response.json();
        // setUserGroups(data.groups);
        // setUserGroupRoles(data.roles);
      } catch (error) {
        console.error('Failed to fetch user groups:', error);
      }
    };

    fetchUserGroups();
  }, []);

  // Update current group based on URL path
  useEffect(() => {
    const groupSlug = location.pathname.split('/').find((part) => 
      userGroups.some((group) => group.slug === part)
    );

    if (groupSlug) {
      const group = userGroups.find((g) => g.slug === groupSlug);
      if (group && group !== currentGroup) {
        setCurrentGroup(group);
      }
    }
  }, [location.pathname, userGroups, currentGroup]);

  // Check if user has a specific role in a group
  const hasGroupRole = useCallback(
    (groupId: string, role: string) => {
      return userGroupRoles.some(
        (groupRole) => groupRole.groupId === groupId && groupRole.role === role
      );
    },
    [userGroupRoles]
  );

  // Convenience methods for common role checks
  const isGroupAdmin = useCallback(
    (groupId: string) => hasGroupRole(groupId, 'admin'),
    [hasGroupRole]
  );

  const isGroupSupervisor = useCallback(
    (groupId: string) => hasGroupRole(groupId, 'supervisor'),
    [hasGroupRole]
  );

  const isGroupAdvisor = useCallback(
    (groupId: string) => hasGroupRole(groupId, 'advisor'),
    [hasGroupRole]
  );

  // Memoize context value to prevent unnecessary re-renders
  const contextValue = useMemo(
    () => ({
      currentGroup,
      userGroups,
      userGroupRoles,
      setCurrentGroup,
      hasGroupRole,
      isGroupAdmin,
      isGroupSupervisor,
      isGroupAdvisor,
    }),
    [currentGroup, userGroups, userGroupRoles, hasGroupRole, isGroupAdmin, isGroupSupervisor, isGroupAdvisor]
  );

  return (
    <GroupContext.Provider value={contextValue}>
      {children}
    </GroupContext.Provider>
  );
}
