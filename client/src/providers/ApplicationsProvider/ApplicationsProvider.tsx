import React, { PropsWithChildren, ReactNode, useEffect, useMemo, useRef, useState } from 'react'
import { doRequest } from '../../requests/request'
import { PaginationResponse } from '../../requests/responses/pagination'
import {
  ApplicationsContext,
  IApplicationsContext,
  IApplicationsFilters,
  IApplicationsSort,
} from './context'
import { useGroupContext } from '../GroupContext/hooks'
import { ApplicationState, IApplication } from '../../requests/responses/application'
import { useDebouncedValue } from '@mantine/hooks'
import { showSimpleError } from '../../utils/notification'
import { getApiResponseErrorMessage } from '../../requests/handler'
import { useAllTopics } from '../../hooks/fetcher'
import { useLoggedInUser } from '../../hooks/authentication'

interface IApplicationsProviderProps {
  fetchAll?: boolean
  limit: number
  defaultStates?: ApplicationState[]
  defaultTopics?: string[]
  showOnlyAssignedTopics?: boolean
  hideIfEmpty?: boolean
  emptyComponent?: ReactNode
}

const ApplicationsProvider = (props: PropsWithChildren<IApplicationsProviderProps>) => {
  const {
    children,
    limit,
    defaultStates,
    defaultTopics,
    showOnlyAssignedTopics,
    fetchAll = false,
    hideIfEmpty = false,
    emptyComponent,
  } = props

  const user = useLoggedInUser()
  const topics = useAllTopics()
  const { currentGroup } = useGroupContext()

  const [applications, setApplications] = useState<PaginationResponse<IApplication>>()
  const [page, setPage] = useState(0)
  const [isLoading, setIsLoading] = useState(false)

  const previousContent = useRef<string[]>([])

  const [filters, setFilters] = useState<IApplicationsFilters>({
    states: defaultStates,
    topics: defaultTopics,
  })
  const [sort, setSort] = useState<IApplicationsSort>({
    column: 'createdAt',
    direction: 'desc',
  })

  const adjustedFilters = useMemo(() => {
    const copiedFilters = { ...filters }

    if (showOnlyAssignedTopics && typeof copiedFilters.topics === 'undefined') {
      copiedFilters.topics = [
        'NO_TOPIC',
        ...(topics ?? [])
          .filter(
            (topic) =>
              topic.supervisors.some((supervisor) => supervisor.userId === user.userId) ||
              topic.advisors.some((advisor) => advisor.userId === user.userId),
          )
          .map((topic) => topic.topicId),
      ]
    }

    return copiedFilters
  }, [filters, topics, user.userId, showOnlyAssignedTopics])

  const [debouncedSearch] = useDebouncedValue(adjustedFilters.search || '', 500)

  useEffect(() => {
    setPage(0)
  }, [sort, adjustedFilters])

  useEffect(() => {
    setApplications(undefined)
    setIsLoading(true)

    if (!topics || !currentGroup?.groupId) {
      setIsLoading(false)
      return
    }

    if (page === 0) {
      previousContent.current = []
    }

    return doRequest<PaginationResponse<IApplication>>(
      `/v2/applications`,
      {
        method: 'GET',
        requiresAuth: true,
        params: {
          fetchAll: fetchAll ? 'true' : 'false',
          previous: previousContent.current.join(','),
          search: debouncedSearch,
          groupId: currentGroup?.groupId ?? '',
          state: adjustedFilters.states?.join(',') ?? '',
          type: adjustedFilters.types?.join(',') ?? '',
          topic:
            adjustedFilters.topics
              ?.map((topicId) =>
                topicId === 'NO_TOPIC' ? '00000000-0000-0000-0000-000000000000' : topicId,
              )
              .join(',') ?? '',
          includeSuggestedTopics: !adjustedFilters.topics?.length
            ? 'true'
            : adjustedFilters.topics.includes('NO_TOPIC')
              ? 'true'
              : 'false',
          limit,
          page,
          sortBy: sort.column,
          sortOrder: sort.direction,
        },
      },
      (res) => {
        if (!res.ok) {
          showSimpleError(getApiResponseErrorMessage(res))

          setIsLoading(false)
          return setApplications({
            content: [],
            totalPages: 0,
            totalElements: 0,
            last: true,
            pageNumber: 0,
            pageSize: limit,
          })
        }

        previousContent.current.push(...res.data.content.map((item) => item.applicationId))
        setApplications(res.data)
        setIsLoading(false)
      },
    )
  }, [
    fetchAll,
    page,
    limit,
    sort,
    adjustedFilters.states?.join(','),
    adjustedFilters.topics?.join(','),
    adjustedFilters.types?.join(','),
    debouncedSearch,
    topics,
    currentGroup?.groupId,
  ])

  const contextState = useMemo<IApplicationsContext>(() => {
    return {
      topics,
      applications,
      isLoading,
      filters: adjustedFilters,
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
      updateApplication: (newApplication) => {
        setApplications((prev) => {
          if (!prev) {
            return undefined
          }

          const newContent = [...prev.content]
          const index = newContent.findIndex(
            (x) => x.applicationId === newApplication.applicationId,
          )

          if (index >= 0) {
            newContent[index] = newApplication
          }

          return { ...prev, content: newContent }
        })
      },
    }
  }, [user.userId, topics, applications, adjustedFilters, sort, page, limit, isLoading])

  if (
    !isLoading &&
    hideIfEmpty &&
    page === 0 &&
    (!applications || applications.content.length === 0)
  ) {
    return <>{emptyComponent}</>
  }

  return (
    <ApplicationsContext.Provider value={contextState}>{children}</ApplicationsContext.Provider>
  )
}

export default ApplicationsProvider
