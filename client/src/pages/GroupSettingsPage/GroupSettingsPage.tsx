import React from 'react';
import { Container, Title, Alert } from '@mantine/core';
import { useParams, Navigate } from 'react-router-dom';
import { useCurrentGroup, useIsGroupAdmin } from '../../providers/GroupContext/hooks';
import GroupSettings from '../../components/GroupSettings/GroupSettings';

const GroupSettingsPage: React.FC = () => {
  const { groupSlug } = useParams<{ groupSlug: string }>();
  const currentGroup = useCurrentGroup();
  const isAdmin = currentGroup ? useIsGroupAdmin(currentGroup.id) : false;

  if (!currentGroup || currentGroup.slug !== groupSlug) {
    return <Navigate to="/groups" replace />;
  }

  if (!isAdmin) {
    return (
      <Container size="md" py="xl">
        <Alert color="red" title="Access Denied">
          You don't have permission to access this page.
        </Alert>
      </Container>
    );
  }

  const handleSaveSettings = async (data: Partial<typeof currentGroup>) => {
    // TODO: Implement settings update
    console.log('Saving settings:', data);
  };

  return (
    <Container size="md" py="xl">
      <Title order={1} mb="xl">Group Settings</Title>
      <GroupSettings group={currentGroup} onSave={handleSaveSettings} />
    </Container>
  );
};

export default GroupSettingsPage;
