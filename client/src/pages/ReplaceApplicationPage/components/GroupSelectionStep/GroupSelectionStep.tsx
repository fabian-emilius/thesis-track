import { Box, Title, Text } from '@mantine/core';
import { GroupSelect } from '../../../../components/GroupSelect/GroupSelect';
import { useGroup } from '../../../../providers/GroupProvider';

interface GroupSelectionStepProps {
  onGroupSelect: (groupId: string) => void;
  selectedGroupId?: string;
}

export function GroupSelectionStep({ onGroupSelect, selectedGroupId }: GroupSelectionStepProps) {
  const { groups, loading } = useGroup();

  return (
    <Box>
      <Title order={2} mb="md">Select Research Group</Title>
      <Text color="dimmed" mb="xl">
        Choose the research group you want to apply to. Each group has its own research focus and thesis topics.
      </Text>
      <GroupSelect
        value={selectedGroupId}
        onGroupSelect={onGroupSelect}
        disabled={loading}
      />
    </Box>
  );
}
