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
  emailTemplates?: Record<string, string>;
  customization?: {
    primaryColor?: string;
    secondaryColor?: string;
  };
}

export interface GroupRole {
  groupId: string;
  role: 'admin' | 'supervisor' | 'advisor';
}

export interface GroupContextType {
  currentGroup?: Group;
  userGroups: Group[];
  userGroupRoles: GroupRole[];
  setCurrentGroup: (group: Group) => void;
  hasGroupRole: (groupId: string, role: string) => boolean;
  isGroupAdmin: (groupId: string) => boolean;
  isGroupSupervisor: (groupId: string) => boolean;
  isGroupAdvisor: (groupId: string) => boolean;
}
