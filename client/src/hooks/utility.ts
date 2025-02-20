import { useCurrentGroup } from '../providers/GroupContext/hooks'

/**
 * Hook providing group-based filtering functionality
 * @param items - Array of items containing groupId
 * @returns Filtered array of items belonging to current group
 */
export function useGroupFilter<T extends { groupId: string }>(
  items: T[] | undefined,
): T[] | undefined {
  const currentGroup = useCurrentGroup()

  if (!items || !currentGroup) {
    return undefined
  }

  return items.filter((item) => item.groupId === currentGroup.id)
}

/**
 * Hook for handling group-based access control
 * @param groupId - ID of the group to check permissions for
 * @returns Object containing permission check functions
 */
export function useGroupPermissions(groupId: string | undefined) {
  const currentGroup = useCurrentGroup()

  const hasAccess = (): boolean => {
    if (!groupId || !currentGroup) {
      return false
    }
    return groupId === currentGroup.id
  }

  return {
    hasAccess,
  }
}
