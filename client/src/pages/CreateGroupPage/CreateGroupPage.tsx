import { Container, Title } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { useGroupContext } from '../../providers/GroupContext/context';
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm';
import { useHasGroupAccess } from '../../hooks/authentication';

export function CreateGroupPage() {
  const navigate = useNavigate();
  const { createGroup } = useGroupContext();
  const isAdmin = useHasGroupAccess('admin');

  if (!isAdmin) {
    navigate('/groups', { replace: true });
    return null;
  }

  const handleCreateGroup = async (values: any) => {
    await createGroup(values);
    navigate('/groups');
  };

  return (
    <Container size="xl" py="xl">
      <Title mb="xl">Create New Group</Title>
      <GroupSettingsForm onSubmit={handleCreateGroup} />
    </Container>
  );
}
