export interface Group {
  id: string;
  name: string;
  slug: string;
  description: string;
  logo?: string;
  link?: string;
  mailFooter?: string;
  acceptanceText?: string;
  createdAt: string;
  updatedAt: string;
}

export type GroupRole = 'SUPERVISOR' | 'ADVISOR' | 'GROUP_ADMIN';

export interface GroupMember {
  groupId: string;
  userId: string;
  role: GroupRole;
}

export interface GroupContextType {
  currentGroup: Group | null;
  userGroups: GroupMember[];
  setCurrentGroup: (group: Group | null) => void;
  isGroupAdmin: (groupId: string) => boolean;
  isSupervisor: (groupId: string) => boolean;
  isAdvisor: (groupId: string) => boolean;
}
