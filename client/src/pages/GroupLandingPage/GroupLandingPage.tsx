import { Container, Title, Text, Stack, Group, Button } from '@mantine/core';
import { Link, useParams } from 'react-router-dom';
import { useEffect } from 'react';
import { useGroupContext } from '../../providers/GroupContext/context';
import { TopicsTable } from '../../components/TopicsTable/TopicsTable';

export function GroupLandingPage() {
  const { groupSlug } = useParams();
  const { currentGroup, setCurrentGroup, availableGroups, isGroupAdmin } = useGroupContext();

  useEffect(() => {
    if (groupSlug && availableGroups.length > 0) {
      const group = availableGroups.find((g) => g.slug === groupSlug);
      setCurrentGroup(group);
    }
  }, [groupSlug, availableGroups, setCurrentGroup]);

  if (!currentGroup) {
    return <Text>Group not found</Text>;
  }

  return (
    <Container size="xl" py="xl">
      <Stack spacing="xl">
        <Group position="apart">
          <div>
            <Title>{currentGroup.name}</Title>
            {currentGroup.description && (
              <Text size="lg" c="dimmed" mt="md">
                {currentGroup.description}
              </Text>
            )}
          </div>
          {isGroupAdmin(currentGroup.id) && (
            <Button
              component={Link}
              to={`/groups/${currentGroup.slug}/settings`}
              variant="light"
            >
              Manage Group
            </Button>
          )}
        </Group>

        <div>
          <Title order={2} mb="md">
            Available Topics
          </Title>
          <TopicsTable groupId={currentGroup.id} />
        </div>
      </Stack>
    </Container>
  );
}
