import React from 'react'
import { Select, Loader, Tooltip } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'

/**
 * Props for the GroupSelector component
 * @interface GroupSelectorProps
 * @property {boolean} [compact] - When true, displays a compact version of the selector
 */
interface GroupSelectorProps {
  compact?: boolean
}

/**
 * A component that allows users to select and switch between groups
 * @component
 * @param {GroupSelectorProps} props - Component props
 * @returns {JSX.Element} Rendered GroupSelector component
 */
const GroupSelector: React.FC<GroupSelectorProps> = ({ compact = false }): JSX.Element => {
  const { currentGroup, setCurrentGroup, groups, isLoading } = useGroupContext()

  if (!groups) {
    throw new Error('Groups data is required but not available')
  }

  const selectComponent = (
    <Select
      label={compact ? undefined : "Current Group"}
      placeholder={compact ? "Group" : "Select a group"}
      data={groups.map((group) => ({
        value: group.id,
        label: compact ? group.name.charAt(0) : group.name,
      }))}
      value={currentGroup?.id}
      onChange={(value: string | null) => {
        if (!value) return
        const selected = groups.find((g) => g.id === value)
        if (selected) {
          setCurrentGroup(selected)
        } else {
          console.error(`Group with id ${value} not found`)
        }
      }}
      disabled={isLoading}
      size={compact ? "xs" : "sm"}
      w={compact ? 60 : "100%"}
      rightSection={isLoading ? <Loader size="xs" /> : null}
    />
  )

  try {
    return compact ? (
      <Tooltip label={currentGroup?.name || "Select Group"} position="right">
        {selectComponent}
      </Tooltip>
    ) : (
      selectComponent
    )
  } catch (error) {
    console.error('Error rendering GroupSelector:', error)
    return <div>Error loading group selector</div>
  }
}

export default GroupSelector
