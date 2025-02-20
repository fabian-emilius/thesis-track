import React, { useEffect, useState } from 'react';
import { Container, Title, Stack, Tabs, Text } from '@mantine/core';
import { useParams } from 'react-router-dom';
import { useGroupsContext } from '../../providers/GroupsProvider/hooks';
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm';
import { GroupMemberManager } from '../../components/GroupMemberManager/GroupMemberManager';
import { EmailTemplateEditor } from '../../components/EmailTemplateEditor/EmailTemplateEditor';
import { GroupResponse } from '../../requests/responses/group';
import { PageLoader } from '../../components/PageLoader/PageLoader';

export function GroupSettingsPage() {
  const { groupSlug } = useParams<{ groupSlug: string }>();
  const { groups, fetchGroupDetails, updateGroup, updateGroupSettings } = useGroupsContext();
  const [groupDetails, setGroupDetails] = useState<GroupResponse>();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error>();

  const group = groups.find((g) => g.slug === groupSlug);

  useEffect(() => {
    if (group) {
      setLoading(true);
      fetchGroupDetails(group.id)
        .then(setGroupDetails)
        .catch(setError)
        .finally(() => setLoading(false));
    }
  }, [group, fetchGroupDetails]);

  if (loading) {
    return <PageLoader />;
  }

  if (error || !group || !groupDetails) {
    return (
      <Container>
        <Text c="red">Error loading group settings: {error?.message}</Text>
      </Container>
    );
  }

  return (
    <Container size="xl">
      <Stack gap="xl">
        <Title order={1}>Group Settings</Title>

        <Tabs defaultValue="basic">
          <Tabs.List>
            <Tabs.Tab value="basic">Basic Information</Tabs.Tab>
            <Tabs.Tab value="members">Members</Tabs.Tab>
            <Tabs.Tab value="email">Email Templates</Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="basic" pt="xl">
            <GroupSettingsForm
              group={group}
              onSubmit={(values) => updateGroup(group.id, values)}
              onCancel={() => {}}
            />
          </Tabs.Panel>

          <Tabs.Panel value="members" pt="xl">
            <GroupMemberManager
              groupId={group.id}
              members={groupDetails.members}
            />
          </Tabs.Panel>

          <Tabs.Panel value="email" pt="xl">
            <EmailTemplateEditor
              settings={groupDetails.settings}
              onSave={(templates) => updateGroupSettings(group.id, templates)}
            />
          </Tabs.Panel>
        </Tabs>
      </Stack>
    </Container>
  );
}