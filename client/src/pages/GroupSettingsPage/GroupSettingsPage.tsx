import { Container, Title, Tabs, Stack } from '@mantine/core';
import { useParams, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useGroupContext } from '../../providers/GroupContext/context';
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm';
import { GroupRoleManagement } from '../../components/GroupRoleManagement/GroupRoleManagement';
import { EmailTemplateEditor } from '../../components/EmailTemplateEditor/EmailTemplateEditor';

export function GroupSettingsPage() {
  const { groupSlug } = useParams();
  const { currentGroup, setCurrentGroup, availableGroups, isGroupAdmin, updateGroup } = useGroupContext();

  useEffect(() => {
    if (groupSlug && availableGroups.length > 0) {
      const group = availableGroups.find((g) => g.slug === groupSlug);
      setCurrentGroup(group);
    }
  }, [groupSlug, availableGroups, setCurrentGroup]);

  if (!currentGroup) {
    return <Navigate to="/groups" replace />;
  }

  if (!isGroupAdmin(currentGroup.id)) {
    return <Navigate to={`/groups/${currentGroup.slug}`} replace />;
  }

  const handleUpdateGroup = async (values: Partial<typeof currentGroup>) => {
    await updateGroup(currentGroup.id, values);
  };

  const handleUpdateRole = async (userId: string, role: string) => {
    // TODO: Implement role update logic
  };

  const handleRemoveMember = async (userId: string) => {
    // TODO: Implement member removal logic
  };

  const handleUpdateEmailTemplates = async (templates: any) => {
    await updateGroup(currentGroup.id, {
      settings: {
        ...currentGroup.settings,
        emailTemplates: templates,
      },
    });
  };

  return (
    <Container size="xl" py="xl">
      <Stack spacing="xl">
        <Title>Group Settings - {currentGroup.name}</Title>

        <Tabs defaultValue="general">
          <Tabs.List>
            <Tabs.Tab value="general">General</Tabs.Tab>
            <Tabs.Tab value="members">Members</Tabs.Tab>
            <Tabs.Tab value="templates">Email Templates</Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="general" pt="xl">
            <GroupSettingsForm group={currentGroup} onSubmit={handleUpdateGroup} />
          </Tabs.Panel>

          <Tabs.Panel value="members" pt="xl">
            <GroupRoleManagement
              groupId={currentGroup.id}
              members={[]}
              onUpdateRole={handleUpdateRole}
              onRemoveMember={handleRemoveMember}
            />
          </Tabs.Panel>

          <Tabs.Panel value="templates" pt="xl">
            <EmailTemplateEditor
              group={currentGroup}
              onSave={handleUpdateEmailTemplates}
            />
          </Tabs.Panel>
        </Tabs>
      </Stack>
    </Container>
  );
}
