import React from 'react';
import { Link } from 'react-router-dom';
import { Card, Text, Group, Image, Button } from '@mantine/core';
import { Group as GroupType } from '../../types/group';

interface GroupCardProps {
  group: GroupType;
  showActions?: boolean;
}

const GroupCard: React.FC<GroupCardProps> = ({ group, showActions = true }) => {
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

      <Group justify="space-between" mt="md" mb="xs">
        <Text fw={500} size="lg">
          {group.name}
        </Text>
      </Group>

      <Text size="sm" c="dimmed">
        {group.description}
      </Text>

      {showActions && (
        <Group mt="md">
          <Button
            component={Link}
            to={`/groups/${group.slug}`}
            variant="light"
            color="blue"
            fullWidth
          >
            View Group
          </Button>
        </Group>
      )}
    </Card>
  );
};

export default GroupCard;
