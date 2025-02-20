import { useContext } from 'react';
import { GroupsContext, GroupsContextType } from './context';

export function useGroupsContext(): GroupsContextType {
  const context = useContext(GroupsContext);
  if (context === undefined) {
    throw new Error('useGroupsContext must be used within a GroupsProvider');
  }
  return context;
}

export function useCurrentGroup() {
  const { currentGroup } = useGroupsContext();
  return currentGroup;
}

export function useGroupAccess(groupId?: string) {
  const { groups } = useGroupsContext();
  // TODO: Implement group access check logic
  return true;
}