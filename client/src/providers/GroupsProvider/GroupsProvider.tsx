import React, { PropsWithChildren, useCallback, useEffect, useState } from 'react'
import { Group, GroupsContext } from './context'
import { useLocalStorage } from '../../../hooks/local-storage'

/**
 * GroupsProvider manages the global state for groups in the application.
 * It provides functionality for:
 * - Fetching and caching groups
 * - Managing selected group state
 * - CRUD operations for groups
 * - Error and loading states
 */
export const GroupsProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [groups, setGroups] = useState<Group[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)
  const [selectedGroup, setSelectedGroup] = useLocalStorage<Group | null>('selected_group', {
    usingJson: true,
  })

  const fetchGroups = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await fetch('/api/groups')
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const data = await response.json()
      setGroups(data)

      // Validate selected group still exists
      if (selectedGroup && !data.find((g) => g.id === selectedGroup.id)) {
        setSelectedGroup(null)
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to fetch groups'))
    } finally {
      setLoading(false)
    }
  }, [selectedGroup])

  const createGroup = useCallback(
    async (group: Partial<Group>) => {
      setError(null)
      try {
        const response = await fetch('/api/groups', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(group),
        })
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        await fetchGroups()
      } catch (err) {
        throw err instanceof Error ? err : new Error('Failed to create group')
      }
    },
    [fetchGroups],
  )

  const updateGroup = useCallback(
    async (id: string, group: Partial<Group>) => {
      setError(null)
      try {
        const response = await fetch(`/api/groups/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(group),
        })
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        await fetchGroups()
      } catch (err) {
        throw err instanceof Error ? err : new Error('Failed to update group')
      }
    },
    [fetchGroups],
  )

  const deleteGroup = useCallback(
    async (id: string) => {
      setError(null)
      try {
        const response = await fetch(`/api/groups/${id}`, {
          method: 'DELETE',
        })
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        if (selectedGroup?.id === id) {
          setSelectedGroup(null)
        }
        await fetchGroups()
      } catch (err) {
        throw err instanceof Error ? err : new Error('Failed to delete group')
      }
    },
    [fetchGroups, selectedGroup],
  )

  useEffect(() => {
    fetchGroups()
  }, [])

  return (
    <GroupsContext.Provider
      value={{
        groups,
        selectedGroup,
        setSelectedGroup,
        loading,
        error,
        fetchGroups,
        createGroup,
        updateGroup,
        deleteGroup,
      }}
    >
      {children}
    </GroupsContext.Provider>
  )
}

export default GroupsProvider
