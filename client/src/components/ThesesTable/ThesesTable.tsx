import { DataTable, DataTableColumn } from 'mantine-datatable'
import { formatDate, formatThesisType } from '../../utils/format'
import React from 'react'
import { useThesesContext } from '../../providers/ThesesProvider/hooks'
import { IThesesSort } from '../../providers/ThesesProvider/context'
import { useNavigate } from 'react-router'
import { IThesis } from '../../requests/responses/thesis'
import ThesisStateBadge from '../ThesisStateBadge/ThesisStateBadge'
import { Center, Text } from '@mantine/core'
import AvatarUserList from '../AvatarUserList/AvatarUserList'
import { useGroupContext } from '../../providers/GroupContext/context'

type ThesisColumn =
  | 'state'
  | 'supervisors'
  | 'advisors'
  | 'students'
  | 'type'
  | 'title'
  | 'start_date'
  | 'end_date'
  | 'group'
  | string

interface IThesesTableProps {
  columns?: ThesisColumn[]
  extraColumns?: Record<string, DataTableColumn<IThesis>>
  hideGroupColumn?: boolean
}

const ThesesTable = (props: IThesesTableProps) => {
  const {
    columns = ['state', 'title', 'type', 'students', 'advisors', 'start_date', 'end_date', 'group'],
    extraColumns = {},
    hideGroupColumn = false,
  } = props

  const { theses, sort, setSort, page, setPage, limit } = useThesesContext()
  const { currentGroup } = useGroupContext()

  const navigate = useNavigate()

  const onThesisClick = (thesis: IThesis) => {
    navigate(`/theses/${thesis.thesisId}`)
  }

  const columnConfig: Record<ThesisColumn, DataTableColumn<IThesis>> = {
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
    group: {
      accessor: 'group',
      title: 'Research Group',
      width: 180,
      ellipsis: true,
      render: (thesis) => <Text size='sm'>{thesis.group?.name}</Text>,
      hidden: hideGroupColumn || !!currentGroup,
    },
    ...extraColumns,
  }

  const visibleColumns = columns
    .filter((column) => !columnConfig[column].hidden)
    .map((column) => columnConfig[column])

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
      onSortStatusChange={(newSort) => {
        setSort({
          column: newSort.columnAccessor as IThesesSort['column'],
          direction: newSort.direction,
        })
      }}
      records={theses?.content}
      idAccessor='thesisId'
      columns={visibleColumns}
      onRowClick={({ record: thesis }) => onThesisClick(thesis)}
    />
  )
}

export default ThesesTable
