import { Card, Text, Group, Button, Image } from '@mantine/core'
import { Link } from 'react-router'
import { Group as GroupType } from '../../types/group'

interface GroupCardProps {
  /** The group data to display */
  group: GroupType
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
    <Card shadow='sm' padding='lg' radius='md' withBorder>
      {group.logoUrl && (
        <Card.Section>
          <Image
            src={group.logoUrl}
            height={160}
            alt={group.name}
            fallbackSrc='/placeholder-logo.png'
          />
        </Card.Section>
      )}

      <Group justify='space-between' mt='md' mb='xs'>
        <Text fw={500} size='lg'>
          {group.name}
        </Text>
      </Group>

      <Text size='sm' c='dimmed' lineClamp={3}>
        {group.description}
      </Text>

      {group.websiteUrl && (
        <Text size='sm' c='dimmed' mt='sm'>
          <a href={group.websiteUrl} target='_blank' rel='noopener noreferrer'>
            Visit Website
          </a>
        </Text>
      )}

      <Button
        component={Link}
        to={`/groups/${group.slug}`}
        variant='light'
        color='blue'
        fullWidth
        mt='md'
        radius='md'
      >
        View Group
      </Button>
    </Card>
  )
}

export default GroupCard
