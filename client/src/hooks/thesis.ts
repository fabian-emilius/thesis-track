import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { request } from '../requests/request'
import { useGroupContext } from '../providers/GroupContext/context'

export function useTheses(groupId?: string) {
  const { currentGroup } = useGroupContext()
  const effectiveGroupId = groupId || currentGroup?.id

  return useQuery({
    queryKey: ['theses', effectiveGroupId],
    queryFn: () => request.get(`/v2/groups/${effectiveGroupId}/theses`),
    enabled: !!effectiveGroupId,
  })
}

export function useThesis(thesisId: string) {
  return useQuery({
    queryKey: ['theses', thesisId],
    queryFn: () => request.get(`/v2/theses/${thesisId}`),
    enabled: !!thesisId,
  })
}

export function useCreateThesis() {
  const queryClient = useQueryClient()
  const { currentGroup } = useGroupContext()

  return useMutation({
    mutationFn: (data: any) => request.post(`/v2/groups/${currentGroup?.id}/theses`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['theses'] })
    },
  })
}

export function useUpdateThesis(thesisId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: any) => request.put(`/v2/theses/${thesisId}`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['theses', thesisId] })
    },
  })
}

export function useThesisComments(thesisId: string) {
  const { currentGroup } = useGroupContext()

  return useQuery({
    queryKey: ['theses', thesisId, 'comments'],
    queryFn: () => request.get(`/v2/theses/${thesisId}/comments`),
    enabled: !!thesisId && !!currentGroup,
  })
}

export function useAddThesisComment(thesisId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: any) => request.post(`/v2/theses/${thesisId}/comments`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['theses', thesisId, 'comments'],
      })
    },
  })
}
