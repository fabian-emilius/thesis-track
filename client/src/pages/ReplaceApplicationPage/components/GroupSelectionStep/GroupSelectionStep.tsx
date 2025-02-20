import React from 'react'
import { Stack, Text, SimpleGrid } from '@mantine/core'
import { useGroup } from '../../../../providers/GroupContext/hooks'
import { Group } from '../../../../types/group'
import { GroupCard } from '../../../../components/GroupCard/GroupCard'

interface GroupSelectionStepProps {
  onComplete: (group: Group) => void
}

export const GroupSelectionStep: React.FC<GroupSelectionStepProps> = ({ onComplete }) => {
  const { userGroups, loading } = useGroup()

  if (loading) {
    return <Text>Loading available groups...</Text>
  }

  if (userGroups.length === 0) {
    return <Text c='dimmed'>No groups are currently available for applications.</Text>
  }

  return (
    <Stack>
      <Text size='sm' c='dimmed'>
        Select a group to apply for a thesis. Each group may have different requirements and
        available topics.
      </Text>

      <SimpleGrid cols={2} spacing='lg' breakpoints={[{ maxWidth: 'sm', cols: 1 }]}>
        {userGroups.map((group) => (
          <div key={group.id} onClick={() => onComplete(group)} style={{ cursor: 'pointer' }}>
            <GroupCard group={group} showActions={false} />
          </div>
        ))}
      </SimpleGrid>
    </Stack>
  )
}
