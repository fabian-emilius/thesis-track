import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { request } from '../requests/request';
import { useGroupContext } from '../providers/GroupContext/context';

export function useApplications(groupId?: string) {
  const { currentGroup } = useGroupContext();
  const effectiveGroupId = groupId || currentGroup?.id;

  return useQuery({
    queryKey: ['applications', effectiveGroupId],
    queryFn: () => request.get(`/v2/groups/${effectiveGroupId}/applications`),
    enabled: !!effectiveGroupId,
  });
}

export function useApplication(applicationId: string) {
  return useQuery({
    queryKey: ['applications', applicationId],
    queryFn: () => request.get(`/v2/applications/${applicationId}`),
    enabled: !!applicationId,
  });
}

export function useCreateApplication() {
  const queryClient = useQueryClient();
  const { currentGroup } = useGroupContext();

  return useMutation({
    mutationFn: (data: any) =>
      request.post(`/v2/groups/${currentGroup?.id}/applications`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['applications'] });
    },
  });
}

export function useUpdateApplication(applicationId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: any) =>
      request.put(`/v2/applications/${applicationId}`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['applications'] });
    },
  });
}
