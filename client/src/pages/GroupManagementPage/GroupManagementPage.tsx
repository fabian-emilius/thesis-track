import React, { useState } from 'react'
import { Container, Title, Button, Group, Stack, Modal } from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm'
import { useGroupContext } from '../../providers/GroupContext/hooks'
import { DataTable } from 'mantine-datatable'
import { showNotification } from '@mantine/notifications'

export const GroupManagementPage: React.FC = () => {
  const { groups, createGroup, deleteGroup } = useGroupContext()
  const [opened, { open, close }] = useDisclosure(false)
  const [loading, setLoading] = useState(false)

  const handleCreateGroup = async (values: any) => {
    try {
      setLoading(true)
      await createGroup(values)
      showNotification({
        title: 'Success',
        message: 'Group created successfully',
        color: 'green',
      })
      close()
    } catch (error) {
      showNotification({
        title: 'Error',
        message: error instanceof Error ? error.message : 'Failed to create group',
        color: 'red',
      })
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteGroup = async (groupId: string) => {
    if (!window.confirm('Are you sure you want to delete this group?')) {
      return
    }

    try {
      await deleteGroup(groupId)
      showNotification({
        title: 'Success',
        message: 'Group deleted successfully',
        color: 'green',
      })
    } catch (error) {
      showNotification({
        title: 'Error',
        message: error instanceof Error ? error.message : 'Failed to delete group',
        color: 'red',
      })
    }
  }

  return (
    <Container size="xl">
      <Stack gap="xl">
        <Group justify="space-between">
          <Title order={1}>Manage Groups</Title>
          <Button onClick={open}>Create New Group</Button>
        </Group>

        <DataTable
          withBorder
          borderRadius="sm"
          withColumnBorders
          striped
          highlightOnHover
          records={groups}
          columns={[
            { accessor: 'name', title: 'Name' },
            { accessor: 'description', title: 'Description', width: '40%' },
            {
              accessor: 'actions',
              title: 'Actions',
              render: (group) => (
                <Group gap="xs">
                  <Button
                    variant="light"
                    component="a"
                    href={`/groups/${group.slug}/settings`}
                  >
                    Settings
                  </Button>
                  <Button
                    variant="light"
                    color="red"
                    onClick={() => handleDeleteGroup(group.id)}
                  >
                    Delete
                  </Button>
                </Group>
              ),
            },
          ]}
        />
      </Stack>

      <Modal
        opened={opened}
        onClose={close}
        title="Create New Group"
        size="lg"
      >
        <GroupSettingsForm
          onSubmit={handleCreateGroup}
          isLoading={loading}
        />
      </Modal>
    </Container>
  )
}

export default GroupManagementPage
