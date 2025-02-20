import { DataTable, DataTableColumn, DataTableSortStatus } from 'mantine-datatable'
import { formatDate, formatThesisType } from '../../utils/format'
import React, { useCallback } from 'react'
import { useThesesContext } from '../../providers/ThesesProvider/hooks'
import { IThesesSort } from '../../providers/ThesesProvider/context'
import { useNavigate } from 'react-router'
import { IThesis } from '../../requests/responses/thesis'
import ThesisStateBadge from '../ThesisStateBadge/ThesisStateBadge'
import { Center, Badge, Tooltip } from '@mantine/core'
import AvatarUserList from '../AvatarUserList/AvatarUserList'
import { useGroupContext } from '../../providers/GroupContext/hooks'
import { IGroup } from '../../providers/GroupContext/types'

type SortableColumns = 'startDate' | 'endDate' | 'createdAt'

type ThesisColumn =
  | 'state'
  | 'supervisors'
  | 'advisors'
  | 'students'
  | 'type'
  | 'title'
  | 'group'
  | 'start_date'
  | 'end_date'

interface IThesesTableProps {
  columns?: ThesisColumn[]
  extraColumns?: Partial<Record<string, DataTableColumn<IThesis>>>
  groupContext?: boolean
}

const ThesesTable = (props: IThesesTableProps) => {
  const {
    columns = ['state', 'title', 'type', 'students', 'advisors', 'group', 'start_date', 'end_date'],
    extraColumns = {},
    groupContext = true,
  } = props

  const { theses, sort, setSort, page, setPage, limit } = useThesesContext()
  const { currentGroup } = useGroupContext()

  const navigate = useNavigate()

  const onThesisClick = useCallback(
    (thesis: IThesis) => {
      const thesisGroup = (thesis.group as IGroup | null) ?? undefined
      const path =
        groupContext && thesisGroup
          ? `/groups/${thesisGroup.slug}/theses/${thesis.thesisId}`
          : `/theses/${thesis.thesisId}`
      navigate(path)
    },
    [groupContext, navigate],
  )

  const columnConfig = React.useMemo<Record<ThesisColumn, DataTableColumn<IThesis>>>(
    () => ({
      group: {
        accessor: 'group',
        title: 'Group',
        width: 150,
        render: (thesis) => {
          const group = (thesis.group as IGroup | null) ?? undefined
          return group ? (
            <Tooltip label={group.description} multiline w={200}>
              <Badge size='sm' variant='light' color='blue'>
                {group.name}
              </Badge>
            </Tooltip>
          ) : null
        },
      },
      state: {
        accessor: 'state',
        title: 'State',
        textAlign: 'center',
        width: 150,
        render: (thesis) => {
          return (
            <Center>
              <ThesisStateBadge state={thesis.state} />
            </Center>
          )
        },
      },
      supervisors: {
        accessor: 'supervisors',
        title: 'Supervisor',
        width: 180,
        render: (thesis) => <AvatarUserList users={thesis.supervisors} />,
      },
      advisors: {
        accessor: 'advisors',
        title: 'Advisor(s)',
        ellipsis: true,
        width: 180,
        render: (thesis) => <AvatarUserList users={thesis.advisors} />,
      },
      students: {
        accessor: 'students',
        title: 'Student(s)',
        ellipsis: true,
        width: 180,
        render: (thesis) => <AvatarUserList users={thesis.students} />,
      },
      type: {
        accessor: 'type',
        title: 'Type',
        ellipsis: true,
        width: 150,
        render: (thesis) => formatThesisType(thesis.type),
      },
      title: {
        accessor: 'title',
        title: 'Title',
        cellsStyle: () => ({ minWidth: 200 }),
      },
      start_date: {
        accessor: 'startDate',
        title: 'Start Date',
        sortable: true,
        ellipsis: true,
        width: 130,
        render: (thesis) => formatDate(thesis.startDate, { withTime: false }),
      },
      end_date: {
        accessor: 'endDate',
        title: 'End Date',
        sortable: true,
        ellipsis: true,
        width: 130,
        render: (thesis) => formatDate(thesis.endDate, { withTime: false }),
      },
      ...extraColumns,
    }),
    [extraColumns],
  )

  return (
    <DataTable
      fetching={!theses}
      withTableBorder
      minHeight={200}
      noRecordsText='No theses to show'
      borderRadius='sm'
      verticalSpacing='md'
      striped
      highlightOnHover
      totalRecords={theses?.totalElements ?? 0}
      recordsPerPage={limit}
      page={page + 1}
      onPageChange={(x) => setPage(x - 1)}
      sortStatus={{
        direction: sort.direction,
        columnAccessor: sort.column,
      }}
      onSortStatusChange={(newSort: DataTableSortStatus) => {
        const column = newSort.columnAccessor as SortableColumns
        if (['startDate', 'endDate', 'createdAt'].includes(column)) {
          setSort({
            column,
            direction: newSort.direction,
          })
        }
      }}
      records={theses?.content}
      idAccessor='thesisId'
      columns={columns.map((column) => columnConfig[column])}
      onRowClick={({ record: thesis }) => onThesisClick(thesis)}
    />
  )
}

export type { IThesesTableProps, ThesisColumn }
export default ThesesTable
