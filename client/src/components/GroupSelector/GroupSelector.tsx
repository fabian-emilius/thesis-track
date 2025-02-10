import React from 'react'
import { Select, SelectProps, Loader } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'

/**
 * Props for the GroupSelector component
 * Extends Mantine Select props, excluding 'data' which is handled internally
 */
export interface GroupSelectorProps extends Omit<SelectProps, 'data'> {}

/**
 * Component for selecting the current working group
 * Displays a dropdown of available groups and handles selection
 */
const GroupSelector = (props: GroupSelectorProps) => {
  const { currentGroup, setCurrentGroup, groups, loading, error } = useGroupContext()

  return (
    <Select
      {...props}
      value={currentGroup?.id}
      onChange={(value) => {
        const group = groups.find((g) => g.id === value)
        setCurrentGroup(group || null)
      }}
      data={groups.map((group) => ({
        value: group.id,
        label: group.name,
      }))}
      placeholder="Select a group"
      aria-label="Select working group"
      disabled={loading || !!error}
      rightSection={loading ? <Loader size="xs" /> : null}
      error={error}
      searchable
      nothingFound="No groups found"
    />
  )
}

export default GroupSelector
