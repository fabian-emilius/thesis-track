import { useQuery } from '@tanstack/react-query'
import { request } from '../requests/request'
import { useContext } from 'react'
import { AuthenticationContext } from '../providers/AuthenticationContext/context'

export function useAuthenticationContext() {
  const context = useContext(AuthenticationContext)
  if (!context) {
    throw new Error('useAuthenticationContext must be used within AuthenticationProvider')
  }
  return context
}

export function useUser(userId: string) {
  return useQuery({
    queryKey: ['users', userId],
    queryFn: () => request.get(`/v2/users/${userId}`),
    enabled: !!userId,
  })
}

export function useLoggedInUser() {
  return useQuery({
    queryKey: ['users', 'me'],
    queryFn: () => request.get('/v2/users/me'),
  })
}

export function useUserGroups() {
  return useQuery({
    queryKey: ['user', 'groups'],
    queryFn: () => request.get('/v2/users/me/groups'),
  })
}

export function useManagementAccess() {
  const { isAdmin } = useAuthenticationContext()
  const { data: userGroups = [] } = useUserGroups()

  return {
    hasSystemAccess: isAdmin,
    hasGroupAccess: userGroups.some((group) => group.role === 'GROUP_ADMIN'),
  }
}

export function useAuthenticatedUser() {
  return useQuery({
    queryKey: ['user'],
    queryFn: () => request.get('/v2/users/me'),
  })
}

export function useIsAdmin() {
  const { data: user } = useAuthenticatedUser()
  return user?.roles?.includes('admin') || false
}
