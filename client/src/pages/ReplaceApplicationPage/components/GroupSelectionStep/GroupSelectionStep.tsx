import React, { useEffect } from 'react'
import { Stack, Title, SimpleGrid, Alert } from '@mantine/core'
import { useGroups } from '../../../../providers/GroupsProvider/hooks'
import GroupCard from '../../../../components/GroupCard/GroupCard'
import PageLoader from '../../../../components/PageLoader/PageLoader'
import { IconAlertCircle } from '@tabler/icons-react'

interface GroupSelectionStepProps {
  /** Callback function called when a group is selected */
  onComplete: (groupId: string) => void
}

/**
 * First step in the application process where users select a research group
 * Displays available groups in a grid layout with group cards
 * Handles loading states and error scenarios
 */
const GroupSelectionStep: React.FC<GroupSelectionStepProps> = ({ onComplete }) => {
  const { groups, loading, error, fetchGroups } = useGroups()

  useEffect(() => {
    fetchGroups()
  }, [fetchGroups])

  if (loading) return <PageLoader />
  
  if (error) {
    return (
      <Alert icon={<IconAlertCircle size='1rem' />} title='Error' color='red'>
        {error.message}
      </Alert>
    )
  }

  if (!groups.length) {
    return (
      <Alert icon={<IconAlertCircle size='1rem' />} title='No Groups Available' color='yellow'>
        There are currently no research groups available. Please try again later.
      </Alert>
    )
  }

  return (
    <Stack spacing='xl'>
      <Title order={2}>Select Research Group</Title>
      <SimpleGrid
        cols={3}
        spacing='lg'
        breakpoints={[
          { maxWidth: 'md', cols: 2, spacing: 'md' },
          { maxWidth: 'sm', cols: 1, spacing: 'sm' },
        ]}
      >
        {groups.map((group) => (
          <div
            key={group.id}
            onClick={() => onComplete(group.id)}
            style={{ cursor: 'pointer' }}
            role='button'
            tabIndex={0}
            onKeyPress={(e) => e.key === 'Enter' && onComplete(group.id)}
          >
            <GroupCard group={group} />
          </div>
        ))}
      </SimpleGrid>
    </Stack>
  )
}

export default GroupSelectionStep
