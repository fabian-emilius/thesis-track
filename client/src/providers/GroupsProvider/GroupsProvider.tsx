import React, { useState, useCallback } from 'react'
import { Group, GroupMember } from '../../requests/responses/group'
import GroupsContext, { GroupError, GroupsContextValue } from './context'

interface GroupsProviderProps {
  children: React.ReactNode
}

/**
 * Provider component for managing group-related state and operations
 * Handles group CRUD operations and member management
 */
const GroupsProvider: React.FC<GroupsProviderProps> = ({ children }) => {
  const [groups, setGroups] = useState<Group[]>([])
  const [loading, setLoading] = useState(false)
  const [loadingStates, setLoadingStates] = useState<GroupsContextValue['loadingStates']>({
    fetchGroups: false,
    fetchGroup: false,
    createGroup: false,
    updateGroup: false,
    addMember: false,
    removeMember: false,
  })
  const [error, setError] = useState<GroupError | null>(null)
  const [selectedGroup, setSelectedGroup] = useState<Group | undefined>()

  /**
   * Updates loading state for a specific operation
   */
  const setOperationLoading = (
    operation: keyof GroupsContextValue['loadingStates'],
    isLoading: boolean,
  ) => {
    setLoadingStates((prev) => ({ ...prev, [operation]: isLoading }))
    setLoading(isLoading)
  }

  /**
   * Handles API errors and sets appropriate error state
   */
  const handleError = (error: unknown) => {
    const groupError: GroupError = new Error('Operation failed')
    if (error instanceof Error) {
      groupError.message = error.message
      groupError.stack = error.stack
    }
    setError(groupError)
  }

  const fetchGroups = useCallback(async () => {
    setOperationLoading('fetchGroups', true)
    try {
      // TODO: Implement API call
      const response = await fetch('/api/v2/groups')
      const data = await response.json()
      setGroups(data.groups)
      setError(null)
    } catch (err) {
      handleError(err)
    } finally {
      setOperationLoading('fetchGroups', false)
    }
  }, [])

  const fetchGroup = useCallback(async (slug: string) => {
    setOperationLoading('fetchGroup', true)
    try {
      // TODO: Implement API call
      const response = await fetch(`/api/v2/groups/${slug}`)
      const data = await response.json()
      setSelectedGroup(data)
      setError(null)
    } catch (err) {
      handleError(err)
    } finally {
      setOperationLoading('fetchGroup', false)
    }
  }, [])

  const createGroup = useCallback(
    async (data: Partial<Group>) => {
      setOperationLoading('createGroup', true)
      try {
        // TODO: Implement API call
        await fetch('/api/v2/groups', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(data),
        })
        await fetchGroups()
        setError(null)
      } catch (err) {
        handleError(err)
      } finally {
        setOperationLoading('createGroup', false)
      }
    },
    [fetchGroups],
  )

  const updateGroup = useCallback(
    async (slug: string, data: Partial<Group>) => {
      setOperationLoading('updateGroup', true)
      try {
        // TODO: Implement API call
        await fetch(`/api/v2/groups/${slug}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(data),
        })
        await fetchGroups()
        setError(null)
      } catch (err) {
        handleError(err)
      } finally {
        setOperationLoading('updateGroup', false)
      }
    },
    [fetchGroups],
  )

  const addGroupMember = useCallback(
    async (slug: string, userId: string, role: GroupMember['role']) => {
      setOperationLoading('addMember', true)
      try {
        // TODO: Implement API call
        await fetch(`/api/v2/groups/${slug}/members`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ userId, role }),
        })
        await fetchGroup(slug)
        setError(null)
      } catch (err) {
        handleError(err)
      } finally {
        setOperationLoading('addMember', false)
      }
    },
    [fetchGroup],
  )

  const removeGroupMember = useCallback(
    async (slug: string, userId: string) => {
      setOperationLoading('removeMember', true)
      try {
        // TODO: Implement API call
        await fetch(`/api/v2/groups/${slug}/members/${userId}`, {
          method: 'DELETE',
        })
        await fetchGroup(slug)
        setError(null)
      } catch (err) {
        handleError(err)
      } finally {
        setOperationLoading('removeMember', false)
      }
    },
    [fetchGroup],
  )

  return (
    <GroupsContext.Provider
      value={{
        groups,
        loading,
        loadingStates,
        error,
        selectedGroup,
        fetchGroups,
        fetchGroup,
        createGroup,
        updateGroup,
        addGroupMember,
        removeGroupMember,
      }}
    >
      {children}
    </GroupsContext.Provider>
  )
}

export default GroupsProvider
