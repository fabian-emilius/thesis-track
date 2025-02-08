import { GroupRole } from '../../types/group';

export interface CreateGroupPayload {
  name: string;
  description: string;
}

export interface UpdateGroupPayload {
  name?: string;
  description?: string;
}

export interface AddGroupMemberPayload {
  userId: string;
  role: GroupRole;
}
