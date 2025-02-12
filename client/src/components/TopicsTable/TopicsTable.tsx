import { DataTable, DataTableColumn } from 'mantine-datatable'
import { formatDate, formatThesisType } from '../../utils/format'
import { useTopicsContext } from '../../providers/TopicsProvider/hooks'
import { useGroupContext } from '../../providers/GroupProvider/hooks'
import { ITopic } from '../../requests/responses/topic'
import { useNavigate } from 'react-router'
import { Badge, Center, Stack, Text, ActionIcon, Group } from '@mantine/core'
import { IconEdit, IconTrash } from '@tabler/icons-react'
import { usePermissions } from '../../hooks/usePermissions'
import AvatarUserList from '../AvatarUserList/AvatarUserList'
import React from 'react'

type TopicColumn = 'title' | 'types' | 'advisor' | 'supervisor' | 'state' | 'createdAt' | string

interface ITopicsTableProps {
  columns?: TopicColumn[]
  extraColumns?: Record<string, DataTableColumn<ITopic>>
  noBorder?: boolean
  onEdit?: (topic: ITopic) => void
  onDelete?: (topic: ITopic) => void
}

const TopicsTable = (props: ITopicsTableProps) => {
  const {
    extraColumns = {},
    columns = ['title', 'types', 'supervisor', 'advisor'],
    noBorder = false,
  } = props

  const navigate = useNavigate()

  const { topics, page, setPage, limit, isLoading } = useTopicsContext()
  const { currentGroup } = useGroupContext()
  const { canEditTopics, canDeleteTopics } = usePermissions()

  const columnConfig: Record<TopicColumn, DataTableColumn<ITopic>> = {
    actions: {
      accessor: 'actions',
      title: 'Actions',
      width: 100,
      render: (topic) => (
        <Group gap="xs" justify="center">
          {canEditTopics && props.onEdit && (
            <ActionIcon size="sm" variant="subtle" onClick={(e) => {
              e.stopPropagation();
              props.onEdit?.(topic);
            }}>
              <IconEdit size={16} />
            </ActionIcon>
          )}
          {canDeleteTopics && props.onDelete && (
            <ActionIcon size="sm" variant="subtle" color="red" onClick={(e) => {
              e.stopPropagation();
              props.onDelete?.(topic);
            }}>
              <IconTrash size={16} />
            </ActionIcon>
          )}
        </Group>
      ),
    },
    state: {
      accessor: 'state',
      title: 'State',
      textAlign: 'center',
      width: 100,
      render: (topic) => (
        <Center>
          {topic.closedAt ? <Badge color='red'>Closed</Badge> : <Badge color='gray'>Open</Badge>}
        </Center>
      ),
    },
    title: {
      accessor: 'title',
      title: 'Title',
      cellsStyle: () => ({ minWidth: 200 }),
    },
    types: {
      accessor: 'thesisTypes',
      title: 'Thesis Types',
      width: 150,
      ellipsis: true,
      render: (topic) => (
        <Stack gap={2}>
          {topic.thesisTypes ? (
            topic.thesisTypes.map((type) => (
              <Text key={type} size='sm'>
                {formatThesisType(type)}
              </Text>
            ))
          ) : (
            <Text size='sm'>Any</Text>
          )}
        </Stack>
      ),
    },
    supervisor: {
      accessor: 'supervisor',
      title: 'Supervisor',
      width: 180,
      ellipsis: true,
      render: (topic) => <AvatarUserList users={topic.supervisors} />,
    },
    advisor: {
      accessor: 'advisor',
      title: 'Advisor(s)',
      width: 180,
      ellipsis: true,
      render: (topic) => <AvatarUserList users={topic.advisors} />,
    },
    createdAt: {
      accessor: 'createdAt',
      title: 'Created At',
      width: 150,
      ellipsis: true,
      render: (record) => formatDate(record.createdAt),
    },
    ...extraColumns,
  }

  return (
    <DataTable
      fetching={isLoading}
      withTableBorder={!noBorder}
      minHeight={200}
      noRecordsText='No topics to show'
      borderRadius='sm'
      verticalSpacing='md'
      striped
      highlightOnHover
      totalRecords={topics?.totalElements ?? 0}
      recordsPerPage={limit}
      page={page + 1}
      onPageChange={(x) => setPage(x - 1)}
      records={topics?.content}
      idAccessor='topicId'
      columns={[
        ...columns.map((column) => columnConfig[column]),
        ...(props.onEdit || props.onDelete ? [columnConfig.actions] : [])
      ]}
      onRowClick={({ record }) => navigate(`/topics/${record.topicId}`)}
    />
  )
}

export default TopicsTable
