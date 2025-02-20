import { type FC } from 'react'
import { useNavigate } from 'react-router'
import { Badge, Center, Group, Stack, Text } from '@mantine/core'
import { DataTable, type DataTableColumn } from 'mantine-datatable'

import { AvatarUserList } from '../AvatarUserList/AvatarUserList'
import { useGroupContext } from '../../providers/GroupContext/hooks'
import { useTopicsContext } from '../../providers/TopicsProvider/hooks'
import { formatDate, formatThesisType } from '../../utils/format'

import { ITopic } from '../../requests/responses/topic'
import { IGroup } from '../../providers/GroupContext/types'

interface ITopicWithGroup extends ITopic {
  group?: IGroup
}

type TopicColumn = keyof typeof defaultColumnConfig

export interface ITopicsTableProps {
  columns?: readonly TopicColumn[]
  extraColumns?: Partial<Record<TopicColumn, DataTableColumn<ITopicWithGroup>>>
  noBorder?: boolean
}

const TopicsTable: React.FC<ITopicsTableProps> = (props) => {
  const {
    extraColumns = {},
    columns = ['title', 'types', 'supervisor', 'advisor'],
    noBorder = false,
  } = props

  const navigate = useNavigate()
  const { currentGroup } = useGroupContext()
  const { topics, page, setPage, limit } = useTopicsContext()

  const defaultColumnConfig: Record<string, DataTableColumn<ITopicWithGroup>> = {
    state: {
      accessor: 'state',
      title: 'State',
      textAlign: 'center',
      width: 100,
      sortable: true,
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
      render: (topic) => (
        <Group gap='xs'>
          <Text>{topic.title}</Text>
          {topic.group && topic.group.id !== currentGroup?.id && (
            <Badge size='sm' variant='light'>
              {topic.group.name}
            </Badge>
          )}
        </Group>
      ),
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
      fetching={!topics}
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
      records={topics?.content as ITopicWithGroup[] | undefined}
      idAccessor='topicId'
      columns={columns.map((column) => ({ ...defaultColumnConfig[column] }))}
      onRowClick={({ record }) => {
        const path = currentGroup
          ? `/groups/${currentGroup.slug}/topics/${record.topicId}`
          : `/topics/${record.topicId}`
        navigate(path)
      }}
    />
  )
}

export type { ITopicsTableProps, TopicColumn }
export { TopicsTable as default }
