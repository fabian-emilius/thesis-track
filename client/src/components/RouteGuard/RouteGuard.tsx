import { ReactNode } from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { useGroupContext } from '../../providers/GroupContext/context'
import { useAuthContext } from '../../providers/AuthenticationContext/context'

interface RouteGuardProps {
  children: ReactNode
  requiredGroups?: ('admin' | 'advisor' | 'supervisor' | 'group_admin')[]
  requireGroup?: boolean
}

export function RouteGuard({
  children,
  requiredGroups = [],
  requireGroup = false,
}: RouteGuardProps) {
  const { groupSlug } = useParams()
  const { currentGroup, isGroupAdmin, isSupervisor, isAdvisor } = useGroupContext()
  const { isAdmin } = useAuthContext()

  // If group context is required but not available
  if (requireGroup && !currentGroup) {
    return <Navigate to='/groups' replace />
  }

  // Check if user has required roles
  const hasRequiredRole =
    requiredGroups.length === 0 ||
    requiredGroups.some((role) => {
      switch (role) {
        case 'admin':
          return isAdmin
        case 'group_admin':
          return currentGroup && isGroupAdmin(currentGroup.id)
        case 'supervisor':
          return currentGroup && isSupervisor(currentGroup.id)
        case 'advisor':
          return currentGroup && isAdvisor(currentGroup.id)
        default:
          return false
      }
    })

  if (!hasRequiredRole) {
    return <Navigate to='/' replace />
  }

  return <>{children}</>
}
