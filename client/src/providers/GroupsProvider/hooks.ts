import { useContext } from 'react'
import GroupsContext from './context'

export const useGroups = () => {
  const context = useContext(GroupsContext)
  if (context === undefined) {
    throw new Error('useGroups must be used within a GroupsProvider')
  }
  return context
}
