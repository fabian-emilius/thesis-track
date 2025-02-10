import React from 'react'
import { Stack, Title } from '@mantine/core'
import GroupOverview from '../../components/GroupOverview/GroupOverview'

const GroupsPage: React.FC = () => {
  return (
    <Stack>
      <Title>Groups</Title>
      <GroupOverview />
    </Stack>
  )
}

export default GroupsPage
