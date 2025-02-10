import React, { ErrorBoundary } from 'react'
import { SimpleGrid, Card, Text, Title, Group, Button, Stack, Skeleton, Alert } from '@mantine/core'
import { useNavigate } from 'react-router'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'
import { IconAlertCircle } from '@tabler/icons-react'
import { Group as GroupType } from '../../types/group'

/**
 * GroupOverview Component
 * 
 * Displays a grid of available groups that users can select from.
 * Handles loading states and empty states appropriately.
 * 
 * @component
 */
const GroupOverview: React.FC = () => {
  const { groups, setCurrentGroup, loading } = useGroupContext()
  const navigate = useNavigate()

  const handleGroupSelect = (group: GroupType): void => {
    setCurrentGroup(group)
    navigate(`/groups/${group.id}/topics`)
  }

  /**
   * Render loading skeleton state
   */
  if (loading) {
    return (
      <Stack>
        <Title order={2}>Available Groups</Title>
        <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
          {[1, 2, 3].map((i) => (
            <Card key={i} shadow="sm" padding="lg" radius="md" withBorder>
              <Stack gap="md">
                <Skeleton height={28} width="70%" />
                <Skeleton height={40} />
                <Group justify="flex-end">
                  <Skeleton height={36} width={120} />
                </Group>
              </Stack>
            </Card>
          ))}
        </SimpleGrid>
      </Stack>
    )
  }

  if (!groups?.length) {
    return (
      <Stack>
        <Title order={2}>Available Groups</Title>
        <Alert icon={<IconAlertCircle size="1rem" />} title="No Groups Found" color="gray">
          There are currently no groups available.
        </Alert>
      </Stack>
    )
  }

  return (
    <Stack>
      <Title order={2}>Available Groups</Title>
      <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
        {groups.map((group) => (
          <Card
            key={group.id}
            shadow="sm"
            padding="lg"
            radius="md"
            withBorder
            style={{ display: 'flex', flexDirection: 'column' }}
            role="button"
            aria-label={`Select group ${group.name}`}
          >
            <Stack gap="md" style={{ flex: 1 }}>
              <Title order={3} lineClamp={2}>{group.name}</Title>
              <Text size="sm" c="dimmed" lineClamp={3} style={{ flex: 1 }}>
                {group.description}
              </Text>
              <Group justify="flex-end" mt="auto">
                <Button 
                  onClick={() => handleGroupSelect(group)}
                  variant="light"
                  radius="md"
                >
                  Select Group
                </Button>
              </Group>
            </Stack>
          </Card>
        ))}
      </SimpleGrid>
    </Stack>
  )
}

/**
 * Error boundary wrapper for GroupOverview
 */
const GroupOverviewWithErrorBoundary: React.FC = () => (
  <ErrorBoundary fallback={
    <Alert icon={<IconAlertCircle size="1rem" />} title="Error" color="red">
      An error occurred while loading groups. Please try again later.
    </Alert>
  }>
    <GroupOverview />
  </ErrorBoundary>
)

export default GroupOverviewWithErrorBoundary
