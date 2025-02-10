import React from 'react'
import { Group, Text, Title, Skeleton } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'

/**
 * Component that displays information about the currently selected group
 * Shows a loading state while group data is being fetched
 */
const GroupHeader = () => {
  const { currentGroup, loading } = useGroupContext()

  if (loading) {
    return (
      <Group mb="md">
        <Skeleton height={32} width={200} />
        <Skeleton height={20} width={300} />
      </Group>
    )
  }

  if (!currentGroup) return null

  return (
    <Group mb="md">
      <Title order={2}>{currentGroup.name}</Title>
      <Text c="dimmed" aria-label="Group description">
        {currentGroup.description}
      </Text>
    </Group>
  )
}

export default GroupHeader
