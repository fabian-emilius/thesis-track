import React, { createContext, useState, PropsWithChildren } from 'react'
import { useLocalStorage } from '../../../hooks/local-storage'
import { useGroupContext } from './hooks'

/**
 * Represents a group entity in the system.
 * @interface Group
 * @property {string} id - Unique identifier for the group
 * @property {string} name - Display name of the group
 * @property {string} description - Detailed description of the group
 */
export interface Group {
  id: string
  name: string
  description: string
}

/**
 * Error thrown when group context is accessed outside of GroupProvider
 */
export class GroupContextError extends Error {
  constructor(message: string) {
    super(message)
    this.name = 'GroupContextError'
  }
}

/**
 * Context type definition for group management
 * @interface GroupContextType
 * @property {Group | null} currentGroup - Currently selected group
 * @property {(group: Group | null) => void} setCurrentGroup - Function to update current group
 * @property {Group[]} groups - List of available groups
 * @property {(groups: Group[]) => void} setGroups - Function to update groups list
 * @property {boolean} loading - Loading state indicator
 * @property {(loading: boolean) => void} setLoading - Function to update loading state
 */
interface GroupContextType {
  currentGroup: Group | null
  setCurrentGroup: (group: Group | null) => void
  groups: Group[]
  setGroups: (groups: Group[]) => void
  loading: boolean
  setLoading: (loading: boolean) => void
}

/**
 * Context for managing group-related state
 * @type {React.Context<GroupContextType | undefined>}
 */
const GroupContext = createContext<GroupContextType | undefined>(undefined)



/**
 * Provider component for group management functionality
 * @component
 * @param {PropsWithChildren} props - Component props with children
 * @returns {JSX.Element} Provider component
 * 
 * @example
 * ```tsx
 * <GroupProvider>
 *   <App />
 * </GroupProvider>
 * ```
 */
const GroupProvider = ({ children }: PropsWithChildren): JSX.Element => {
  const [currentGroup, setCurrentGroup] = useLocalStorage<Group | null>('current_group', {
    usingJson: true,
  })
  const [groups, setGroups] = useState<Group[]>([])
  const [loading, setLoading] = useState<boolean>(false)

  const value: GroupContextType = {
    currentGroup,
    setCurrentGroup,
    groups,
    setGroups,
    loading,
    setLoading,
  }

  return <GroupContext.Provider value={value}>{children}</GroupContext.Provider>
}

export { GroupContext }
export default GroupProvider