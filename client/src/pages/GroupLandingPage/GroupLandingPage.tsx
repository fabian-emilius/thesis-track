import React from 'react'
import { Container, Title, Stack, Group, Button } from '@mantine/core'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useCurrentGroup, useIsGroupAdmin } from '../../providers/GroupContext/hooks'
import GroupCard from '../../components/GroupCard/GroupCard'

const GroupLandingPage: React.FC = () => {
  const { groupSlug } = useParams<{ groupSlug: string }>()
  const currentGroup = useCurrentGroup()
  const isAdmin = currentGroup ? useIsGroupAdmin(currentGroup.id) : false

  if (!currentGroup || currentGroup.slug !== groupSlug) {
    return <Navigate to='/groups' replace />
  }

  return (
    <Container size='xl' py='xl'>
      <Stack gap='xl'>
        <Group justify='space-between'>
          <Title order={1}>{currentGroup.name}</Title>
          {isAdmin && (
            <Button component={Link} to={`/groups/${groupSlug}/settings`} variant='light'>
              Group Settings
            </Button>
          )}
        </Group>

        <GroupCard group={currentGroup} showActions={false} />
      </Stack>
    </Container>
  )
}

export default GroupLandingPage
