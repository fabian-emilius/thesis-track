import { useQuery } from '@tanstack/react-query';
import { request } from '../requests/request';
import { useGroupContext } from '../providers/GroupContext/context';

export function useUserGroups() {
  return useQuery({
    queryKey: ['user', 'groups'],
    queryFn: () => request.get('/v2/users/me/groups'),
  });
}

export function useAuthenticatedUser() {
  return useQuery({
    queryKey: ['user'],
    queryFn: () => request.get('/v2/users/me'),
  });
}

export function useIsAdmin() {
  const { data: user } = useAuthenticatedUser();
  return user?.roles?.includes('admin') || false;
}
