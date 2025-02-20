import React, { PropsWithChildren, useCallback, useEffect, useMemo, useState } from 'react'
import { GroupContext } from './context'
import {
  IGroup,
  IGroupResponse,
  IGroupContextValue,
  IGroupSettings,
  IGroupSettingsResponse,
  IAddGroupMemberPayload,
  IUpdateGroupSettingsPayload,
  GroupOperation,
} from './types'
import { doRequest } from '../../requests/request'
import { ApiError } from '../../requests/handler'

const GroupProvider: React.FC<PropsWithChildren<unknown>> = ({ children }) => {
  const [groups, setGroups] = useState<IGroupResponse[]>([])
  const [currentGroup, setCurrentGroup] = useState<IGroupResponse | undefined>()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | undefined>()
  const [operationInProgress, setOperationInProgress] = useState<GroupOperation | undefined>()

  const fetchGroups = useCallback(async () => {
    setIsLoading(true)
    setError(undefined)

    try {
      const response = await doRequest<IGroupResponse[]>('/v2/groups', {
        method: 'GET',
        requiresAuth: true,
      })

      if (response.ok) {
        setGroups(response.data)
      } else {
        throw new ApiError(response)
      }
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to fetch groups')
    } finally {
      setIsLoading(false)
    }
  }, [])

  const createGroup = useCallback(async (group: Omit<IGroup, 'id' | 'createdAt' | 'updatedAt'>) => {
    setOperationInProgress('create')
    setError(undefined)
    try {
      const response = await doRequest<IGroupResponse>('/v2/groups', {
        method: 'POST',
        requiresAuth: true,
        body: group,
      })

      if (response.ok) {
        setGroups((prev) => [...prev, response.data])
        return response.data
      }
      throw new ApiError(response)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to create group')
      throw err
    } finally {
      setOperationInProgress(undefined)
    }
  }, [])

  const updateGroup = useCallback(
    async (groupId: string, group: Partial<IGroup>) => {
      setOperationInProgress('update')
      setError(undefined)
      try {
        const response = await doRequest<IGroupResponse>(`/v2/groups/${groupId}`, {
          method: 'PUT',
          requiresAuth: true,
          body: group,
        })

        if (response.ok) {
          setGroups((prev) => prev.map((g) => (g.id === groupId ? { ...g, ...response.data } : g)))
          if (currentGroup?.id === groupId) {
            setCurrentGroup(response.data)
          }
          return response.data
        }
        throw new ApiError(response)
      } catch (err) {
        setError(err instanceof ApiError ? err.message : `Failed to update group ${groupId}`)
        throw err
      } finally {
        setOperationInProgress(undefined)
      }
    },
    [currentGroup],
  )

  const deleteGroup = useCallback(
    async (groupId: string) => {
      setOperationInProgress('delete')
      setError(undefined)
      try {
        const response = await doRequest(`/v2/groups/${groupId}`, {
          method: 'DELETE',
          requiresAuth: true,
        })

        if (response.ok) {
          setGroups((prev) => prev.filter((g) => g.id !== groupId))
          if (currentGroup?.id === groupId) {
            setCurrentGroup(undefined)
          }
        } else {
          throw new ApiError(response)
        }
      } catch (err) {
        setError(err instanceof ApiError ? err.message : `Failed to delete group ${groupId}`)
        throw err
      } finally {
        setOperationInProgress(undefined)
      }
    },
    [currentGroup],
  )

  const updateGroupSettings = useCallback(
    async (groupId: string, settings: IUpdateGroupSettingsPayload) => {
      setOperationInProgress('manage_settings')
      setError(undefined)
      try {
        const response = await doRequest<IGroupSettingsResponse>(`/v2/groups/${groupId}/settings`, {
          method: 'PUT',
          requiresAuth: true,
          body: settings,
        })

        if (!response.ok) {
          throw new ApiError(response)
        }
      } catch (err) {
        setError(
          err instanceof ApiError ? err.message : `Failed to update group settings for ${groupId}`,
        )
        throw err
      } finally {
        setOperationInProgress(undefined)
      }
    },
    [],
  )

  const addGroupMember = useCallback(
    async (groupId: string, { userId, role }: IAddGroupMemberPayload) => {
      setOperationInProgress('manage_members')
      setError(undefined)
      try {
        const response = await doRequest(`/v2/groups/${groupId}/members`, {
          method: 'POST',
          requiresAuth: true,
          body: { userId, role },
        })

        if (!response.ok) {
          throw new ApiError(response)
        }
      } catch (err) {
        setError(err instanceof ApiError ? err.message : `Failed to add member to group ${groupId}`)
        throw err
      } finally {
        setOperationInProgress(undefined)
      }
    },
    [],
  )

  const removeGroupMember = useCallback(async (groupId: string, userId: string) => {
    setOperationInProgress('manage_members')
    setError(undefined)
    try {
      const response = await doRequest(`/v2/groups/${groupId}/members/${userId}`, {
        method: 'DELETE',
        requiresAuth: true,
      })

      if (!response.ok) {
        throw new ApiError(response)
      }
    } catch (err) {
      setError(
        err instanceof ApiError ? err.message : `Failed to remove member from group ${groupId}`,
      )
      throw err
    } finally {
      setOperationInProgress(undefined)
    }
  }, [])

  useEffect(() => {
    void fetchGroups()
  }, [])

  const contextValue = useMemo<IGroupContextValue>(
    () => ({
      currentGroup,
      groups,
      isLoading,
      error,
      operationInProgress,
      setCurrentGroup,
      fetchGroups,
      createGroup,
      updateGroup,
      deleteGroup,
      updateGroupSettings,
      addGroupMember,
      removeGroupMember,
    }),
    [
      currentGroup,
      groups,
      isLoading,
      error,
      operationInProgress,
      fetchGroups,
      createGroup,
      updateGroup,
      deleteGroup,
      updateGroupSettings,
      addGroupMember,
      removeGroupMember,
    ],
  )

  return <GroupContext.Provider value={contextValue}>{children}</GroupContext.Provider>
}

export default GroupProvider
