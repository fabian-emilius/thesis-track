import React from 'react';
import { SimpleGrid } from '@mantine/core';
import { Group } from '../../requests/responses/group';
import { GroupCard } from '../GroupCard/GroupCard';

interface GroupListProps {
  groups: Group[];
}

export function GroupList({ groups }: GroupListProps) {
  return (
    <SimpleGrid
      cols={{ base: 1, sm: 2, lg: 3 }}
      spacing="lg"
      verticalSpacing="lg"
    >
      {groups.map((group) => (
        <GroupCard key={group.id} group={group} />
      ))}
    </SimpleGrid>
  );
}