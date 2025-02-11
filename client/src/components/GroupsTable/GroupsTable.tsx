import React from 'react'
import { Table, Text, Button, Group } from '@mantine/core'
import { useGroups } from '../../providers/GroupsProvider/hooks'

interface GroupsTableProps {
  onEdit: (group: { id: string; name: string; description: string }) => void
}

const GroupsTable: React.FC<GroupsTableProps> = ({ onEdit }) => {
  const { groups, loading, deleteGroup } = useGroups()

  if (loading) {
    return <Text>Loading groups...</Text>
  }

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this group?')) {
      try {
        await deleteGroup(id)
      } catch (error) {
        console.error('Failed to delete group:', error)
      }
    }
  }

  return (
    <Table>
      <Table.Thead>
        <Table.Tr>
          <Table.Th>Name</Table.Th>
          <Table.Th>Description</Table.Th>
          <Table.Th>Created</Table.Th>
          <Table.Th>Actions</Table.Th>
        </Table.Tr>
      </Table.Thead>
      <Table.Tbody>
        {groups.map((group) => (
          <Table.Tr key={group.id}>
            <Table.Td>{group.name}</Table.Td>
            <Table.Td>{group.description}</Table.Td>
            <Table.Td>{new Date(group.createdAt).toLocaleDateString()}</Table.Td>
            <Table.Td>
              <Group gap="xs">
                <Button
                  variant="light"
                  size="xs"
                  onClick={() => onEdit(group)}
                >
                  Edit
                </Button>
                <Button
                  variant="light"
                  color="red"
                  size="xs"
                  onClick={() => handleDelete(group.id)}
                >
                  Delete
                </Button>
              </Group>
            </Table.Td>
          </Table.Tr>
        ))}
      </Table.Tbody>
    </Table>
  )
}

export default GroupsTable