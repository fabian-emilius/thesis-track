import { Container, SimpleGrid, Title, Button, Group } from '@mantine/core';
import { Link } from 'react-router-dom';
import { useGroupContext } from '../../providers/GroupContext/context';
import { GroupCard } from '../../components/GroupCard/GroupCard';
import { useHasGroupAccess } from '../../hooks/authentication';

export function GroupsOverviewPage() {
  const { availableGroups } = useGroupContext();
  const isAdmin = useHasGroupAccess('admin');

  return (
    <Container size="xl" py="xl">
      <Group justify="space-between" mb="xl">
        <Title>Available Groups</Title>
        {isAdmin && (
          <Button component={Link} to="/admin/groups/new" variant="filled">
            Create New Group
          </Button>
        )}
      </Group>

      <SimpleGrid
        cols={3}
        spacing="lg"
        breakpoints={[
          { maxWidth: 'md', cols: 2, spacing: 'md' },
          { maxWidth: 'sm', cols: 1, spacing: 'sm' },
        ]}
      >
        {availableGroups.map((group) => (
          <GroupCard key={group.id} group={group} />
        ))}
      </SimpleGrid>
    </Container>
  );
}
