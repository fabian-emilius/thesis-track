import { Group, GroupDetails, GroupMember, GroupRole } from '../../types/group';

export interface GetGroupsResponse {
  groups: Group[];
  total: number;
}

export interface GetGroupDetailsResponse extends GroupDetails {}

export interface GetGroupMembersResponse {
  members: GroupMember[];
  total: number;
}

export interface CreateGroupResponse extends Group {}

export interface UpdateGroupResponse extends Group {}
