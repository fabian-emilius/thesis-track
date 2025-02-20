import { createContext, useContext } from 'react'
import { GroupContextType } from '../../types/group'

export const GroupContext = createContext<GroupContextType>({
  userGroups: [],
  userGroupRoles: [],
  setCurrentGroup: () => {},
  hasGroupRole: () => false,
  isGroupAdmin: () => false,
  isGroupSupervisor: () => false,
  isGroupAdvisor: () => false,
})

export const useGroupContext = () => useContext(GroupContext)
