import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { request } from '../requests/request';
import { useGroupContext } from '../providers/GroupContext/context';

export function useApplication(applicationId: string) {
  return useQuery({
    queryKey: ['applications', applicationId],
    queryFn: () => request.get(`/v2/applications/${applicationId}`),
    enabled: !!applicationId,
  });
}

export function useThesesByGroup(groupId?: string) {
  const { currentGroup } = useGroupContext();
  const effectiveGroupId = groupId || currentGroup?.id;

  return useQuery({
    queryKey: ['theses', 'group', effectiveGroupId],
    queryFn: () => request.get(`/v2/groups/${effectiveGroupId}/theses`),
    enabled: !!effectiveGroupId,
  });
}
