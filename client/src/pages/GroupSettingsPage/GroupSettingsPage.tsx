import { Container, Tabs, Title, Alert } from '@mantine/core';
import { useParams } from 'react-router';
import { useGroupContext } from '../../providers/GroupContext/context';
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm';
import { GroupRoleManagement } from '../../components/GroupRoleManagement/GroupRoleManagement';

function GroupSettingsPage() {
  const { groupSlug } = useParams();
  const { currentGroup, userGroupRoles, isGroupAdmin } = useGroupContext();

  if (!currentGroup) {
    return (
      <Container size="xl" py="xl">
        <Alert color="red">Group not found</Alert>
      </Container>
    );
  }

  if (!isGroupAdmin(currentGroup.id)) {
    return (
      <Container size="xl" py="xl">
        <Alert color="red">You don't have permission to access this page</Alert>
      </Container>
    );
  }

  const handleUpdateGroup = async (values: any) => {
    // TODO: Implement group update logic
    console.log('Update group:', values);
  };

  const handleAddRole = async (userId: string, role: string) => {
    // TODO: Implement add role logic
    console.log('Add role:', { userId, role });
  };

  const handleRemoveRole = async (userId: string) => {
    // TODO: Implement remove role logic
    console.log('Remove role:', userId);
  };

  return (
    <Container size="xl" py="xl">
      <Title order={1} mb="xl">
        Group Settings
      </Title>

      <Tabs defaultValue="general">
        <Tabs.List>
          <Tabs.Tab value="general">General</Tabs.Tab>
          <Tabs.Tab value="members">Members</Tabs.Tab>
          <Tabs.Tab value="templates">Email Templates</Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="general" pt="xl">
          <GroupSettingsForm
            group={currentGroup}
            onSubmit={handleUpdateGroup}
          />
        </Tabs.Panel>

        <Tabs.Panel value="members" pt="xl">
          <GroupRoleManagement
            groupId={currentGroup.id}
            roles={userGroupRoles.filter((role) => role.groupId === currentGroup.id)}
            onAddRole={handleAddRole}
            onRemoveRole={handleRemoveRole}
          />
        </Tabs.Panel>

        <Tabs.Panel value="templates" pt="xl">
          {/* TODO: Implement email template editor */}
          <Alert>Email template customization coming soon</Alert>
        </Tabs.Panel>
      </Tabs>
    </Container>
  );
}

export default GroupSettingsPage;
