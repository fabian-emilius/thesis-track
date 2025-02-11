import { createContext } from 'react'

export interface Thesis {
  id: string
  title: string
  type: 'BACHELOR' | 'MASTER'
  state: 'PROPOSED' | 'IN_PROGRESS' | 'COMPLETED' | 'GRADED'
  groupId: string
  // ... other thesis fields
}

export interface ThesesFilters {
  search?: string
  type?: string
  state?: string
  groupId?: string
}

export interface ThesesContextType {
  theses: Thesis[]
  loading: boolean
  error: Error | null
  filters: ThesesFilters
  setFilters: (filters: ThesesFilters) => void
  fetchTheses: () => Promise<void>
}

export const ThesesContext = createContext<ThesesContextType | null>(null)