import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { request } from '../requests/request'
import { useGroupContext } from '../providers/GroupContext/context'

export function useTopics(groupId?: string) {
  const { currentGroup } = useGroupContext()
  const effectiveGroupId = groupId || currentGroup?.id

  return useQuery({
    queryKey: ['topics', effectiveGroupId],
    queryFn: () => request.get(`/v2/groups/${effectiveGroupId}/topics`),
    enabled: !!effectiveGroupId,
  })
}

export function useTopic(topicId: string) {
  return useQuery({
    queryKey: ['topics', topicId],
    queryFn: () => request.get(`/v2/topics/${topicId}`),
    enabled: !!topicId,
  })
}

export function useCreateTopic() {
  const queryClient = useQueryClient()
  const { currentGroup } = useGroupContext()

  return useMutation({
    mutationFn: (data: any) => request.post(`/v2/groups/${currentGroup?.id}/topics`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['topics'] })
    },
  })
}

export function useUpdateTopic(topicId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: any) => request.put(`/v2/topics/${topicId}`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['topics', topicId] })
    },
  })
}

export function useCloseTopic(topicId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: () => request.post(`/v2/topics/${topicId}/close`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['topics', topicId] })
    },
  })
}
