import React from 'react'
import { Stack, Text, SimpleGrid } from '@mantine/core'
import { useGroupContext } from '../../../../providers/GroupContext/hooks'
import { GroupCard } from '../../../../components/GroupCard/GroupCard'
import { PageLoader } from '../../../../components/PageLoader/PageLoader'

interface GroupSelectionStepProps {
  onGroupSelect: (groupId: string) => void
}

export const GroupSelectionStep: React.FC<GroupSelectionStepProps> = ({ onGroupSelect }) => {
  const { groups, isLoading, error } = useGroupContext()

  if (isLoading) {
    return <PageLoader />
  }

  if (error) {
    return <Text c='red'>{error}</Text>
  }

  return (
    <Stack gap='xl'>
      <Text size='lg'>Select a research group to submit your thesis application to:</Text>

      <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing='lg'>
        {groups.map((group) => (
          <div key={group.id} onClick={() => onGroupSelect(group.id)} style={{ cursor: 'pointer' }}>
            <GroupCard group={group} />
          </div>
        ))}
      </SimpleGrid>
    </Stack>
  )
}
