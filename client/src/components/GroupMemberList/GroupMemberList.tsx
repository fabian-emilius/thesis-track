import React, { useState } from 'react'
import { Stack, Group, Text, Button, TextInput } from '@mantine/core'
import { GroupMember } from '../../requests/responses/group'
import GroupRoleSelect from '../GroupRoleSelect/GroupRoleSelect'

interface GroupMemberListProps {
  members: GroupMember[]
  onAddMember: (email: string, role: GroupMember['role']) => void
  onRemoveMember: (userId: string) => void
}

const GroupMemberList: React.FC<GroupMemberListProps> = ({
  members,
  onAddMember,
  onRemoveMember,
}) => {
  const [newMemberEmail, setNewMemberEmail] = useState('')
  const [newMemberRole, setNewMemberRole] = useState<GroupMember['role']>('ADVISOR')

  const handleAddMember = () => {
    if (newMemberEmail) {
      onAddMember(newMemberEmail, newMemberRole)
      setNewMemberEmail('')
      setNewMemberRole('ADVISOR')
    }
  }

  return (
    <Stack gap='md'>
      {members.map((member) => (
        <Group key={member.userId} justify='space-between'>
          <div>
            <Text>{member.userId}</Text>
            <Text size='sm' c='dimmed'>
              {member.role}
            </Text>
          </div>
          <Button variant='subtle' color='red' onClick={() => onRemoveMember(member.userId)}>
            Remove
          </Button>
        </Group>
      ))}

      <Group grow>
        <TextInput
          placeholder='Enter member email'
          value={newMemberEmail}
          onChange={(e) => setNewMemberEmail(e.target.value)}
        />
        <GroupRoleSelect value={newMemberRole} onChange={setNewMemberRole} />
        <Button onClick={handleAddMember}>Add Member</Button>
      </Group>
    </Stack>
  )
}

export default GroupMemberList
