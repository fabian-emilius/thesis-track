import { Card, Image, Text, Button, Group } from '@mantine/core';
import { Link } from 'react-router-dom';
import { Group as GroupType } from '../../types/group';
import { useGroupContext } from '../../providers/GroupContext/context';

interface GroupCardProps {
  /** The group to display */
  group: GroupType;
}

/**
 * Card component for displaying group information
 * Includes group logo, name, description, and action buttons
 */
export function GroupCard({ group }: GroupCardProps) {
  const { isGroupAdmin } = useGroupContext();

  return (
    <Card shadow="sm" padding="lg" radius="md" withBorder>
      {group.logoUrl && (
        <Card.Section>
          <Image
            src={group.logoUrl}
            height={160}
            alt={group.name}
            fallbackSrc="/placeholder-logo.png"
          />
        </Card.Section>
      )}

      <Text fw={500} size="lg" mt="md">
        {group.name}
      </Text>

      {group.description && (
        <Text size="sm" c="dimmed">
          {group.description}
        </Text>
      )}

      <Group gap="sm" mt="md">
        <Button
          component={Link}
          to={`/groups/${group.slug}`}
          variant="light"
          fullWidth
        >
          Visit Group
        </Button>
        {isGroupAdmin(group.id) && (
          <Button
            component={Link}
            to={`/groups/${group.slug}/settings`}
            variant="outline"
            fullWidth
          >
            Manage
          </Button>
        )}
      </Group>
    </Card>
  );
}
