import React from 'react'
import { Select } from '@mantine/core'
import { useGroups } from '../../providers/GroupsProvider/hooks'

interface GroupSelectProps {
  onChange?: (groupId: string | null) => void
}

const GroupSelect: React.FC<GroupSelectProps> = ({ onChange }) => {
  const { groups, selectedGroup, setSelectedGroup } = useGroups()

  const handleChange = (value: string | null) => {
    const group = groups.find((g) => g.id === value) || null
    setSelectedGroup(group)
    onChange?.(value)
  }

  return (
    <Select
      label="Select Group"
      placeholder="Choose a group"
      data={groups.map((group) => ({ value: group.id, label: group.name }))}
      value={selectedGroup?.id || null}
      onChange={handleChange}
      clearable
    />
  )
}

export default GroupSelect