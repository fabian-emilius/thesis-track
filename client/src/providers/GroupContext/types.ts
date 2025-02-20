// Type definitions
/**
 * Type for group operations that can be performed
 * @type {GroupOperation}
 */
export type GroupOperation = 'create' | 'update' | 'delete' | 'manage_members' | 'manage_settings'

// Interface definitions
/**
 * Represents a research or academic group within the thesis management system.
 * Groups are organizational units that can manage theses, topics and members.
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

// Context interfaces
/**
 * State interface for group context
 * @interface IGroupContextState
 */
export interface IGroupContextState {
  currentGroup?: IGroupResponse
  groups: IGroupResponse[]
  isLoading: boolean
  error?: string
}

/**
 * Operations interface for group context
 * @interface IGroupContextOperations
 */
export interface IGroupContextOperations {
  /**
   * Sets the currently active group
   */
  setCurrentGroup: (group: IGroupResponse | undefined) => void

  /**
   * Fetches all available groups
   */
  fetchGroups: () => Promise<void>

  /**
   * Creates a new group
   */
  createGroup: (group: Omit<IGroup, 'id' | 'createdAt' | 'updatedAt'>) => Promise<IGroupResponse>

  /**
   * Updates an existing group's information
   */
  updateGroup: (groupId: string, group: Partial<IGroup>) => Promise<IGroupResponse>

  /**
   * Deletes a group
   */
  deleteGroup: (groupId: string) => Promise<void>

  /**
   * Updates a group's settings
   */
  updateGroupSettings: (
    groupId: string,
    settings: IUpdateGroupSettingsPayload,
  ) => Promise<IGroupSettingsResponse>

  /**
   * Adds a new member to a group
   */
  addGroupMember: (
    groupId: string,
    payload: IAddGroupMemberPayload,
  ) => Promise<IGroupMemberResponse>

  /**
   * Removes a member from a group
   */
  removeGroupMember: (groupId: string, userId: string) => Promise<void>
}

/**
 * Combined interface for group context value
 * @interface IGroupContextValue
 */
/**
 * Combined interface for group context value
 * Provides state and operations for group management
 * @interface IGroupContextValue
 */
export interface IGroupContextValue extends IGroupContextState, IGroupContextOperations {}

// Export all types and interfaces
export type {
  // Response types
  IGroupResponse,
  IGroupMemberResponse,
  IGroupSettingsResponse,

  // Base interfaces
  IGroup,
  IGroupMember,
  IGroupSettings,
  IGroupMemberUser,

  // Payload types
  IUpdateGroupSettingsPayload,
  IAddGroupMemberPayload,

  // Context types
  IGroupContextState,
  IGroupContextOperations,
  IGroupContextValue,

  // Enums and constants
  GroupRole,
  GroupOperation,
}
