import { useContext } from 'react';
import { AuthenticationContext } from '../providers/AuthenticationContext/context';

/**
 * Hook for accessing authentication context
 * @returns Authentication context containing user information and auth state
 */
export const useAuthentication = () => {
  const context = useContext(AuthenticationContext);
  if (!context) {
    throw new Error('useAuthentication must be used within an AuthenticationProvider');
  }
  return context;
};

/**
 * Hook for checking if the current user has a specific role
 * @param role - Role to check for
 * @returns Boolean indicating if user has the role
 */
export const useHasRole = (role: string): boolean => {
  const { user } = useAuthentication();
  return user?.roles?.includes(role) || false;
};

/**
 * Hook for checking if the current user is authenticated
 * @returns Boolean indicating if user is authenticated
 */
export const useIsAuthenticated = (): boolean => {
  const { user } = useAuthentication();
  return !!user;
};
