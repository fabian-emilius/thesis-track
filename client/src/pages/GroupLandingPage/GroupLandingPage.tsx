import { Container, Title, Text, Stack, Group as MantineGroup, Button } from '@mantine/core';
import { Link, useParams } from 'react-router-dom';
import { useGroupContext } from '../../providers/GroupContext/context';

export function GroupLandingPage() {
  const { groupSlug } = useParams();
  const { currentGroup, isGroupAdmin } = useGroupContext();

  if (!currentGroup) {
    return (
      <Container size="xl" py="xl">
        <Text>Group not found</Text>
      </Container>
    );
  }

  return (
    <Container size="xl" py="xl">
      <Stack spacing="xl">
        <MantineGroup position="apart">
          <div>
            <Title order={1}>{currentGroup.name}</Title>
            <Text c="dimmed" mt="sm">
              {currentGroup.description}
            </Text>
          </div>

          {isGroupAdmin(currentGroup.id) && (
            <Button
              component={Link}
              to={`/groups/${groupSlug}/settings`}
              variant="light"
            >
              Manage Group
            </Button>
          )}
        </MantineGroup>

        {/* Add group-specific content here */}
        {/* Topics, theses, and other relevant information */}
      </Stack>
    </Container>
  );
}
