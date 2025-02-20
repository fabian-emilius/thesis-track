import { Card, Image, Text, Button, Group as MantineGroup } from '@mantine/core';
import { Link } from 'react-router-dom';
import { Group } from '../../types/group';
import { useGroupContext } from '../../providers/GroupContext/context';

interface GroupCardProps {
  group: Group;
}

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

      <MantineGroup mt="md">
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
      </MantineGroup>
    </Card>
  );
}
