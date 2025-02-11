import React, { useState } from 'react'
import { Button, Container, Group, Title, Text, Alert, LoadingOverlay } from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import { Plus, Warning } from 'phosphor-react'
import GroupsTable from '../../components/GroupsTable/GroupsTable'
import GroupModal from '../../components/GroupModal/GroupModal'
import { useGroups } from '../../providers/GroupsProvider/hooks'

/**
 * GroupsPage component provides an interface for managing groups in the system.
 * It allows administrators to create, edit, and delete groups, as well as view
 * all existing groups in a tabular format.
 */
const GroupsPage: React.FC = () => {
  const [opened, { open, close }] = useDisclosure(false)
  const { loading, error } = useGroups()
  const [selectedGroup, setSelectedGroup] = useState<{
    id: string
    name: string
    description: string
  } | null>(null)

  const handleEdit = (group: { id: string; name: string; description: string }) => {
    setSelectedGroup(group)
    open()
  }

  const handleClose = () => {
    setSelectedGroup(null)
    close()
  }

  if (error) {
    return (
      <Container size="xl">
        <Alert icon={<Warning />} title="Error" color="red">
          {error.message || 'An error occurred while loading groups'}
        </Alert>
      </Container>
    )
  }

  return (
    <Container size="xl" pos="relative">
      <LoadingOverlay visible={loading} />
      <Group justify="space-between" mb="xl">
        <Title>Groups</Title>
        <Button
          leftSection={<Plus />}
          onClick={open}
          disabled={loading}
        >
          Create Group
        </Button>
      </Group>
      <GroupsTable onEdit={handleEdit} />
      <GroupModal
        opened={opened}
        onClose={handleClose}
        group={selectedGroup || undefined}
      />
    </Container>
  )
}

export default GroupsPage