export interface Group {
  id: string;
  name: string;
  slug: string;
  description?: string;
  logoUrl?: string;
  websiteUrl?: string;
  settings?: GroupSettings;
  createdAt: string;
  updatedAt: string;
}

export interface GroupSettings {
  emailTemplates?: {
    [key: string]: string;
  };
  customization?: {
    primaryColor?: string;
    secondaryColor?: string;
  };
}

export type GroupRole = 'admin' | 'supervisor' | 'advisor';

export interface GroupMember {
  userId: string;
  groupId: string;
  role: GroupRole;
}

export interface GroupContextType {
  currentGroup?: Group;
  setCurrentGroup: (group: Group | undefined) => void;
  userGroups: GroupMember[];
  availableGroups: Group[];
  hasGroupRole: (groupId: string, role: GroupRole) => boolean;
  isGroupAdmin: (groupId: string) => boolean;
  loadGroups: () => Promise<void>;
  updateGroup: (groupId: string, data: Partial<Group>) => Promise<void>;
  createGroup: (data: Omit<Group, 'id' | 'createdAt' | 'updatedAt'>) => Promise<void>;
}