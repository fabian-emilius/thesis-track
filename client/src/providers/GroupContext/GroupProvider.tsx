import React, { useState, useEffect } from 'react'
import { useLocation, useParams } from 'react-router-dom'
import { Group, GroupRole, GroupContextType } from '../../types/group'
import { GroupContext } from './context'
import { useAuthentication } from '../../hooks/authentication'

export const GroupProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentGroup, setCurrentGroup] = useState<Group>()
  const [userGroups, setUserGroups] = useState<Group[]>([])
  const [userRoles, setUserRoles] = useState<{ [groupId: string]: GroupRole[] }>({})
  const [loading, setLoading] = useState(true)
  const { user } = useAuthentication()
  const location = useLocation()
  const { groupSlug } = useParams<{ groupSlug: string }>()

  useEffect(() => {
    const fetchUserGroups = async () => {
      try {
        // TODO: Implement API call to fetch user's groups and roles
        setLoading(false)
      } catch (error) {
        console.error('Failed to fetch user groups:', error)
        setLoading(false)
      }
    }

    if (user) {
      fetchUserGroups()
    }
  }, [user])

  useEffect(() => {
    if (groupSlug && userGroups.length > 0) {
      const group = userGroups.find((g) => g.slug === groupSlug)
      setCurrentGroup(group)
    }
  }, [groupSlug, userGroups])

  const hasGroupRole = (groupId: string, role: GroupRole): boolean => {
    return userRoles[groupId]?.includes(role) || false
  }

  const isGroupAdmin = (groupId: string): boolean => {
    return hasGroupRole(groupId, 'admin')
  }

  const contextValue: GroupContextType = {
    currentGroup,
    userGroups,
    userRoles,
    setCurrentGroup,
    hasGroupRole,
    isGroupAdmin,
    loading,
  }

  return <GroupContext.Provider value={contextValue}>{children}</GroupContext.Provider>
}
