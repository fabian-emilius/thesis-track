import React, { createContext, useContext, useState, PropsWithChildren, useEffect } from 'react'
import { useLocalStorage } from '../../hooks/local-storage'
import { showSimpleError } from '../../utils/notification'
import { doRequest } from '../../requests/request'

/**
 * Represents a group in the system
 */
export interface Group {
  /** Unique identifier for the group */
  id: string
  /** Display name of the group */
  name: string
  /** Detailed description of the group */
  description: string
}

/**
 * Context for managing group state across the application
 */
interface GroupContextType {
  /** Currently selected group */
  currentGroup: Group | null
  /** Function to update the current group */
  setCurrentGroup: (group: Group | null) => void
  /** List of available groups */
  groups: Group[]
  /** Function to update the list of groups */
  setGroups: (groups: Group[]) => void
  /** Loading state for group operations */
  loading: boolean
  /** Error state for group operations */
  error: string | null
}

const GroupContext = createContext<GroupContextType | undefined>(undefined)

/**
 * Provider component for managing group state and operations
 */
export const GroupProvider = ({ children }: PropsWithChildren) => {
  const [groups, setGroups] = useState<Group[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [currentGroup, setCurrentGroup] = useLocalStorage<Group | null>('current_group', {
    usingJson: true,
  })

  // Fetch available groups on mount
  useEffect(() => {
    const fetchGroups = async () => {
      try {
        setLoading(true)
        setError(null)

        const response = await doRequest<Group[]>('/v2/groups', {
          method: 'GET',
          requiresAuth: true,
        })

        if (!response.ok) {
          throw new Error('Failed to fetch groups')
        }

        setGroups(response.data)

        // If current group is not in the list, reset it
        if (currentGroup && !response.data.find(g => g.id === currentGroup.id)) {
          setCurrentGroup(null)
        }
      } catch (err) {
        const message = err instanceof Error ? err.message : 'An error occurred while fetching groups'
        setError(message)
        showSimpleError(message)
      } finally {
        setLoading(false)
      }
    }

    fetchGroups()
  }, [])

  return (
    <GroupContext.Provider
      value={{
        currentGroup,
        setCurrentGroup,
        groups,
        setGroups,
        loading,
        error,
      }}
    >
      {children}
    </GroupContext.Provider>
  )
}

/**
 * Hook for accessing group context
 * @throws {Error} When used outside of GroupProvider
 */
export const useGroupContext = () => {
  const context = useContext(GroupContext)
  if (context === undefined) {
    throw new Error('useGroupContext must be used within a GroupProvider')
  }
  return context
}
