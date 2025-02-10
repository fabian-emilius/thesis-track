import React from 'react'
import { SimpleGrid, Card, Text, Title, Group, Button, Skeleton } from '@mantine/core'
import { useGroupContext } from '../../hooks/useGroupContext'
import { useNavigate } from 'react-router-dom'

const GroupOverview = () => {
  const { groups, loading, setCurrentGroup } = useGroupContext()
  const navigate = useNavigate()

  if (loading) {
    return (
      <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
        {[1, 2, 3].map((i) => (
          <Skeleton key={i} height={200} radius="md" />
        ))}
      </SimpleGrid>
    )
  }

  return (
    <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
      {groups.map((group) => (
        <Card key={group.id} shadow="sm" padding="lg" radius="md" withBorder>
          <Group justify="space-between" mb="xs">
            <Title order={3}>{group.name}</Title>
          </Group>

          <Text size="sm" c="dimmed" mb="xl">
            {group.description}
          </Text>

          <Button
            variant="light"
            fullWidth
            onClick={() => {
              setCurrentGroup(group)
              navigate('/dashboard')
            }}
          >
            Enter Group
          </Button>
        </Card>
      ))}
    </SimpleGrid>
  )
}

export default GroupOverview