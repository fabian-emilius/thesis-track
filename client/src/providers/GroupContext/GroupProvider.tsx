import React, { createContext, useContext, useState, useEffect } from 'react'
import { useAuthenticationContext } from '@/hooks/authentication'

/**
 * Represents a group entity in the system
 * @interface Group
 */
interface Group {
  id: string
  name: string
  description: string
}

/**
 * Context type definition for group management
 * @interface GroupContextType
 */
interface GroupContextType {
  /** Currently selected group */
  currentGroup: Group | null
  /** Function to set the current group */
  setCurrentGroup: (group: Group | null) => void
  /** List of available groups */
  groups: Group[]
  /** Loading state indicator */
  loading: boolean
  /** Error message if any */
  error: string | null
  /** Function to clear any existing error */
  clearError: () => void
}

const GroupContext = createContext<GroupContextType | undefined>(undefined)

/**
 * Custom hook to access the group context
 * @throws {Error} If used outside of GroupProvider
 * @returns {GroupContextType} The group context value
 */
export const useGroupContext = (): GroupContextType => {
  const context = useContext(GroupContext)
  if (context === undefined) {
    throw new Error('useGroupContext must be used within a GroupProvider')
  }
  return context
}

/**
 * Provider component for group management functionality
 * @component
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.children - Child components
 */
export const GroupProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentGroup, setCurrentGroup] = useState<Group | null>(() => {
    const stored = localStorage.getItem('currentGroup')
    return stored ? JSON.parse(stored) : null
  })
  const [groups, setGroups] = useState<Group[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const auth = useAuthenticationContext()

  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/api/groups`, {
          headers: {
            'Authorization': `Bearer ${auth.token}`
          }
        })
        if (!response.ok) {
          throw new Error(`Failed to fetch groups: ${response.status} ${response.statusText}`)
        }
        const data = (await response.json()) as Group[]
        setGroups(data)
        setError(null)
      } catch (error) {
        const message = error instanceof Error ? error.message : 'Failed to fetch groups'
        setError(message)
        console.error('Error fetching groups:', error)
      } finally {
        setLoading(false)
      }
    }

    if (auth.isAuthenticated) {
      fetchGroups()
    }
  }, [auth.isAuthenticated])

  /**
   * Handles setting the current group and persisting it to localStorage
   * @param {Group | null} group - The group to set as current
   */
  const handleSetCurrentGroup = (group: Group | null): void => {
    setCurrentGroup(group)
    if (group) {
      try {
        localStorage.setItem('currentGroup', JSON.stringify(group))
      } catch (error) {
        console.error('Failed to save group to localStorage:', error)
        setError('Failed to save group selection')
      }
    } else {
      localStorage.removeItem('currentGroup')
    }
  }

  const clearError = () => setError(null)

  return (
    <GroupContext.Provider 
      value={{ 
        currentGroup, 
        setCurrentGroup: handleSetCurrentGroup, 
        groups, 
        loading,
        error,
        clearError
      }}
    >
      {children}
    </GroupContext.Provider>
  )
}
