import React from 'react';
import { Stack, Title, Text } from '@mantine/core';
import { usePageTitle } from '../../hooks/theme';
import PublicArea from '../../app/layout/PublicArea/PublicArea';
import GroupOverview from '../../components/GroupOverview';

const LandingPage = () => {
  usePageTitle('Find a Thesis Topic');

  return (
    <PublicArea>
      <Stack>
        <Stack spacing="xs">
          <Title order={1}>Welcome to Thesis Management</Title>
          <Text>
            Please select a group to explore available topics and theses. Groups help organize
            topics and theses by area of interest or department.
          </Text>
        </Stack>
        <GroupOverview />
      </Stack>
    </PublicArea>
  );
};

export default LandingPage;
