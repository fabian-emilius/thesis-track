import { createContext } from 'react'

export interface Topic {
  id: string
  title: string
  type: 'BACHELOR' | 'MASTER'
  closed: boolean
  groupId: string
}

export interface TopicsFilters {
  search?: string
  type?: string
  showClosed?: boolean
  groupId?: string
}

export interface TopicsPagination {
  content: Topic[]
  totalPages: number
  totalElements: number
}

export interface TopicsContextType {
  topics: TopicsPagination | null
  loading: boolean
  error: Error | null
  filters: TopicsFilters
  setFilters: (filters: TopicsFilters) => void
  fetchTopics: () => Promise<void>
  page: number
  setPage: (page: number) => void
  limit: number
  addTopic: (topic: Omit<Topic, 'id'>) => Promise<void>
  updateTopic: (id: string, topic: Partial<Topic>) => Promise<void>
}

export const TopicsContext = createContext<TopicsContextType | null>(null)
