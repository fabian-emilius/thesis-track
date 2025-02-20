import React, { useState } from 'react';
import { Container, Title, Stack, Button, Text, Modal } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { useGroupsContext } from '../../providers/GroupsProvider/hooks';
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm';
import { Group } from '../../requests/responses/group';
import { PageLoader } from '../../components/PageLoader/PageLoader';

export function GroupManagementPage() {
  const { groups, isLoading, error, createGroup } = useGroupsContext();
  const [opened, { open, close }] = useDisclosure(false);
  const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);

  if (isLoading) {
    return <PageLoader />;
  }

  if (error) {
    return (
      <Container>
        <Text c="red">Error loading groups: {error.message}</Text>
      </Container>
    );
  }

  const handleCreateGroup = async (groupData: Partial<Group>) => {
    try {
      await createGroup(groupData);
      close();
    } catch (err) {
      console.error('Error creating group:', err);
    }
  };

  return (
    <Container size="xl">
      <Stack gap="xl">
        <div>
          <Title order={1}>Group Management</Title>
          <Text size="lg" c="dimmed" mt="sm">
            Manage research groups and their settings.
          </Text>
        </div>

        <Button onClick={open}>Create New Group</Button>

        <Modal
          opened={opened}
          onClose={close}
          title="Create New Group"
          size="lg"
        >
          <GroupSettingsForm onSubmit={handleCreateGroup} onCancel={close} />
        </Modal>

        {/* TODO: Add group list with edit/delete actions */}
      </Stack>
    </Container>
  );
}