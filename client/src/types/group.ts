/**
 * Represents a research group in the system
 */
export interface Group {
  /** Unique identifier for the group */
  id: string;
  /** Name of the group */
  name: string;
  /** Description of the group's research focus */
  description: string;
  /** Timestamp when the group was created */
  createdAt: string;
  /** Timestamp when the group was last updated */
  updatedAt: string;
}

/**
 * Extended group details including statistics and members
 */
export interface GroupDetails extends Group {
  /** Number of active topics in the group */
  topicCount: number;
  /** Number of theses in the group */
  thesisCount: number;
  /** List of group members */
  members: GroupMember[];
}

/**
 * Represents a member of a research group
 */
export interface GroupMember {
  /** Unique identifier for the group membership */
  id: string;
  /** ID of the user */
  userId: string;
  /** ID of the group */
  groupId: string;
  /** Role of the member in the group */
  role: GroupRole;
  /** Name of the member */
  name: string;
  /** Email of the member */
  email: string;
}

/**
 * Available roles for group members
 */
export enum GroupRole {
  /** Can manage group settings and members */
  ADMIN = 'ADMIN',
  /** Can create and manage topics */
  ADVISOR = 'ADVISOR',
  /** Can supervise theses */
  SUPERVISOR = 'SUPERVISOR',
  /** Can apply for topics and submit theses */
  STUDENT = 'STUDENT'
}

/**
 * Filter options for group queries
 */
export interface GroupFilter {
  /** Filter by group ID */
  groupId?: string;
  /** Filter by member role */
  role?: GroupRole;
}
