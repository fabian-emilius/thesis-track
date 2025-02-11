import { createContext } from 'react'

export interface Thesis {
  id: string
  title: string
  type: 'BACHELOR' | 'MASTER'
  state: 'PROPOSED' | 'IN_PROGRESS' | 'COMPLETED' | 'GRADED'
  groupId: string
  thesisId: string
  startDate?: string
  endDate?: string
  students: any[]
  advisors: any[]
  supervisors: any[]
  presentations: any[]
  states: { state: ThesisState; startedAt: string; endedAt: string }[]
  keywords: string[]
}

export interface ThesesFilters {
  search?: string
  type?: string
  state?: string
  groupId?: string
}

export interface IThesesSort {
  column: 'startDate' | 'endDate' | null
  direction: 'asc' | 'desc' | null
}

export enum ThesisState {
  PROPOSAL = 'PROPOSAL',
  WRITING = 'WRITING',
  SUBMITTED = 'SUBMITTED'
}

export interface ThesesPagination {
  content: Thesis[]
  totalPages: number
  totalElements: number
}

export interface ThesesContextType {
  theses: ThesesPagination | null
  loading: boolean
  error: Error | null
  filters: ThesesFilters
  setFilters: (filters: ThesesFilters) => void
  fetchTheses: () => Promise<void>
  page: number
  setPage: (page: number) => void
  sort: IThesesSort
  setSort: (sort: IThesesSort) => void
  limit: number
}

export const ThesesContext = createContext<ThesesContextType | null>(null)
