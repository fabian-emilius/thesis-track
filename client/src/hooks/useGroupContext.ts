import { useContext } from 'react'
import { GroupContext, GroupContextError } from '../providers/GroupContext/GroupProvider'

/**
 * Custom hook to access group context
 * @throws {GroupContextError} When used outside of GroupProvider
 * @returns Group context value
 */
export const useGroupContext = () => {
  const context = useContext(GroupContext)
  if (!context) {
    throw new GroupContextError('useGroupContext must be used within a GroupProvider')
  }
  return context
}