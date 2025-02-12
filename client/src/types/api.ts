import { Group, GroupRole } from './group'

// API Response Types
export interface ApiResponse<T> {
  data: T
  message?: string
}

export interface ApiError {
  message: string
  code: string
  details?: Record<string, string[]>
}

// Group API Types
export interface CreateGroupRequest {
  name: string
  description: string
  link?: string
}

export interface UpdateGroupRequest extends CreateGroupRequest {
  mailFooter?: string
  acceptanceText?: string
}

export interface GroupMemberRequest {
  userId: string
  role: GroupRole
}

export interface GroupResponse extends Group {}

// Validation Types
export interface ValidationError {
  field: string
  message: string
}

// API Utility Types
export type ApiRequestOptions = {
  headers?: Record<string, string>
  params?: Record<string, string | number | boolean>
  signal?: AbortSignal
}
