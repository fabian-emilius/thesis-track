import React, { useState } from 'react'
import { Button, Group, Modal, TextInput, Textarea, Text } from '@mantine/core'
import { useForm } from '@mantine/form'
import { useGroups } from '../../providers/GroupsProvider/hooks'

interface GroupModalProps {
  opened: boolean
  onClose: () => void
  group?: {
    id: string
    name: string
    description: string
  }
}

const GroupModal: React.FC<GroupModalProps> = ({ opened, onClose, group }) => {
  const { createGroup, updateGroup } = useGroups()
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const form = useForm({
    initialValues: {
      name: group?.name || '',
      description: group?.description || '',
    },
    validate: {
      name: (value) => {
        if (!value) return 'Name is required'
        if (value.length < 3) return 'Name must be at least 3 characters'
        if (value.length > 50) return 'Name must be less than 50 characters'
        return null
      },
      description: (value) => {
        if (value && value.length > 500) return 'Description must be less than 500 characters'
        return null
      },
    },
  })

  const handleSubmit = async (values: typeof form.values) => {
    setError(null)
    setLoading(true)
    try {
      if (group) {
        await updateGroup(group.id, values)
      } else {
        await createGroup(values)
      }
      onClose()
      form.reset()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred while saving the group')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      opened={opened}
      onClose={onClose}
      title={group ? 'Edit Group' : 'Create Group'}
      size="md"
    >
      <form onSubmit={form.onSubmit(handleSubmit)}>
        {error && (
          <Text color="red" mb="md">
            {error}
          </Text>
        )}
        <TextInput
          label="Name"
          placeholder="Enter group name"
          required
          mb="md"
          data-autofocus
          {...form.getInputProps('name')}
        />
        <Textarea
          label="Description"
          placeholder="Enter group description"
          mb="xl"
          {...form.getInputProps('description')}
        />
        <Group justify="flex-end">
          <Button variant="light" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button type="submit" loading={loading}>
            {group ? 'Update' : 'Create'}
          </Button>
        </Group>
      </form>
    </Modal>
  )
}

export default GroupModal