import { useContext } from 'react'
import { GroupsContext, GroupsContextType } from './context'

export const useGroups = (): GroupsContextType => {
  const context = useContext(GroupsContext)
  if (!context) {
    throw new Error('useGroups must be used within a GroupsProvider')
  }
  return context
}
