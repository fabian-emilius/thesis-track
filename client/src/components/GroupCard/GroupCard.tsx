import React from 'react'
import { Card, Image, Text, Group, Button } from '@mantine/core'
import { Link } from 'react-router-dom'
import { Group as GroupType } from '../../requests/responses/group'

interface GroupCardProps {
  /** Group data to display */
  group: GroupType
  /** Optional click handler for the entire card */
  onClick?: () => void
  /** Optional custom styles */
  className?: string
}

/**
 * Displays a card with group information including logo, name, and description
 * Used in group listings and selection interfaces
 */
const GroupCard: React.FC<GroupCardProps> = ({ group, onClick, className }) => {
  return (
    <Card
      shadow='sm'
      padding='lg'
      radius='md'
      withBorder
      className={className}
      onClick={onClick}
      style={{ cursor: onClick ? 'pointer' : 'default' }}
    >
      {group.logoFilename && (
        <Card.Section>
          <Image
            src={`/api/v2/groups/${group.slug}/logo`}
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

      <Button
        variant='light'
        color='blue'
        fullWidth
        mt='md'
        radius='md'
        component={Link}
        to={`/groups/${group.slug}`}
      >
        View Group
      </Button>
    </Card>
  )
}

export default GroupCard
