import { Table, Select, Button, Group, Text } from '@mantine/core'
import { useState } from 'react'
import { GroupRole } from '../../types/group'

interface GroupRoleManagementProps {
  groupId: string
  roles: GroupRole[]
  onAddRole: (userId: string, role: string) => void
  onRemoveRole: (userId: string) => void
  isLoading?: boolean
}

export function GroupRoleManagement({
  groupId,
  roles,
  onAddRole,
  onRemoveRole,
  isLoading,
}: GroupRoleManagementProps) {
  const [selectedUser, setSelectedUser] = useState('')
  const [selectedRole, setSelectedRole] = useState('advisor')

  const handleAddRole = () => {
    if (selectedUser && selectedRole) {
      onAddRole(selectedUser, selectedRole)
      setSelectedUser('')
      setSelectedRole('advisor')
    }
  }

  return (
    <div>
      <Group justify='space-between' mb='md'>
        <Text size='lg' fw={500}>
          Group Members
        </Text>

        <Group gap='sm'>
          <Select
            label=''
            placeholder='Select user'
            value={selectedUser}
            onChange={(value) => setSelectedUser(value || '')}
            data={[]} // TODO: Add user data
            searchable
            clearable
          />

          <Select
            value={selectedRole}
            onChange={(value) => setSelectedRole(value || 'advisor')}
            data={[
              { value: 'admin', label: 'Admin' },
              { value: 'supervisor', label: 'Supervisor' },
              { value: 'advisor', label: 'Advisor' },
            ]}
            w={120}
          />

          <Button
            onClick={handleAddRole}
            disabled={!selectedUser || !selectedRole}
            loading={isLoading}
            size='sm'
          >
            Add Member
          </Button>
        </Group>
      </Group>

      <Table>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>User</Table.Th>
            <Table.Th>Role</Table.Th>
            <Table.Th>Actions</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {roles.map((role) => (
            <Table.Tr key={`${role.groupId}-${role.role}`}>
              <Table.Td>{role.groupId}</Table.Td>
              <Table.Td style={{ textTransform: 'capitalize' }}>{role.role}</Table.Td>
              <Table.Td>
                <Button
                  variant='subtle'
                  color='red'
                  size='sm'
                  onClick={() => onRemoveRole(role.groupId)}
                  loading={isLoading}
                >
                  Remove
                </Button>
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </div>
  )
}

export default GroupRoleManagement
