import React from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { useHasGroupAccess } from '../../hooks/group-permissions';
import { useAuthenticationContext } from '../../hooks/authentication';
import { useGroupsContext } from '../../providers/GroupsProvider/hooks';
import { PageLoader } from '../PageLoader/PageLoader';
import { notifications } from '@mantine/notifications';

/**
 * Props for the GroupProtectedRoute component
 * @interface GroupProtectedRouteProps
 * @property {React.ReactNode} children - The content to render if access is granted
 * @property {'supervisor' | 'advisor' | 'group_admin'} [requiredRole] - The required role for access
 * @property {boolean} [requireAuthentication=true] - Whether authentication is required
 * @property {string} [redirectPath='/'] - Custom redirect path for unauthorized access
 * @property {React.ReactNode} [loadingComponent] - Custom loading component
 */
type GroupRole = 'supervisor' | 'advisor' | 'group_admin';

interface GroupProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: GroupRole;
  requireAuthentication?: boolean;
  redirectPath?: string;
  loadingComponent?: React.ReactNode;
  onAccessDenied?: () => void;
}

type RouteParams = {
  groupSlug: string;
};

const DEFAULT_REDIRECT = '/groups';

/**
 * A route component that protects content based on group membership and roles.
 * Handles authentication, loading states, and redirects for unauthorized access.
 *
 * @component
 * @example
 * ```tsx
 * <GroupProtectedRoute requiredRole="advisor">
 *   <GroupContent />
 * </GroupProtectedRoute>
 * ```
 */
export function GroupProtectedRoute({
  children,
  requiredRole,
  requireAuthentication = true,
  redirectPath = '/',
  loadingComponent,
}: GroupProtectedRouteProps) {
  const { groupSlug } = useParams<RouteParams>();
  const { isAuthenticated, isLoading: authLoading } = useAuthenticationContext();
  const { groups, isLoading: groupsLoading } = useGroupsContext();

  // Handle loading states
  if (authLoading || groupsLoading) {
    return loadingComponent ?? <PageLoader />;
  }

  // Handle authentication requirement
  if (requireAuthentication && !isAuthenticated) {
    notifications.show({
      title: 'Authentication Required',
      message: 'Please log in to access this resource',
      color: 'red'
    });
    return <Navigate to={redirectPath} replace />;
  }

  // Validate group existence and access
  if (!groupSlug) {
    notifications.show({
      title: 'Access Error',
      message: 'No group specified',
      color: 'red'
    });
    return <Navigate to={DEFAULT_REDIRECT} replace />;
  }

  const group = groups.find((g) => g.slug === groupSlug);
  
  if (!group) {
    notifications.show({
      title: 'Group Not Found',
      message: `The group "${groupSlug}" does not exist`,
      color: 'red'
    });
    return <Navigate to={DEFAULT_REDIRECT} replace />;
  }

  const hasAccess = useHasGroupAccess(group.id, requiredRole);

  if (requireAuthentication && !hasAccess) {
    notifications.show({
      title: 'Access Denied',
      message: requiredRole 
        ? `You need ${requiredRole} role to access this resource`
        : 'You do not have permission to access this resource',
      color: 'red'
    });
    onAccessDenied?.();
    return <Navigate to={DEFAULT_REDIRECT} replace />;
  }

  return <>{children}</>;
}