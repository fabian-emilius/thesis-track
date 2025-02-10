import React from 'react'
import { Stack } from '@mantine/core'
import { useParams } from 'react-router'
import GroupHeader from '../../components/GroupHeader/GroupHeader'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'

const GroupPage: React.FC = () => {
  const { groupId } = useParams()
  const { groups } = useGroupContext()
  const group = groups.find((g) => g.id === groupId)

  if (!group) {
    return <div>Group not found</div>
  }

  return (
    <Stack>
      <GroupHeader />
      {/* Add group-specific content here */}
    </Stack>
  )
}

export default GroupPage
