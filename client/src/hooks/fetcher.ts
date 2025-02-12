import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { request } from '../requests/request';
import { useGroupContext } from '../providers/GroupContext/context';

export function useApiPdfFile(fileId: string) {
  return useQuery({
    queryKey: ['files', 'pdf', fileId],
    queryFn: async () => {
      const response = await fetch(`/api/v2/files/${fileId}`, {
        credentials: 'include',
      });
      if (!response.ok) throw new Error('Failed to fetch PDF');
      return response.blob();
    },
    enabled: !!fileId,
  });
}

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
