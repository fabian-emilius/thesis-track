import { Card, Image, Text, Button, Group } from '@mantine/core';
import { Link } from 'react-router-dom';
import { Group as GroupType } from '../../types/group';
import { ErrorBoundary } from 'react-error-boundary';

interface GroupCardProps {
  /** Group data to display in the card */
  group: GroupType;
  /** Optional click handler for the apply button */
  onApplyClick?: (groupId: string) => void;
  /** Optional className for custom styling */
  className?: string;
  /** Optional test ID for testing */
  'data-testid'?: string;
}

const FallbackComponent = () => (
  <ErrorBoundary FallbackComponent={FallbackComponent}>
      <Card 
        shadow="sm" 
        padding="lg" 
        radius="md" 
        withBorder
        className={className}
        data-testid={testId}
        role="article"
        aria-label={`Group card for ${group.name}`}
      >
    <Text c="dimmed">Failed to load group card</Text>
  </Card>
);

export function GroupCard({ 
  group, 
  onApplyClick, 
  className, 
  'data-testid': testId 
}: GroupCardProps) {
  if (!group?.name) {
    throw new Error('Group name is required');
  }
  return (
    <Card shadow="sm" padding="lg" radius="md" withBorder>
      <Card.Section>
        {group.logo ? (
          <Image
            src={group.logo}
            height={160}
            alt={group.name}
            fallbackSrc="/placeholder-logo.png"
          />
        ) : (
          <div style={{ height: 160, background: 'var(--mantine-color-gray-1)' }} />
        )}
      </Card.Section>

      <Group justify="space-between" mt="md" mb="xs">
        <Text fw={500} size="lg">{group.name}</Text>
      </Group>

      <Text size="sm" c="dimmed" lineClamp={3}>
        {group.description}
      </Text>

      <Button
        component={Link}
        to={`/groups/${group.slug}/apply`}
        variant="light"
        color="blue"
        fullWidth
        mt="md"
        radius="md"
        onClick={(e) => onApplyClick?.(group.id)}
        aria-label={`Apply for thesis in ${group.name}`}
      >
        Apply for Thesis
      </Button>
    </Card>
    </ErrorBoundary>
  );
}
