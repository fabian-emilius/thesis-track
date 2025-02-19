import { createContext } from 'react'
import { Group, GroupMember } from '../../requests/responses/group'

/**
 * Error type for group operations
 */
export interface GroupError extends Error {
  code?: string
  details?: unknown
}

/**
 * Context value interface for GroupsProvider
 */
export interface GroupsContextValue {
  /** List of all available groups */
  groups: Group[]
  /** Global loading state for group operations */
  loading: boolean
  /** Loading states for individual operations */
  loadingStates: {
    fetchGroups: boolean
    fetchGroup: boolean
    createGroup: boolean
    updateGroup: boolean
    addMember: boolean
    removeMember: boolean
  }
  /** Current error state */
  error: GroupError | null
  /** Currently selected group */
  selectedGroup?: Group

  /**
   * Fetches all available groups
   * @throws {GroupError} If the fetch operation fails
   */
  fetchGroups: () => Promise<void>

  /**
   * Fetches a specific group by slug
   * @param slug - The unique slug identifier of the group
   * @throws {GroupError} If the fetch operation fails
   */
  fetchGroup: (slug: string) => Promise<void>

  /**
   * Creates a new group
   * @param data - Partial group data for creation
   * @throws {GroupError} If the creation fails
   */
  createGroup: (data: Partial<Group>) => Promise<void>

  /**
   * Updates an existing group
   * @param slug - The unique slug identifier of the group
   * @param data - Partial group data for update
   * @throws {GroupError} If the update fails
   */
  updateGroup: (slug: string, data: Partial<Group>) => Promise<void>

  /**
   * Adds a new member to a group
   * @param slug - The unique slug identifier of the group
   * @param userId - The ID of the user to add
   * @param role - The role to assign to the user
   * @throws {GroupError} If adding the member fails
   */
  addGroupMember: (slug: string, userId: string, role: GroupMember['role']) => Promise<void>

  /**
   * Removes a member from a group
   * @param slug - The unique slug identifier of the group
   * @param userId - The ID of the user to remove
   * @throws {GroupError} If removing the member fails
   */
  removeGroupMember: (slug: string, userId: string) => Promise<void>
}

const GroupsContext = createContext<GroupsContextValue | undefined>(undefined)

export default GroupsContext
