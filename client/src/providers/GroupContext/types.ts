/**
 * Represents a research or academic group within the thesis management system.
 * Groups are organizational units that can manage theses, topics and members.
 * @interface IGroup
 * @property {string} id - Unique identifier for the group
 * @property {string} slug - URL-friendly identifier for the group
 * @property {string} name - Display name of the group
 * @property {string} description - Detailed description of the group's focus and activities
 * @property {string} [logoUrl] - Optional URL to the group's logo image
 * @property {string} [externalLink] - Optional URL to the group's external website
 * @property {string} createdAt - ISO timestamp of when the group was created
 * @property {string} updatedAt - ISO timestamp of the group's last update
 */
export interface IGroup {
  id: string
  slug: string
  name: string
  description: string
  logoUrl?: string
  externalLink?: string
  createdAt: string
  updatedAt: string
}

/**
 * Configuration settings for a group's communication and workflow.
 * @interface IGroupSettings
 * @property {string} acceptanceEmailTemplate - Template for emails sent when accepting thesis applications
 * @property {string} postAcceptanceInstructions - Instructions shown to students after thesis acceptance
 * @property {string} emailFooter - Standard footer text appended to all group emails
 */
export interface IGroupSettings {
  acceptanceEmailTemplate: string
  postAcceptanceInstructions: string
  emailFooter: string
}

/**
 * Represents a member of a research group with their associated role.
 * @interface IGroupMember
 * @property {string} userId - Unique identifier of the user
 * @property {'supervisor' | 'advisor' | 'group_admin'} role - Member's role in the group
 *   - supervisor: Can oversee theses and manage group
 *   - advisor: Can advise students and review theses
 *   - group_admin: Has full administrative rights for the group
 */
export interface IGroupMember {
  userId: string
  role: 'supervisor' | 'advisor' | 'group_admin'
}

/**
 * Context value interface for managing groups within the application.
 * Provides state and methods for group operations.
 * @interface IGroupContextValue
 * @property {IGroup} [currentGroup] - Currently selected group
 * @property {IGroup[]} groups - List of all available groups
 * @property {boolean} isLoading - Loading state indicator
 * @property {string} [error] - Error message if operation failed
 * @property {function} setCurrentGroup - Sets the currently active group
 * @property {function} fetchGroups - Retrieves all available groups
 * @property {function} createGroup - Creates a new group
 * @property {function} updateGroup - Updates an existing group's information
 * @property {function} deleteGroup - Removes a group
 * @property {function} updateGroupSettings - Updates a group's settings
 * @property {function} addGroupMember - Adds a new member to a group
 * @property {function} removeGroupMember - Removes a member from a group
 */
export interface IGroupContextValue {
  currentGroup?: IGroup
  groups: IGroup[]
  isLoading: boolean
  error?: string
  setCurrentGroup: (group: IGroup | undefined) => void
  fetchGroups: () => Promise<void>
  createGroup: (group: Omit<IGroup, 'id' | 'createdAt' | 'updatedAt'>) => Promise<IGroup>
  updateGroup: (groupId: string, group: Partial<IGroup>) => Promise<IGroup>
  deleteGroup: (groupId: string) => Promise<void>
  updateGroupSettings: (groupId: string, settings: Partial<IGroupSettings>) => Promise<void>
  addGroupMember: (groupId: string, userId: string, role: IGroupMember['role']) => Promise<void>
  removeGroupMember: (groupId: string, userId: string) => Promise<void>
}