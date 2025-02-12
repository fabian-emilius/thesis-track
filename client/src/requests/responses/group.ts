import { Group, GroupMember, GroupRole } from '../types/group';

export interface GroupResponse extends Group {}

export interface GroupMemberResponse extends GroupMember {}

export interface CreateGroupRequest {
  name: string;
  description: string;
  link?: string;
}

export interface UpdateGroupRequest extends CreateGroupRequest {
  mailFooter?: string;
  acceptanceText?: string;
}

export interface AddGroupMemberRequest {
  userId: string;
  role: GroupRole;
}
