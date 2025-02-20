export interface Group {
  id: string
  name: string
  slug: string
  description?: string
  logoUrl?: string
  websiteUrl?: string
  settings?: GroupSettings
  createdAt: string
  updatedAt: string
}

export interface GroupSettings {
  emailTemplates?: {
    [key: string]: string
  }
  customization?: {
    primaryColor?: string
    secondaryColor?: string
  }
}

export type GroupRole = 'admin' | 'supervisor' | 'advisor'

export interface GroupMember {
  userId: string
  groupId: string
  role: GroupRole
}

export interface GroupContextType {
  currentGroup?: Group
  userGroups: Group[]
  userRoles: { [groupId: string]: GroupRole[] }
  setCurrentGroup: (group?: Group) => void
  hasGroupRole: (groupId: string, role: GroupRole) => boolean
  isGroupAdmin: (groupId: string) => boolean
  loading: boolean
}

export interface ThesisWithGroup {
  id: string
  groupId: string
  title: string
  type: string
  state: string
  student: { name: string }
  advisor: { name: string }
}

export interface TopicWithGroup {
  id: string
  groupId: string
  title: string
  type: string
  status: string
  supervisor: { name: string }
}
