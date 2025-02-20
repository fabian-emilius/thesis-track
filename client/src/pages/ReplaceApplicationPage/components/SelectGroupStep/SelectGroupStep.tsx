import { SimpleGrid, Text } from '@mantine/core'
import { useGroupContext } from '../../../../providers/GroupContext/context'
import { GroupCard } from '../../../../components/GroupCard/GroupCard'
import { Group } from '../../../../types/group'

interface SelectGroupStepProps {
  onComplete: (group: Group) => void
}

export function SelectGroupStep({ onComplete }: SelectGroupStepProps) {
  const { userGroups } = useGroupContext()

  if (userGroups.length === 0) {
    return (
      <Text c='dimmed' ta='center' mt='xl'>
        No research groups available. Please contact your administrator.
      </Text>
    )
  }

  return (
    <SimpleGrid
      cols={3}
      spacing='lg'
      breakpoints={[
        { maxWidth: 'md', cols: 2, spacing: 'md' },
        { maxWidth: 'sm', cols: 1, spacing: 'sm' },
      ]}
    >
      {userGroups.map((group) => (
        <div key={group.id} style={{ cursor: 'pointer' }} onClick={() => onComplete(group)}>
          <GroupCard group={group} />
        </div>
      ))}
    </SimpleGrid>
  )
}
