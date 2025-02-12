import { Container, Alert, LoadingOverlay } from '@mantine/core';
import { useNavigate, useParams } from 'react-router-dom';
import { useGroupContext } from '../../providers/GroupContext/context';
import { GroupGrid } from '../GroupGrid/GroupGrid';
import { useGroups, useGroupBySlug } from '../../hooks/groups';
import { IconAlertCircle } from '@tabler/icons-react';

interface GroupSelectionStepProps {
  onGroupSelect: (groupId: string) => void;
}

export function GroupSelectionStep({ onGroupSelect }: GroupSelectionStepProps) {
  const { groupSlug } = useParams();
  const navigate = useNavigate();
  const { setCurrentGroup } = useGroupContext();
  
  const { data: groups, isLoading: isLoadingGroups, error: groupsError } = useGroups();
  const { data: selectedGroup, isLoading: isLoadingGroup } = useGroupBySlug(groupSlug);

  const isLoading = isLoadingGroups || isLoadingGroup;

  const handleGroupSelect = async (groupId: string) => {
    const selected = groups?.find(g => g.id === groupId);
    if (selected) {
      setCurrentGroup(selected);
      onGroupSelect(groupId);
      navigate('student-information');
    }
  };

  if (groupsError) {
    return (
      <Container size="xl">
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          Failed to load groups. Please try again later.
        </Alert>
      </Container>
    );
  }

  return (
    <Container size="xl" pos="relative">
      <LoadingOverlay visible={isLoading} />
      <GroupGrid groups={groups || []} onGroupSelect={handleGroupSelect} />
    </Container>
  );
}
