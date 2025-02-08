import { Select, SelectProps } from '@mantine/core';
import { useEffect, useState } from 'react';

interface Group {
  id: string;
  name: string;
  description?: string;
}

interface GroupSelectProps extends Omit<SelectProps, 'data'> {
  onGroupSelect?: (groupId: string) => void;
}

export function GroupSelect({ onGroupSelect, ...props }: GroupSelectProps) {
  const [groups, setGroups] = useState<Group[]>([]);

  useEffect(() => {
    // TODO: Fetch groups from API
    const fetchGroups = async () => {
      // Temporary mock data
      setGroups([
        { id: '1', name: 'Software Engineering' },
        { id: '2', name: 'Data Science' },
      ]);
    };
    fetchGroups();
  }, []);

  return (
    <Select
      label="Research Group"
      placeholder="Select a research group"
      data={groups.map((group) => ({
        value: group.id,
        label: group.name,
      }))}
      onChange={(value) => onGroupSelect?.(value || '')}
      {...props}
    />
  );
}
