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
/**
 * API response interface for group data
 * @interface IGroupResponse
 */
export interface IGroupResponse {
  id: string
  slug: string
  name: string
  description: string
  logoUrl: string | null
  externalLink: string | null
  createdAt: string
  updatedAt: string
  members: IGroupMemberResponse[]
  settings: IGroupSettingsResponse
}

/**
 * Base group interface without members and settings
 * @interface IGroup
 */
export interface IGroup extends Omit<IGroupResponse, 'members' | 'settings'> {}

/**
 * Configuration settings for a group's communication and workflow.
 * @interface IGroupSettings
 * @property {string} acceptanceEmailTemplate - Template for emails sent when accepting thesis applications
 * @property {string} postAcceptanceInstructions - Instructions shown to students after thesis acceptance
 * @property {string} emailFooter - Standard footer text appended to all group emails
 */
/**
 * API response interface for group settings
 * @interface IGroupSettingsResponse
 */
export interface IGroupSettingsResponse {
  acceptanceEmailTemplate: string | null
  postAcceptanceInstructions: string | null
  emailFooter: string | null
  updatedAt: string
}

/**
 * Group settings without timestamp
 * @interface IGroupSettings
 */
export interface IGroupSettings extends Omit<IGroupSettingsResponse, 'updatedAt'> {}

/**
 * Payload for updating group settings
 * @interface IUpdateGroupSettingsPayload
 */
export interface IUpdateGroupSettingsPayload extends Partial<IGroupSettings> {}

/**
 * Represents a member of a research group with their associated role.
 * @interface IGroupMember
 * @property {string} userId - Unique identifier of the user
 * @property {'supervisor' | 'advisor' | 'group_admin'} role - Member's role in the group
 *   - supervisor: Can oversee theses and manage group
 *   - advisor: Can advise students and review theses
 *   - group_admin: Has full administrative rights for the group
 */
/**
 * Available roles for group members
 * @type {GroupRole}
 */
export type GroupRole = 'supervisor' | 'advisor' | 'group_admin'

/**
 * Light user information included in member response
 * @interface IGroupMemberUser
 */
export interface IGroupMemberUser {
  firstName: string | null
  lastName: string | null
  email: string | null
  avatar: string | null
}

/**
 * API response interface for group member
 * @interface IGroupMemberResponse
 */
export interface IGroupMemberResponse {
  userId: string
  role: GroupRole
  joinedAt: string
  user: IGroupMemberUser
}

/**
 * Basic group member information
 * @interface IGroupMember
 */
export interface IGroupMember extends Pick<IGroupMemberResponse, 'userId' | 'role'> {}

/**
 * Payload for adding new group member
 * @interface IAddGroupMemberPayload
 */
export interface IAddGroupMemberPayload extends IGroupMember {}

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
export interface IGroupContextState {
  currentGroup?: IGroupResponse
  groups: IGroupResponse[]
  isLoading: boolean
  error?: string
}

export interface IGroupContextOperations {
  setCurrentGroup: (group: IGroupResponse | undefined) => void
  fetchGroups: () => Promise<void>
  createGroup: (group: Omit<IGroup, 'id' | 'createdAt' | 'updatedAt'>) => Promise<IGroupResponse>
  updateGroup: (groupId: string, group: Partial<IGroup>) => Promise<IGroupResponse>
  deleteGroup: (groupId: string) => Promise<void>
  updateGroupSettings: (groupId: string, settings: IUpdateGroupSettingsPayload) => Promise<void>
  addGroupMember: (groupId: string, payload: IAddGroupMemberPayload) => Promise<void>
  removeGroupMember: (groupId: string, userId: string) => Promise<void>
}

/**
 * Combined interface for group context value
 * @interface IGroupContextValue
 */
export interface IGroupContextValue extends IGroupContextState, IGroupContextOperations {}

// Export all types and interfaces
export type {
  IGroupResponse,
  IGroup,
  IGroupSettings,
  IGroupSettingsResponse,
  IUpdateGroupSettingsPayload,
  IGroupMember,
  IGroupMemberResponse,
  IGroupMemberUser,
  IAddGroupMemberPayload,
  GroupRole,
  IGroupContextState,
  IGroupContextOperations
}