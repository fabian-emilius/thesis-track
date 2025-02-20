import React, { useState } from 'react'
import { Table, Button, Group, Select, Text } from '@mantine/core'
import { useGroupContext } from '../../providers/GroupContext/hooks'
import { IGroupMember } from '../../providers/GroupContext/types'
import { UserMultiSelect } from '../UserMultiSelect/UserMultiSelect'

interface GroupMemberManagerProps {
  groupId: string
  members: Array<{ user: { id: string; name: string }; role: IGroupMember['role'] }>
}

export const GroupMemberManager: React.FC<GroupMemberManagerProps> = ({ groupId, members }) => {
  const { addGroupMember, removeGroupMember } = useGroupContext()
  const [selectedUser, setSelectedUser] = useState<string | null>(null)
  const [selectedRole, setSelectedRole] = useState<IGroupMember['role']>('advisor')

  const handleAddMember = async () => {
    if (selectedUser) {
      await addGroupMember(groupId, selectedUser, selectedRole)
      setSelectedUser(null)
    }
  }

  return (
    <>
      <Group mb="lg">
        <UserMultiSelect
          label="Add Member"
          maxSelectedValues={1}
          value={selectedUser ? [selectedUser] : []}
          onChange={(value) => setSelectedUser(value[0] || null)}
        />
        <Select
          label="Role"
          value={selectedRole}
          onChange={(value: IGroupMember['role']) => setSelectedRole(value)}
          data={[
            { value: 'supervisor', label: 'Supervisor' },
            { value: 'advisor', label: 'Advisor' },
            { value: 'group_admin', label: 'Group Admin' },
          ]}
        />
        <Button onClick={handleAddMember} disabled={!selectedUser}>
          Add Member
        </Button>
      </Group>

      <Table>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Name</Table.Th>
            <Table.Th>Role</Table.Th>
            <Table.Th>Actions</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {members.map((member) => (
            <Table.Tr key={member.user.id}>
              <Table.Td>
                <Text>{member.user.name}</Text>
              </Table.Td>
              <Table.Td>
                <Text transform="capitalize">
                  {member.role.replace('_', ' ')}
                </Text>
              </Table.Td>
              <Table.Td>
                <Button
                  variant="subtle"
                  color="red"
                  onClick={() => removeGroupMember(groupId, member.user.id)}
                >
                  Remove
                </Button>
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </>
  )
}
