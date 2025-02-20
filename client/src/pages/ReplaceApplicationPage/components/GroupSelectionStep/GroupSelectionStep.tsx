import { Select, Stack, Text } from '@mantine/core';
import { useGroupContext } from '../../../../providers/GroupContext/context';

interface GroupSelectionStepProps {
  value: string;
  onChange: (value: string) => void;
  error?: string;
}

export function GroupSelectionStep({ value, onChange, error }: GroupSelectionStepProps) {
  const { availableGroups } = useGroupContext();

  return (
    <Stack>
      <Text size="sm">
        Please select the group you want to submit your thesis application to.
        Each group may have different requirements and processes for thesis applications.
      </Text>

      <Select
        label="Select Group"
        placeholder="Choose a group"
        value={value}
        onChange={(val) => val && onChange(val)}
        error={error}
        data={availableGroups.map((group) => ({
          value: group.id,
          label: group.name,
          description: group.description,
        }))}
        searchable
        clearable
        required
      />
    </Stack>
  );
}
