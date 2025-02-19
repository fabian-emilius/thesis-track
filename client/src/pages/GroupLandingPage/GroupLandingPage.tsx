import { Container, Title, Text, Stack, Group, Button } from '@mantine/core';
import { Link, useParams } from 'react-router';
import { useGroupContext } from '../../providers/GroupContext/context';

function GroupLandingPage() {
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
      <Stack gap="xl">
        <Group justify="space-between">
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
        </Group>

        {/* Add group-specific content here */}
        {/* Topics, theses, and other relevant information */}
      </Stack>
    </Container>
  );
}

export default GroupLandingPage;
