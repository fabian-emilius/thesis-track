import React from 'react';
import { Container, Title, SimpleGrid, Text } from '@mantine/core';
import GroupCard from '../../components/GroupCard/GroupCard';
import { useGroup } from '../../providers/GroupContext/hooks';

const GroupOverviewPage: React.FC = () => {
  const { userGroups, loading } = useGroup();

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Container size="xl" py="xl">
      <Title order={1} mb="xl">Available Groups</Title>
      
      {userGroups.length === 0 ? (
        <Text c="dimmed">No groups available.</Text>
      ) : (
        <SimpleGrid
          cols={3}
          spacing="lg"
          breakpoints={[
            { maxWidth: 'md', cols: 2, spacing: 'md' },
            { maxWidth: 'sm', cols: 1, spacing: 'sm' },
          ]}
        >
          {userGroups.map((group) => (
            <GroupCard key={group.id} group={group} />
          ))}
        </SimpleGrid>
      )}
    </Container>
  );
};

export default GroupOverviewPage;
