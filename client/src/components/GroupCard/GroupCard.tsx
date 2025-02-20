import React from 'react';
import { Link } from 'react-router-dom';
import { Card, Text, Group as MantineGroup, Image, Button } from '@mantine/core';
import { Group } from '../../types/group';

interface GroupCardProps {
  group: Group;
  showActions?: boolean;
}

export const GroupCard: React.FC<GroupCardProps> = ({ group, showActions = true }) => {
  return (
    <Card shadow="sm" padding="lg" radius="md" withBorder>
      {group.logoUrl && (
        <Card.Section>
          <Image
            src={group.logoUrl}
            height={160}
            alt={group.name}
            fit="contain"
          />
        </Card.Section>
      )}

      <MantineGroup position="apart" mt="md" mb="xs">
        <Text fw={500} size="lg">
          {group.name}
        </Text>
      </MantineGroup>

      <Text size="sm" c="dimmed">
        {group.description}
      </Text>

      {showActions && (
        <MantineGroup position="apart" mt="md">
          <Button
            component={Link}
            to={`/groups/${group.slug}`}
            variant="light"
            color="blue"
            fullWidth
          >
            View Group
          </Button>
        </MantineGroup>
      )}
    </Card>
  );
};
