export interface Group {
  id: string;
  slug: string;
  name: string;
  description: string;
  logoUrl: string;
  externalLink: string;
  createdAt: string;
  updatedAt: string;
}

export interface GroupSettings {
  groupId: string;
  acceptanceEmailTemplate: string;
  postAcceptanceInstructions: string;
  emailFooter: string;
}

export interface GroupMember {
  userId: string;
  groupId: string;
  role: 'supervisor' | 'advisor' | 'group_admin';
}

export interface GroupResponse extends Group {
  settings: GroupSettings;
  members: GroupMember[];
}