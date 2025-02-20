import { createContext } from 'react';
import { GroupContextType } from '../../types/group';

export const GroupContext = createContext<GroupContextType>({
  userGroups: [],
  userRoles: {},
  setCurrentGroup: () => {},
  hasGroupRole: () => false,
  isGroupAdmin: () => false,
  loading: true,
});
