import React from 'react'
import { SimpleGrid, Card, Text, Title, Group, Button, Center, Stack, Skeleton } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'
import { useNavigate } from 'react-router'

/**
 * Page component for displaying and selecting available groups
 * Shows a grid of group cards with selection functionality
 */
const GroupsPage = () => {
  const { groups, setCurrentGroup, loading, error } = useGroupContext()
  const navigate = useNavigate()

  const handleGroupSelect = (group: any) => {
    setCurrentGroup(group)
    navigate('/dashboard')
  }

  if (loading) {
    return (
      <>
        <Title mb="xl">Available Groups</Title>
        <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
          {[1, 2, 3].map((i) => (
            <Card key={i} padding="lg" radius="md" withBorder>
              <Skeleton height={24} width="70%" mb="xs" />
              <Skeleton height={40} mb="md" />
              <Skeleton height={36} />
            </Card>
          ))}
        </SimpleGrid>
      </>
    )
  }

  if (error) {
    return (
      <Center>
        <Stack align="center">
          <Title order={2} c="red">
            Error Loading Groups
          </Title>
          <Text>{error}</Text>
          <Button onClick={() => window.location.reload()}>Retry</Button>
        </Stack>
      </Center>
    )
  }

  if (groups.length === 0) {
    return (
      <Center>
        <Stack align="center">
          <Title order={2}>No Groups Available</Title>
          <Text>Please contact your administrator to get access to a group.</Text>
        </Stack>
      </Center>
    )
  }

  return (
    <>
      <Title mb="xl">Available Groups</Title>
      <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
        {groups.map((group) => (
          <Card
            key={group.id}
            padding="lg"
            radius="md"
            withBorder
            role="button"
            tabIndex={0}
            onClick={() => handleGroupSelect(group)}
            onKeyPress={(e) => e.key === 'Enter' && handleGroupSelect(group)}
          >
            <Group justify="space-between" mb="xs">
              <Title order={3}>{group.name}</Title>
            </Group>
            <Text mb="md" c="dimmed" size="sm">
              {group.description}
            </Text>
            <Button onClick={() => handleGroupSelect(group)} fullWidth>
              Select Group
            </Button>
          </Card>
        ))}
      </SimpleGrid>
    </>
  )
}

export default GroupsPage
