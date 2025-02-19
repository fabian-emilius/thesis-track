import React, { useEffect } from 'react'
import { Container, Title, Text, Group, Stack, Button, Grid } from '@mantine/core'
import { useParams, Link } from 'react-router-dom'
import { useGroups } from '../../providers/GroupsProvider/hooks'
import TopicsTable from '../../components/TopicsTable/TopicsTable'
import ThesesTable from '../../components/ThesesTable/ThesesTable'
import PageLoader from '../../components/PageLoader/PageLoader'

const GroupLandingPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>()
  const { selectedGroup, loading, error, fetchGroup } = useGroups()

  useEffect(() => {
    if (slug) {
      fetchGroup(slug)
    }
  }, [slug, fetchGroup])

  if (loading) return <PageLoader />
  if (error) return <div>Error: {error.message}</div>
  if (!selectedGroup) return <div>Group not found</div>

  return (
    <Container size='xl' py='xl'>
      <Stack spacing='xl'>
        <Group position='apart'>
          <div>
            <Title order={1}>{selectedGroup.name}</Title>
            <Text size='lg' color='dimmed' mt='xs'>
              {selectedGroup.description}
            </Text>
          </div>
          <Button component={Link} to={`/groups/${slug}/edit`}>
            Edit Group
          </Button>
        </Group>

        <Grid>
          <Grid.Col xs={12}>
            <Title order={2} mb='md'>
              Open Topics
            </Title>
            <TopicsTable groupId={selectedGroup.id} />
          </Grid.Col>

          <Grid.Col xs={12}>
            <Title order={2} mb='md'>
              Published Theses
            </Title>
            <ThesesTable groupId={selectedGroup.id} />
          </Grid.Col>
        </Grid>
      </Stack>
    </Container>
  )
}

export default GroupLandingPage
