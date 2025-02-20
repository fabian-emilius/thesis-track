import { useMemo } from 'react';
import { useAuthenticationContext } from './authentication';
import { useGroupsContext } from '../providers/GroupsProvider/hooks';

/**
 * Represents the possible roles a user can have within a group
 * @typedef {'supervisor' | 'advisor' | 'group_admin'} GroupRole
 */
export type GroupRole = 'supervisor' | 'advisor' | 'group_admin';

/**
 * Type guard to check if a string is a valid GroupRole
 * @param role - The role to check
 * @returns True if the role is a valid GroupRole
 */
export function isGroupRole(role: unknown): role is GroupRole {
  return typeof role === 'string' && ['supervisor', 'advisor', 'group_admin'].includes(role);
}

/**
 * Role hierarchy configuration with weights
 * Higher weight means more permissions
 * @constant
 * @type {Record<GroupRole, number>}
 */
export const ROLE_HIERARCHY: Record<GroupRole, number> = {
  advisor: 1,
  supervisor: 2,
  group_admin: 3,
};

/**
 * Hook to check various permissions for a user within a group
 * @param groupId - The ID of the group to check permissions for
 * @returns Object containing various permission flags and the user's role
 * 
 * @example
 * const { canManageGroup, canManageTopics } = useGroupPermissions(groupId);
 * if (canManageGroup) {
 *   // Perform group management action
 * }
 */
export function useGroupPermissions(groupId?: string) {
  const { user } = useAuthenticationContext();
  const { groups } = useGroupsContext();

  return useMemo(() => {
    const isSystemAdmin = user?.roles.includes('admin') ?? false;
    
    if (!groupId || !user) {
    return {
      canManageGroup: isSystemAdmin,
      canManageMembers: isSystemAdmin,
      canManageTopics: isSystemAdmin,
      canReviewApplications: isSystemAdmin,
      groupRole: undefined as GroupRole | undefined,
    };
  }

  const group = groups.find(g => g.id === groupId);
  const membership = group?.members?.find(m => m.userId === user.id);
  const groupRole = membership?.role;

  if (groupRole && !isGroupRole(groupRole)) {
    return {
      canManageGroup: isSystemAdmin,
      canManageMembers: isSystemAdmin,
      canManageTopics: isSystemAdmin,
      canReviewApplications: isSystemAdmin,
      groupRole: undefined,
    };
  }

  const isGroupAdmin = groupRole === 'group_admin';
  const isSupervisor = groupRole === 'supervisor';
  const isAdvisor = groupRole === 'advisor';

  return {
    canManageGroup: isSystemAdmin || isGroupAdmin,
    canManageMembers: isSystemAdmin || isGroupAdmin,
    canManageTopics: isSystemAdmin || isGroupAdmin || isSupervisor,
    canReviewApplications: isSystemAdmin || isGroupAdmin || isSupervisor || isAdvisor,
    groupRole: isGroupRole(groupRole ?? '') ? groupRole : undefined,
  };
  }, [user, groups, groupId]); // Memoize based on these dependencies
}

/* Suggested unit tests:
 * 1. Should return all permissions false for undefined groupId
 * 2. Should return all permissions true for system admin
 * 3. Should return correct permissions for group_admin role
 * 4. Should return correct permissions for supervisor role
 * 5. Should return correct permissions for advisor role
 * 6. Should handle missing user data correctly
 * 7. Should handle missing group data correctly
 * 8. Should memoize results for same inputs
 */

/**
 * Hook to check if a user has access to a group with a specific role level
 * @param groupId - The ID of the group to check access for
 * @param requiredRole - The minimum role level required for access
 * @returns Boolean indicating if the user has sufficient access
 * 
 * @example
 * const hasAccess = useHasGroupAccess(groupId, 'supervisor');
 * if (hasAccess) {
 *   // Perform supervisor-level action
 * }
 */
export function useHasGroupAccess(groupId?: string, requiredRole?: GroupRole) {
  const { user } = useAuthenticationContext();
  const { groups } = useGroupsContext();

  return useMemo(() => {
    if (!groupId || !user) return false;

    // System admins always have access
    if (user.roles.includes('admin')) return true;

    const group = groups.find(g => g.id === groupId);
    const membership = group?.members?.find(m => m.userId === user.id);
    
    if (!requiredRole) return !!membership;
    
    if (!membership?.role || !isGroupRole(membership.role)) return false;

    const userRoleWeight = ROLE_HIERARCHY[membership.role];
    const requiredRoleWeight = ROLE_HIERARCHY[requiredRole];

    return userRoleWeight >= requiredRoleWeight;
  }, [user, groups, groupId, requiredRole]);
}

/* Suggested unit tests:
 * 1. Should return false for undefined groupId
 * 2. Should return true for system admin regardless of role
 * 3. Should return true when user has higher role than required
 * 4. Should return true when user has exact role required
 * 5. Should return false when user has lower role than required
 * 6. Should handle invalid role values correctly
 * 7. Should return false for non-member users
 * 8. Should memoize results for same inputs
 */