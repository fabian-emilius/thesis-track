import React from 'react'
import { IApplication } from '../../requests/responses/application'
import { DataTable, DataTableColumn } from 'mantine-datatable'
import { Badge, Center } from '@mantine/core'
import { formatApplicationState, formatDate, formatThesisType } from '../../utils/format'
import { useApplicationsContext } from '../../providers/ApplicationsProvider/hooks'
import { useGroupContext } from '../../providers/GroupProvider/hooks'
import { IApplicationsSort } from '../../providers/ApplicationsProvider/context'
import { ApplicationStateColor } from '../../config/colors'
import AvatarUser from '../AvatarUser/AvatarUser'
import { Skeleton, ActionIcon, Group } from '@mantine/core'
import { Eye, PencilSimple } from 'phosphor-react'

type ApplicationColumn =
  | 'state'
  | 'user'
  | 'thesis_title'
  | 'thesis_type'
  | 'reviewed_at'
  | 'created_at'
  | string

interface IApplicationsTableProps {
  onApplicationClick?: (application: IApplication) => unknown
  onApplicationView?: (application: IApplication) => unknown
  onApplicationEdit?: (application: IApplication) => unknown
  columns?: ApplicationColumn[]
  extraColumns?: Record<string, DataTableColumn<IApplication>>
  loading?: boolean
}

const ApplicationsTable = (props: IApplicationsTableProps) => {
  const {
    onApplicationClick,
    onApplicationView,
    onApplicationEdit,
    columns = ['state', 'thesis_title', 'thesis_type', 'user', 'created_at', 'actions'],
    extraColumns = {},
    loading,
  } = props

  const { currentGroup, hasGroupPermission } = useGroupContext()

  const { applications, sort, setSort, page, setPage, limit } = useApplicationsContext()

  const columnConfig: Record<ApplicationColumn, DataTableColumn<IApplication>> = {
    state: {
      accessor: 'state',
      title: 'State',
      textAlign: 'center',
      width: 140,
      render: (application) => {
        return (
          <Center>
            <Badge color={ApplicationStateColor[application.state]}>
              {formatApplicationState(application.state)}
            </Badge>
          </Center>
        )
      },
    },
    user: {
      accessor: 'user.firstName',
      title: 'Student',
      width: 180,
      render: (application) => <AvatarUser user={application.user} />,
    },
    thesis_title: {
      accessor: 'thesisTitle',
      title: 'Topic',
      cellsStyle: () => ({ minWidth: 200 }),
      render: (application) => application.thesisTitle,
    },
    thesis_type: {
      accessor: 'thesisType',
      title: 'Thesis Type',
      ellipsis: true,
      width: 150,
      render: (application) => formatThesisType(application.thesisType),
    },
    reviewed_at: {
      accessor: 'reviewedAt',
      title: 'Reviewed At',
      sortable: true,
      width: 150,
      render: (application) => formatDate(application.reviewedAt),
    },
    created_at: {
      accessor: 'createdAt',
      title: 'Created At',
      sortable: true,
      width: 150,
      render: (application) => formatDate(application.createdAt),
    },
    ...extraColumns,
    actions: {
      accessor: 'actions',
      title: 'Actions',
      textAlign: 'center',
      width: 100,
      render: (application) => (
        <Group gap='xs' justify='center'>
          {onApplicationView && (
            <ActionIcon
              size='sm'
              variant='subtle'
              onClick={(e) => {
                e.stopPropagation()
                onApplicationView(application)
              }}
            >
              <Eye size={16} />
            </ActionIcon>
          )}
          {onApplicationEdit && hasGroupPermission('applications.edit') && (
            <ActionIcon
              size='sm'
              variant='subtle'
              onClick={(e) => {
                e.stopPropagation()
                onApplicationEdit(application)
              }}
            >
              <PencilSimple size={16} />
            </ActionIcon>
          )}
        </Group>
      ),
    },
  }

  return (
    <DataTable
      fetching={loading || !applications}
      withTableBorder
      minHeight={200}
      noRecordsText='No applications to show'
      borderRadius='sm'
      verticalSpacing='md'
      striped
      highlightOnHover
      totalRecords={applications?.totalElements ?? 0}
      recordsPerPage={limit}
      page={page + 1}
      onPageChange={(x) => setPage(x - 1)}
      sortStatus={{
        direction: sort.direction,
        columnAccessor: sort.column,
      }}
      onSortStatusChange={(newSort) => {
        setSort({
          column: newSort.columnAccessor as IApplicationsSort['column'],
          direction: newSort.direction,
        })
      }}
      records={applications?.content}
      idAccessor='applicationId'
      columns={columns.map((column) => columnConfig[column])}
      onRowClick={
        onApplicationClick
          ? ({ record: application }) => onApplicationClick(application)
          : undefined
      }
    />
  )
}

export default ApplicationsTable
