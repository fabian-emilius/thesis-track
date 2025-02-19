export interface Group {
  id: string
  slug: string
  name: string
  description: string
  logoFilename?: string
  websiteLink?: string
  mailFooter: string
  acceptanceEmailText: string
  acceptanceInstructions: string
  createdAt: string
  updatedAt: string
}

export interface GroupMember {
  userId: string
  groupId: string
  role: 'SUPERVISOR' | 'ADVISOR' | 'GROUP_ADMIN'
  joinedAt: string
}

export interface GroupResponse extends Group {
  members: GroupMember[]
}

export interface GroupsResponse {
  groups: Group[]
  total: number
}
