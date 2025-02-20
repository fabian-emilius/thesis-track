import { createContext } from 'react';
import { Group, GroupResponse, GroupSettings, GroupMember } from '../../requests/responses/group';

export interface GroupsContextType {
  groups: Group[];
  currentGroup?: Group;
  isLoading: boolean;
  error?: Error;
  fetchGroups: () => Promise<void>;
  fetchGroupDetails: (groupId: string) => Promise<GroupResponse>;
  createGroup: (group: Partial<Group>) => Promise<Group>;
  updateGroup: (groupId: string, group: Partial<Group>) => Promise<Group>;
  updateGroupSettings: (groupId: string, settings: Partial<GroupSettings>) => Promise<GroupSettings>;
  addGroupMember: (groupId: string, userId: string, role: GroupMember['role']) => Promise<void>;
  removeGroupMember: (groupId: string, userId: string) => Promise<void>;
  setCurrentGroup: (group?: Group) => void;
}

export const GroupsContext = createContext<GroupsContextType | undefined>(undefined);