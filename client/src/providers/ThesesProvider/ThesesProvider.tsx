import React, { PropsWithChildren, useEffect, useMemo, useState } from 'react'
import { ThesesContext, IThesesContext, IThesesFilters, IThesesSort } from './context'
import { IThesis, ThesisState } from '../../types/thesis'
import { doRequest, RequestError } from '../../utils/request'
import { PaginationResponse } from '../../types/pagination'
import { useDebouncedValue } from '@mantine/hooks'
import { showSimpleError } from '../../utils/notification'
import { getApiResponseErrorMessage } from '../../utils/apiHandler'
import { useGroupContext } from '../group/GroupProvider'

interface IThesesProviderProps {
  fetchAll?: boolean
  limit: number
  defaultStates?: readonly ThesisState[]
  hideIfEmpty?: boolean
  onError?: (error: Error) => void
}

interface IThesesState {
  theses?: PaginationResponse<IThesis>
  error?: RequestError
  isLoading: boolean
}

const ThesesProvider = (props: PropsWithChildren<IThesesProviderProps>) => {
  const { children, fetchAll = false, limit, hideIfEmpty = false, defaultStates } = props
  const { currentGroup } = useGroupContext()

  const [state, setState] = useState<IThesesState>({
    theses: undefined,
    error: undefined,
    isLoading: false
  })
  const [page, setPage] = useState<number>(0)

  const [filters, setFilters] = useState<IThesesFilters>({
    states: defaultStates,
    groupId: currentGroup?.groupId,
  })
  const [sort, setSort] = useState<IThesesSort>({
    column: 'startDate',
    direction: 'asc',
  })

  const [debouncedSearch] = useDebouncedValue(filters.search || '', 500)

  useEffect(() => {
    setFilters((prev) => ({
      ...prev,
      groupId: currentGroup?.groupId,
    }))
  }, [currentGroup])

  useEffect(() => {
    setState(prev => ({ ...prev, isLoading: true, error: undefined }))

    return doRequest<PaginationResponse<IThesis>>(
      `/v2/theses`,
      {
        method: 'GET',
        requiresAuth: true,
        params: {
          fetchAll: fetchAll ? 'true' : 'false',
          search: debouncedSearch,
          state: filters.states?.join(',') ?? '',
          type: filters.types?.join(',') ?? '',
          groupId: filters.groupId ?? '',
          page,
          limit,
          sortBy: sort.column,
          sortOrder: sort.direction,
        },
      },
      (res) => {
        if (!res.ok) {
          const error = new RequestError(getApiResponseErrorMessage(res))
          setState(prev => ({
            ...prev,
            error,
            isLoading: false,
            theses: {
              content: [],
              totalPages: 0,
              totalElements: 0,
              last: true,
              pageNumber: 0,
              pageSize: limit,
            }
          }))
          if (props.onError) {
            props.onError(error)
          }
          return
        }

        setState({
          theses: res.data,
          error: undefined,
          isLoading: false
        })
      },
    )
  }, [
    fetchAll,
    page,
    limit,
    sort,
    filters.states?.join(','),
    filters.types?.join(','),
    filters.groupId,
    debouncedSearch,
  ])

  const contextState = useMemo<IThesesContext>(() => ({
    theses: state.theses,
    isLoading: state.isLoading,
    error: state.error,
    filters,
    setFilters: (value) => {
      setPage(0)
      setFilters(value)
    },
    sort,
    setSort: (value) => {
      setPage(0)
      setSort(value)
    },
    page,
    setPage,
    limit,
    updateThesis: (newThesis: IThesis) => {
      setState((prev) => {
        if (!prev.theses) {
          return prev
        }

        const index = prev.theses.content.findIndex((x) => x.thesisId === newThesis.thesisId)

        if (index >= 0) {
          const updatedContent = [...prev.theses.content]
          updatedContent[index] = newThesis
          return {
            ...prev,
            theses: {
              ...prev.theses,
              content: updatedContent
            }
          }
        }
        return prev
      })
    },
  }), [state, filters, sort, page, limit])

  if (hideIfEmpty && page === 0 && (!state.theses || state.theses.content.length === 0)) {
    return null
  }

  return (
    <ThesesContext.Provider value={contextState}>
      {children}
    </ThesesContext.Provider>
  )
}

export default ThesesProvider