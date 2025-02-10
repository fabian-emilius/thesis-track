import React from 'react'
import { Select, SelectProps } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'

/**
 * Props for the GroupSelector component, extending Mantine Select props
 * while omitting conflicting props that are managed internally
 */
interface GroupSelectorProps extends Omit<SelectProps, 'data' | 'value' | 'onChange'> {}

/**
 * A dropdown component for selecting groups using Mantine UI Select
 * 
 * @component
 * @example
 * ```tsx
 * <GroupSelector label="Select Group" required />
 * ```
 * 
 * @remarks
 * This component uses the GroupContext to manage group selection state.
 * It automatically handles group data and selection changes through the context.
 */
const GroupSelector = (props: GroupSelectorProps): JSX.Element => {
  const { currentGroup, setCurrentGroup, groups } = useGroupContext()

  return (
    <Select
      {...props}
      value={currentGroup?.id ?? null}
      onChange={(value: string | null) => {
        const group = value ? groups.find((g) => g.id === value) : null
        setCurrentGroup(group)
      }}
      data={groups.map((group) => ({
        value: group.id,
        label: group.name,
      }))}
      placeholder="Select a group"
      clearable
    />
  )
}

export default GroupSelector