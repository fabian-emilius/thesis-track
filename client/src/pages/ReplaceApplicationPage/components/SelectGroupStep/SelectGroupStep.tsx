import React from 'react';
import { Stack, Text } from '@mantine/core';
import { useGroupsContext } from '../../../../providers/GroupsProvider/hooks';
import { GroupList } from '../../../../components/GroupList/GroupList';

interface SelectGroupStepProps {
  onGroupSelect: (groupId: string) => void;
}

export function SelectGroupStep({ onGroupSelect }: SelectGroupStepProps) {
  const { groups } = useGroupsContext();

  return (
    <Stack gap="xl">
      <div>
        <Text size="xl" fw={500}>
          Select Research Group
        </Text>
        <Text size="sm" c="dimmed" mt="xs">
          Choose the research group you want to apply to. Each group has different research focuses and requirements.
        </Text>
      </div>

      <GroupList
        groups={groups.map(group => ({
          ...group,
          onClick: () => onGroupSelect(group.id)
        }))}
      />
    </Stack>
  );
}