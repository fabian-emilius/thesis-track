import React from 'react'
import { Container, Title } from '@mantine/core'
import GroupOverview from '../../components/GroupOverview/GroupOverview'

const GroupsPage = () => {
  return (
    <Container size="xl">
      <Title order={1} mb="xl">
        Available Groups
      </Title>
      <GroupOverview />
    </Container>
  )
}

export default GroupsPage