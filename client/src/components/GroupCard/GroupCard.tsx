import React from 'react';
import { Card, Image, Text, Button, Group as MantineGroup } from '@mantine/core';
import { Link } from 'react-router-dom';
import { Group } from '../../requests/responses/group';

interface GroupCardProps {
  group: Group;
}

export function GroupCard({ group }: GroupCardProps) {
  return (
    <Card shadow="sm" padding="lg" radius="md" withBorder>
      <Card.Section>
        {group.logoUrl && (
          <Image
            src={group.logoUrl}
            height={160}
            alt={group.name}
            fallbackSrc="/placeholder.svg"
          />
        )}
      </Card.Section>

      <MantineGroup justify="space-between" mt="md" mb="xs">
        <Text fw={500} size="lg">
          {group.name}
        </Text>
      </MantineGroup>

      <Text size="sm" c="dimmed" lineClamp={3}>
        {group.description}
      </Text>

      <MantineGroup justify="space-between" mt="md">
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
        {group.externalLink && (
          <Button
            component="a"
            href={group.externalLink}
            target="_blank"
            variant="subtle"
            color="gray"
            fullWidth
            mt="md"
            radius="md"
          >
            Visit Website
          </Button>
        )}
      </MantineGroup>
    </Card>
  );
}