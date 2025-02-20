import React from 'react'
import { SimpleGrid } from '@mantine/core'
import { GroupCard } from '../GroupCard/GroupCard'
import { IGroup } from '../../providers/GroupContext/types'

interface GroupListProps {
  groups: IGroup[]
}

export const GroupList: React.FC<GroupListProps> = ({ groups }) => {
  return (
    <SimpleGrid
      cols={{ base: 1, sm: 2, lg: 3 }}
      spacing="lg"
      verticalSpacing="lg"
    >
      {groups.map((group) => (
        <GroupCard key={group.id} group={group} />
      ))}
    </SimpleGrid>
  )
}
