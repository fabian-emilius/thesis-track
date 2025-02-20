import React from 'react';
import { Title, Container, Text, Stack } from '@mantine/core';
import { useGroupsContext } from '../../providers/GroupsProvider/hooks';
import { GroupList } from '../../components/GroupList/GroupList';
import { PageLoader } from '../../components/PageLoader/PageLoader';

export function GroupsOverviewPage() {
  const { groups, isLoading, error } = useGroupsContext();

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

  return (
    <Container size="xl">
      <Stack gap="xl">
        <div>
          <Title order={1}>Research Groups</Title>
          <Text size="lg" c="dimmed" mt="sm">
            Select a research group to view available thesis topics and ongoing research.
          </Text>
        </div>

        <GroupList groups={groups} />
      </Stack>
    </Container>
  );
}