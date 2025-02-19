import { Card, Text, Group as MantineGroup, Button, Image } from '@mantine/core';
import { Link } from 'react-router-dom';
import { Group } from '../../types/group';

/**
 * Props for the GroupCard component
 */
interface GroupCardProps {
  /** The group data to display */
  group: Group;
}

/**
 * A card component that displays group information including:
 * - Group logo (if available)
 * - Group name
 * - Group description
 * - Website link (if available)
 * - View group button
 */
export function GroupCard({ group }: GroupCardProps) {
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

      <MantineGroup position="apart" mt="md" mb="xs">
        <Text fw={500} size="lg">
          {group.name}
        </Text>
      </MantineGroup>

      <Text size="sm" c="dimmed" lineClamp={3}>
        {group.description}
      </Text>

      {group.websiteUrl && (
        <Text size="sm" c="dimmed" mt="sm">
          <a href={group.websiteUrl} target="_blank" rel="noopener noreferrer">
            Visit Website
          </a>
        </Text>
      )}

      <Button
        component={Link}
        to={`/groups/${group.slug}`}
        variant="light"
        color="blue"
        fullWidth
        mt="md"
        radius="md"
      >
        View Group
      </Button>
    </Card>
  );
}
