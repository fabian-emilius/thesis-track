import { createContext, useContext } from 'react';
import { GroupContextType } from '../../types/group';

export const GroupContext = createContext<GroupContextType | null>(null);

export const useGroupContext = () => {
  const context = useContext(GroupContext);
  if (!context) {
    throw new Error('useGroupContext must be used within a GroupProvider');
  }
  return context;
};
