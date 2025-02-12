import { ReactNode, useState, useCallback, useEffect, useMemo } from 'react'
import { Group, GroupMember, GroupContextType, GroupRole } from '@/types/group'
import { GroupContext } from '@/contexts/group/context'
import { useGetGroups, useGetGroupMembers, useUpdateGroup } from '@/hooks/api/groups'
import { notifications } from '@mantine/notifications'
import { ApiError } from '@/types/api'

export interface GroupProviderProps {
  children: ReactNode
}

export function GroupProvider({ children }: GroupProviderProps) {
  export default GroupProvider
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null)
  const [userGroups, setUserGroups] = useState<GroupMember[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<ApiError | null>(null)

  const { data: groups, isLoading: groupsLoading, error: groupsError } = useGetGroups()

  const { data: members, isLoading: membersLoading, error: membersError } = useGetGroupMembers()

  const { mutate: updateGroup, isLoading: updateLoading } = useUpdateGroup()

  const checkRole = useCallback(
    (groupId: string, role: GroupRole): boolean => {
      return userGroups.some(
        (membership) => membership.groupId === groupId && membership.role === role,
      )
    },
    [userGroups],
  )

  const isGroupAdmin = useCallback(
    (groupId: string): boolean => checkRole(groupId, 'GROUP_ADMIN'),
    [checkRole],
  )

  const isSupervisor = useCallback(
    (groupId: string): boolean => checkRole(groupId, 'SUPERVISOR'),
    [checkRole],
  )

  const isAdvisor = useCallback(
    (groupId: string): boolean => checkRole(groupId, 'ADVISOR'),
    [checkRole],
  )

  useEffect(() => {
    let mounted = true

    if (groups && members && mounted) {
      setUserGroups(members)
      setIsLoading(false)
    }

    return () => {
      mounted = false
    }
  }, [groups, members])

  useEffect(() => {
    if (groupsError || membersError) {
      const error = groupsError || membersError
      setError(error)
      notifications.show({
        title: 'Error',
        message: error?.message || 'Failed to load group data',
        color: 'red',
        autoClose: 5000,
      })
    }
  }, [groupsError, membersError])

  const handleGroupUpdate = useCallback(
    async (groupId: string, data: Partial<Group>): Promise<void> => {
      try {
        await updateGroup({ groupId, data })
        notifications.show({
          title: 'Success',
          message: 'Group updated successfully',
          color: 'green',
          autoClose: 3000,
        })
      } catch (err) {
        const error = err as ApiError
        setError(error)
        notifications.show({
          title: 'Error',
          message: error.message || 'Failed to update group',
          color: 'red',
          autoClose: 5000,
        })
        throw error
      }
    },
    [updateGroup, setError],
  )

  const value: GroupContextType = useMemo(
    () => ({
      currentGroup,
      userGroups,
      setCurrentGroup,
      isGroupAdmin,
      isSupervisor,
      isAdvisor,
      isLoading: isLoading || groupsLoading || membersLoading || updateLoading,
      error,
      handleGroupUpdate,
    }),
    [
      currentGroup,
      userGroups,
      setCurrentGroup,
      isGroupAdmin,
      isSupervisor,
      isAdvisor,
      isLoading,
      groupsLoading,
      membersLoading,
      updateLoading,
      error,
      handleGroupUpdate,
    ],
  )

  return <GroupContext.Provider value={value}>{children}</GroupContext.Provider>
}
