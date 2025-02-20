import { useContext } from 'react';
import { GroupContext } from './context';
import { Group, GroupRole } from '../../types/group';

export const useGroup = () => {
  const context = useContext(GroupContext);
  if (!context) {
    throw new Error('useGroup must be used within a GroupProvider');
  }
  return context;
};

export const useCurrentGroup = (): Group | undefined => {
  const { currentGroup } = useGroup();
  return currentGroup;
};

export const useGroupRole = (groupId: string): GroupRole[] => {
  const { userRoles } = useGroup();
  return userRoles[groupId] || [];
};

export const useIsGroupAdmin = (groupId: string): boolean => {
  const { isGroupAdmin } = useGroup();
  return isGroupAdmin(groupId);
};
