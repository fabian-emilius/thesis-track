import { DataTable, DataTableColumn } from 'mantine-datatable'
import { formatDate, formatThesisType } from '../../utils/format'
import React from 'react'
import { useThesesContext } from '../../providers/ThesesProvider/hooks'
import { IThesesSort } from '../../providers/ThesesProvider/context'
import { useNavigate } from 'react-router'
import { IThesis } from '../../requests/responses/thesis'
import ThesisStateBadge from '../ThesisStateBadge/ThesisStateBadge'
import { ActionIcon, Center, Group, Menu, Tooltip } from '@mantine/core'
import { IconDots, IconEdit, IconTrash } from '@tabler/icons-react'
import AvatarUserList from '../AvatarUserList/AvatarUserList'
import { useGroupPermissions } from '../../hooks/group-permissions'
import { notifications } from '@mantine/notifications'

type ThesisColumn =
  | 'state'
  | 'supervisors'
  | 'advisors'
  | 'students'
  | 'type'
  | 'title'
  | 'start_date'
  | 'end_date'
  | string

interface IThesesTableProps {
  columns?: ThesisColumn[]
  extraColumns?: Record<string, DataTableColumn<IThesis>>
  groupId?: string
}

const ThesesTable = (props: IThesesTableProps) => {
  const {
    columns = ['state', 'title', 'type', 'students', 'advisors', 'start_date', 'end_date'],
    extraColumns = {},
    groupId
  } = props

  const { canManageTopics } = useGroupPermissions(groupId)

  const { theses, sort, setSort, page, setPage, limit, deleteThesis, loading } = useThesesContext()

  const navigate = useNavigate()

  const onThesisClick = (thesis: IThesis) => {
    navigate(`/theses/${thesis.thesisId}`)
  }

  const columnConfig: Record<ThesisColumn, DataTableColumn<IThesis>> = {
    actions: {
      accessor: 'actions',
      title: 'Actions',
      width: 80,
      render: (thesis) => canManageTopics && (
        <Group justify="center">
          <Menu position="bottom-end" withinPortal>
            <Tooltip label="Actions">
              <Menu.Target>
                <ActionIcon variant="subtle" size="sm">
                  <IconDots size={16} />
                </ActionIcon>
              </Menu.Target>
            </Tooltip>
            <Menu.Dropdown>
              <Menu.Item 
                leftSection={<IconEdit size={14} />} 
                onClick={(e) => {
                  e.stopPropagation()
                  navigate(`/theses/${thesis.thesisId}/edit`)
                }}
              >
                Edit
              </Menu.Item>
              <Menu.Item 
                leftSection={<IconTrash size={14} />} 
                color="red" 
                onClick={async (e) => {
                  e.stopPropagation()
                  try {
                    await deleteThesis(thesis.thesisId)
                    notifications.show({
                      title: 'Success',
                      message: 'Thesis deleted successfully',
                      color: 'green'
                    })
                  } catch (error) {
                    notifications.show({
                      title: 'Error',
                      message: 'Failed to delete thesis',
                      color: 'red'
                    })
                  }
                }}
              >
                Delete
              </Menu.Item>
            </Menu.Dropdown>
          </Menu>
        </Group>
      )
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
  }

  return (
    <DataTable
      fetching={loading || !theses}
      withTableBorder
      defaultColumnProps={{ 
        filtering: groupId ? { groupId } : undefined,
        cellsStyle: { cursor: 'pointer' }
      }}
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
      columns={[...columns.map((column) => columnConfig[column]), ...(canManageTopics ? [columnConfig.actions] : [])]
      onRowClick={({ record: thesis }) => onThesisClick(thesis)}
    />
  )
}

export default ThesesTable
