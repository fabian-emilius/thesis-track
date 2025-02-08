import { Container, Title, Text, SimpleGrid, Card, Group, Button, Menu } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { useGroups } from '@/providers/GroupProvider';
import { useGroupPermissions } from '@/hooks/useGroupPermissions';
import { PageLoader } from '@/components/PageLoader';
import { ErrorAlert } from '@/components/ErrorAlert';
import { DotsThree, PencilSimple, Trash } from 'phosphor-react';

export function GroupOverviewPage() {
  const navigate = useNavigate();
  const { groups, loading, error } = useGroups();
  const { canManageGroup, canDeleteGroup } = useGroupPermissions();

  if (loading) {
    return <PageLoader />;
  }

  if (error) {
    return <ErrorAlert message={error} />;
  }

  return (
    <Container size="lg" py="xl">
      <Title order={1} mb="xl">Research Groups</Title>
      <SimpleGrid cols={2} spacing="lg" breakpoints={[{ maxWidth: 'sm', cols: 1 }]}>
        {groups?.map((group) => (
          <Card key={group.id} shadow="sm" padding="lg" radius="md" withBorder>
            <Group position="apart" mb="xs">
              <Title order={3}>{group.name}</Title>
              {(canManageGroup(group.id) || canDeleteGroup(group.id)) && (
                <Menu position="bottom-end" withinPortal>
                  <Menu.Target>
                    <Button variant="subtle" size="sm" p={0}>
                      <DotsThree size={20} weight="bold" />
                    </Button>
                  </Menu.Target>
                  <Menu.Dropdown>
                    {canManageGroup(group.id) && (
                      <Menu.Item leftSection={<PencilSimple size={16} />} onClick={() => navigate(`/groups/${group.id}/edit`)}>
                        Edit Group
                      </Menu.Item>
                    )}
                    {canDeleteGroup(group.id) && (
                      <Menu.Item leftSection={<Trash size={16} />} color="red" onClick={() => navigate(`/groups/${group.id}/delete`)}>
                        Delete Group
                      </Menu.Item>
                    )}
                  </Menu.Dropdown>
                </Menu>
              )}
            </Group>
            <Text mb="md" color="dimmed" size="sm">
              {group.description}
            </Text>
            <Text size="sm" mb="md">
              {group.topicCount} active topics · {group.thesisCount} theses
            </Text>
            <Button
              variant="light"
              fullWidth
              onClick={() => navigate(`/groups/${group.id}`)}
            >
              View Group
            </Button>
          </Card>
        ))}
      </SimpleGrid>
    </Container>
  );
}
