import React from 'react'
import { Group, Text, Title } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'

/**
 * Displays the header section for a group, showing the group name and description.
 * 
 * @component
 * @example
 * ```tsx
 * <GroupHeader />
 * ```
 * 
 * @remarks
 * This component relies on the GroupContext to access the current group data.
 * If no group is selected (currentGroup is null), the component renders nothing.
 * 
 * @returns {JSX.Element | null} The rendered group header or null if no group is selected
 * 
 * @throws {Error} When used outside of a GroupContext.Provider
 */
const GroupHeader: React.FC = () => {
  const { currentGroup } = useGroupContext()

  if (!currentGroup) return null

  return (
    <Group mb="md">
      <Title order={2}>{currentGroup.name}</Title>
      {currentGroup.description && (
        <Text size="sm" c="dimmed">
          {currentGroup.description}
        </Text>
      )}
    </Group>
  )
}

export default GroupHeader