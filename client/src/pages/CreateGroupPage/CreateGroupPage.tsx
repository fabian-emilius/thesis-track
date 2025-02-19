import { Container, Title } from '@mantine/core';
import { useNavigate } from 'react-router';
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm';

function CreateGroupPage() {
  const navigate = useNavigate();

  const handleCreateGroup = async (values: any) => {
    try {
      // TODO: Implement group creation logic
      console.log('Create group:', values);
      // After successful creation, navigate to the new group's page
      navigate(`/groups/${values.slug}`);
    } catch (error) {
      console.error('Failed to create group:', error);
    }
  };

  return (
    <Container size="xl" py="xl">
      <Title order={1} mb="xl">
        Create New Group
      </Title>

      <GroupSettingsForm onSubmit={handleCreateGroup} />
    </Container>
  );
}

export default CreateGroupPage;
