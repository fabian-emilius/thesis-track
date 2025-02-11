import { createContext } from 'react'

export interface Topic {
  id: string
  title: string
  type: 'BACHELOR' | 'MASTER'
  closed: boolean
  groupId: string
  // ... other topic fields
}

export interface TopicsFilters {
  search?: string
  type?: string
  showClosed?: boolean
  groupId?: string
}

export interface TopicsContextType {
  topics: Topic[]
  loading: boolean
  error: Error | null
  filters: TopicsFilters
  setFilters: (filters: TopicsFilters) => void
  fetchTopics: () => Promise<void>
}

export const TopicsContext = createContext<TopicsContextType | null>(null)