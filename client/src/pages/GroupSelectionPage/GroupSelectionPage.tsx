import { Container } from '@mantine/core';
import { GroupGrid } from '../../components/GroupGrid/GroupGrid';
import { useGroups } from '../../hooks/group';

export function GroupSelectionPage() {
  const { data: groups = [], isLoading } = useGroups();

  return (
    <Container size="xl">
      <GroupGrid groups={groups} isLoading={isLoading} />
    </Container>
  );
}

export default GroupSelectionPage;
