import { Container, Title, Tabs, Text, Alert, Button, Group } from '@mantine/core';
import { useParams, Link } from 'react-router-dom';
import { PageLoader } from '../../components/PageLoader/PageLoader';
import { TopicsTable } from '../../components/TopicsTable/TopicsTable';
import { ThesesTable } from '../../components/ThesesTable/ThesesTable';
import { GroupManagement } from '../../components/GroupManagement/GroupManagement';
import { useGroup } from '../../providers/GroupProvider';
import { useGroupPermissions } from '../../hooks/useGroupPermissions';
import { IconAlertCircle, IconSettings } from '@phosphor-react/icons';

export function GroupDetailPage() {
  const { groupId } = useParams<{ groupId: string }>();
  const { group, loading, error, fetchGroup } = useGroup();
  const { canManageGroup, canCreateTopic, canCreateThesis } = useGroupPermissions(groupId);

  useEffect(() => {
    if (groupId) {
      fetchGroup(groupId);
    }
  }, [groupId, fetchGroup]);

  if (loading) {
    return <PageLoader />;
  }

  if (error) {
    return (
      <Container size="lg" py="xl">
        <Alert icon={<IconAlertCircle />} title="Error" color="red">
          {error}
        </Alert>
      </Container>
    );
  }

  if (!group) {
    return (
      <Container size="lg" py="xl">
        <Alert icon={<IconAlertCircle />} title="Not Found" color="yellow">
          Group not found
        </Alert>
      </Container>
    );
  }

  return (
    <Container size="lg" py="xl">
      <Group justify="space-between" align="flex-start" mb="xs">
        <Title order={1}>{group.name}</Title>
      </Group>
      <Text color="dimmed" mb="xl">{group.description}</Text>

      <Tabs defaultValue="topics">
        <Tabs.List>
          <Tabs.Tab value="topics">Topics</Tabs.Tab>
          <Tabs.Tab value="theses">Theses</Tabs.Tab>
          {canManageGroup && <Tabs.Tab value="management">Management</Tabs.Tab>}
        </Tabs.List>

        <Tabs.Panel value="topics" pt="xl">
          <TopicsTable 
            groupId={groupId} 
            canCreate={canCreateTopic}
            showActions={canManageGroup}
          />
        </Tabs.Panel>

        <Tabs.Panel value="theses" pt="xl">
          <ThesesTable 
            groupId={groupId}
            canCreate={canCreateThesis}
            showActions={canManageGroup}
          />
        </Tabs.Panel>

        {canManageGroup && (
          <Tabs.Panel value="management" pt="xl">
            <GroupManagement groupId={groupId} />
          </Tabs.Panel>
        )}
      </Tabs>
    </Container>
  );
}
