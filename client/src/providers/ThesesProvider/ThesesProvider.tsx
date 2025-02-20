import React, { PropsWithChildren, useEffect, useMemo, useState } from 'react'
import { ThesesContext, IThesesContext, IThesesFilters, IThesesSort } from './context'
import { IThesis, ThesisState } from '../../requests/responses/thesis'
import { doRequest } from '../../requests/request'
import { PaginationResponse } from '../../requests/responses/pagination'
import { useDebouncedValue } from '@mantine/hooks'
import { showSimpleError } from '../../utils/notification'
import { getApiResponseErrorMessage } from '../../requests/handler'
import { useGroupContext } from '../GroupContext/hooks'

interface IThesesProviderProps {
  fetchAll?: boolean
  limit: number
  defaultStates?: ThesisState[]
  hideIfEmpty?: boolean
}

const ThesesProvider = (props: PropsWithChildren<IThesesProviderProps>) => {
  const { children, fetchAll = false, limit, hideIfEmpty = false, defaultStates } = props
  const { currentGroup } = useGroupContext()

  const [theses, setTheses] = useState<PaginationResponse<IThesis>>()
  const [page, setPage] = useState(0)

  const [filters, setFilters] = useState<IThesesFilters>({
    states: defaultStates,
    groupId: currentGroup?.id,
  })
  const [sort, setSort] = useState<IThesesSort>({
    column: 'startDate',
    direction: 'asc',
  })

  const [debouncedSearch] = useDebouncedValue(filters.search || '', 500)

  useEffect(() => {
    setTheses(undefined)

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
          showSimpleError(getApiResponseErrorMessage(res))

          return setTheses({
            content: [],
            totalPages: 0,
            totalElements: 0,
            last: true,
            pageNumber: 0,
            pageSize: limit,
          })
        }

        setTheses(res.data)
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

  useEffect(() => {
    setFilters(prev => ({ ...prev, groupId: currentGroup?.id }))
  }, [currentGroup])

  const contextState = useMemo<IThesesContext>(() => {
    return {
      theses,
      filters,
      setFilters: (value) => {
        setPage(0)
        setFilters(prev => ({ ...value, groupId: currentGroup?.id }))
      },
      sort,
      setSort: (value) => {
        setPage(0)
        setSort(value)
      },
      page,
      setPage,
      limit,
      updateThesis: (newThesis) => {
        setTheses((prev) => {
          if (!prev) {
            return undefined
          }

          const index = prev.content.findIndex((x) => x.thesisId === newThesis.thesisId)

          if (index >= 0) {
            prev.content[index] = newThesis
          }

          return { ...prev }
        })
      },
    }
  }, [theses, filters, sort, page, limit, filters.groupId])

  if (hideIfEmpty && page === 0 && (!theses || theses.content.length === 0)) {
    return <></>
  }

  return <ThesesContext.Provider value={contextState}>{children}</ThesesContext.Provider>
}

export default ThesesProvider
