import React from 'react'
import { Group, Select, TextInput } from '@mantine/core'
import { useTheses } from '../../providers/ThesesProvider/hooks'
import { useGroups } from '../../providers/GroupsProvider/hooks'

const ThesesFilters: React.FC = () => {
  const { filters, setFilters } = useTheses()
  const { groups, selectedGroup } = useGroups()

  return (
    <Group gap='md' grow>
      <TextInput
        placeholder='Search theses...'
        value={filters.search || ''}
        onChange={(e) => setFilters({ ...filters, search: e.target.value })}
      />
      <Select
        label='Type'
        placeholder='All types'
        data={[
          { value: 'BACHELOR', label: 'Bachelor Thesis' },
          { value: 'MASTER', label: 'Master Thesis' },
        ]}
        value={filters.type || null}
        onChange={(value) => setFilters({ ...filters, type: value })}
        clearable
      />
      <Select
        label='State'
        placeholder='All states'
        data={[
          { value: 'PROPOSED', label: 'Proposed' },
          { value: 'IN_PROGRESS', label: 'In Progress' },
          { value: 'COMPLETED', label: 'Completed' },
          { value: 'GRADED', label: 'Graded' },
        ]}
        value={filters.state || null}
        onChange={(value) => setFilters({ ...filters, state: value })}
        clearable
      />
      <Select
        label='Group'
        placeholder='All groups'
        data={groups.map((group) => ({ value: group.id, label: group.name }))}
        value={selectedGroup?.id || null}
        onChange={(value) => setFilters({ ...filters, groupId: value })}
        clearable
      />
    </Group>
  )
}

export default ThesesFilters
