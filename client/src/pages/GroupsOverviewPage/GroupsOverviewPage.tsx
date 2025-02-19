import React, { useEffect } from 'react'
import { Container, Grid, Title, Button, Group } from '@mantine/core'
import { Link } from 'react-router-dom'
import { useGroups } from '../../providers/GroupsProvider/hooks'
import GroupCard from '../../components/GroupCard/GroupCard'
import PageLoader from '../../components/PageLoader/PageLoader'

const GroupsOverviewPage: React.FC = () => {
  const { groups, loading, error, fetchGroups } = useGroups()

  useEffect(() => {
    fetchGroups()
  }, [fetchGroups])

  if (loading) return <PageLoader />
  if (error) return <div>Error: {error.message}</div>

  return (
    <Container size='xl' py='xl'>
      <Group justify='space-between' mb='xl'>
        <Title order={1}>Research Groups</Title>
        <Button component={Link} to='/groups/create'>
          Create Group
        </Button>
      </Group>

      <Grid>
        {groups.map((group) => (
          <Grid.Col key={group.id} span={{ base: 12, sm: 6, md: 4, lg: 3 }}>
            <GroupCard group={group} />
          </Grid.Col>
        ))}
      </Grid>
    </Container>
  )
}

export default GroupsOverviewPage
