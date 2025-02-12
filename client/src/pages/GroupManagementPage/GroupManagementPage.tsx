import { Container, Title, Button, Group as MantineGroup } from '@mantine/core'
import { Link } from 'react-router-dom'
import { useGroupContext } from '../../providers/GroupContext/context'

export function GroupManagementPage() {
  const { currentGroup, isGroupAdmin } = useGroupContext()

  if (!currentGroup || !isGroupAdmin(currentGroup.id)) {
    return <div>Access denied</div>
  }

  return (
    <Container size='xl'>
      <MantineGroup justify='space-between' align='center' mb='xl'>
        <Title order={2}>Group Management</Title>
        <Button component={Link} to={`/groups/${currentGroup.slug}/edit`} variant='light'>
          Edit Group
        </Button>
      </MantineGroup>

      {/* Add group management sections here */}
    </Container>
  )
}
