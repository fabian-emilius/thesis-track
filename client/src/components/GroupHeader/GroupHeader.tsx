import React from 'react'
import { Group, Text, Button, Stack, Badge, Paper } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'
import { Users } from 'phosphor-react'

const GroupHeader: React.FC = () => {
  const { currentGroup, changeGroup } = useGroupContext()

  if (!currentGroup) {
    return null
  }

  return (
    <Paper withBorder p="md" mb="md">
      <Group justify="space-between" align="flex-start">
        <Stack gap="xs">
          <Group gap="xs">
            <Text fw={700} size="xl">
              {currentGroup.name}
            </Text>
            <Badge size="lg" variant="light">
              <Group gap="xs">
                <Users size={14} />
                <Text>{currentGroup.memberCount || 0}</Text>
              </Group>
            </Badge>
          </Group>
          {currentGroup.description && (
            <Text size="sm" c="dimmed" maw={600}>
              {currentGroup.description}
            </Text>
          )}
        </Stack>
        <Button variant="light" size="sm" onClick={changeGroup}>
          Change Group
        </Button>
      </Group>
    </Paper>
  )
}

export default GroupHeader
