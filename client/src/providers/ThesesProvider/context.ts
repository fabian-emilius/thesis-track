import { IThesis } from '../../requests/responses/thesis'

/**
 * Interface defining the sorting configuration for theses
 * @property {('startDate' | 'endDate' | null)} column - The column to sort by
 * @property {('asc' | 'desc' | null)} direction - The sort direction
 */
export interface IThesesSort {
  column: 'startDate' | 'endDate' | null
  direction: 'asc' | 'desc' | null
}

/**
 * Interface defining the filter options for theses
 * @property {string} [search] - Optional search term for filtering theses
 * @property {string} [type] - Optional thesis type filter
 * @property {string} [state] - Optional thesis state filter
 * @property {string} [groupId] - Optional group ID filter
 */
export interface IThesesFilters {
  search?: string
  type?: string
  state?: string
  groupId?: string
}

/**
 * Interface defining the context for thesis management
 * @property {{content: IThesis[], totalElements: number} | null} theses - Paginated thesis data
 * @property {boolean} loading - Loading state indicator
 * @property {string | null} error - Error message if any
 * @property {IThesesSort} sort - Current sorting configuration
 * @property {(sort: IThesesSort) => void} setSort - Function to update sorting
 * @property {IThesesFilters} filters - Current filter settings
 * @property {(filters: IThesesFilters) => void} setFilters - Function to update filters
 * @property {number} page - Current page number
 * @property {(page: number) => void} setPage - Function to update page number
 * @property {number} limit - Items per page
 * @property {(limit: number) => void} setLimit - Function to update items per page
 * @property {() => void} clearError - Function to clear error state
 */
export interface IThesesContext {
  theses: {
    content: IThesis[]
    totalElements: number
  } | null
  loading: boolean
  error: string | null
  sort: IThesesSort
  setSort: (sort: IThesesSort) => void
  filters: IThesesFilters
  setFilters: (filters: IThesesFilters) => void
  page: number
  setPage: (page: number) => void
  limit: number
  setLimit: (limit: number) => void
  clearError: () => void
}
