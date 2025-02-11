import React from 'react'
import { Group, Select, Switch, TextInput } from '@mantine/core'
import { useTopics } from '../../providers/TopicsProvider/hooks'
import { useGroups } from '../../providers/GroupsProvider/hooks'

interface TopicsFiltersProps {
  visible?: {
    type?: boolean
    closed?: boolean
    group?: boolean
  }
}

const TopicsFilters: React.FC<TopicsFiltersProps> = ({ visible = {} }) => {
  const { filters, setFilters } = useTopics()
  const { groups, selectedGroup } = useGroups()

  const {
    type = true,
    closed = true,
    group = true,
  } = visible

  return (
    <Group gap="md" grow>
      <TextInput
        placeholder="Search topics..."
        value={filters.search || ''}
        onChange={(e) => setFilters({ ...filters, search: e.target.value })}
      />
      {type && (
        <Select
          label="Type"
          placeholder="All types"
          data={[
            { value: 'BACHELOR', label: 'Bachelor Thesis' },
            { value: 'MASTER', label: 'Master Thesis' },
          ]}
          value={filters.type || null}
          onChange={(value) => setFilters({ ...filters, type: value || undefined })}
          clearable
        />
      )}
      {closed && (
        <Switch
          label="Show closed topics"
          checked={filters.showClosed || false}
          onChange={(e) => setFilters({ ...filters, showClosed: e.currentTarget.checked })}
        />
      )}
      {group && (
        <Select
          label="Group"
          placeholder="All groups"
          data={groups.map((group) => ({ value: group.id, label: group.name }))}
          value={selectedGroup?.id || null}
          onChange={(value) => setFilters({ ...filters, groupId: value || undefined })}
          clearable
        />
      )}
    </Group>
  )
}

export default TopicsFilters