import { createContext } from 'react'

/**
 * Represents a group in the system
 */
export interface Group {
  /** Unique identifier for the group */
  id: string
  /** Display name of the group */
  name: string
  /** Optional description of the group's purpose */
  description: string
  /** Timestamp when the group was created */
  createdAt: string
  /** Timestamp when the group was last updated */
  updatedAt: string
}

/**
 * Context type definition for the groups management system
 */
export interface GroupsContextType {
  /** List of all available groups */
  groups: Group[]
  /** Currently selected group */
  selectedGroup: Group | null
  /** Function to update the selected group */
  setSelectedGroup: (group: Group | null) => void
  /** Loading state for group operations */
  loading: boolean
  /** Error state for group operations */
  error: Error | null
  /** Function to fetch all groups */
  fetchGroups: () => Promise<void>
  /** Function to create a new group */
  createGroup: (group: Partial<Group>) => Promise<void>
  /** Function to update an existing group */
  updateGroup: (id: string, group: Partial<Group>) => Promise<void>
  /** Function to delete a group */
  deleteGroup: (id: string) => Promise<void>
}

/**
 * Context for managing groups throughout the application
 */
export const GroupsContext = createContext<GroupsContextType | null>(null)
