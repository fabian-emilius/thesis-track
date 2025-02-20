import React from 'react'
import { Container, Title, Text, Stack } from '@mantine/core'
import { GroupList } from '../../components/GroupList/GroupList'
import { useGroupContext } from '../../providers/GroupContext/hooks'
import { PageLoader } from '../../components/PageLoader/PageLoader'

export const GroupsPage: React.FC = () => {
  const { groups, isLoading, error } = useGroupContext()

  if (isLoading) {
    return <PageLoader />
  }

  if (error) {
    return (
      <Container>
        <Text c="red">{error}</Text>
      </Container>
    )
  }

  return (
    <Container size="xl">
      <Stack gap="xl">
        <Title order={1}>Research Groups</Title>
        <GroupList groups={groups} />
      </Stack>
    </Container>
  )
}

export default GroupsPage
