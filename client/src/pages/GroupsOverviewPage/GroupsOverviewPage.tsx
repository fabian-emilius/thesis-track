import { Container, SimpleGrid, Title, Text, Stack } from '@mantine/core';
import { useGroupContext } from '../../providers/GroupContext/context';
import { GroupCard } from '../../components/GroupCard/GroupCard';

function GroupsOverviewPage() {
  const { userGroups } = useGroupContext();

  return (
    <Container size="xl" py="xl">
      <Stack gap="lg">
        <div>
          <Title order={1}>Research Groups</Title>
          <Text c="dimmed" mt="sm">
            Browse and join research groups to start your thesis journey
          </Text>
        </div>

        <SimpleGrid
          cols={3}
          spacing="lg"
          breakpoints={[
            { maxWidth: 'md', cols: 2, spacing: 'md' },
            { maxWidth: 'sm', cols: 1, spacing: 'sm' },
          ]}
        >
          {userGroups.map((group) => (
            <GroupCard key={group.id} group={group} />
          ))}
        </SimpleGrid>
      </Stack>
    </Container>
  );
}

export default GroupsOverviewPage;
