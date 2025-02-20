import React from 'react'
import { Container, Title, Alert } from '@mantine/core'
import { useAuthentication } from '../../hooks/authentication'
import GroupSettings from '../../components/GroupSettings/GroupSettings'
import { Group } from '../../types/group'

const AdminGroupPage: React.FC = () => {
  const { user } = useAuthentication()

  if (!user?.isAdmin) {
    return (
      <Container size='md' py='xl'>
        <Alert color='red' title='Access Denied'>
          You don't have permission to access this page.
        </Alert>
      </Container>
    )
  }

  const handleCreateGroup = async (data: Partial<Group>) => {
    // TODO: Implement group creation
    console.log('Creating group:', data)
  }

  return (
    <Container size='md' py='xl'>
      <Title order={1} mb='xl'>
        Create New Group
      </Title>
      <GroupSettings
        group={{
          id: '',
          name: '',
          slug: '',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        }}
        onSave={handleCreateGroup}
      />
    </Container>
  )
}

export default AdminGroupPage
