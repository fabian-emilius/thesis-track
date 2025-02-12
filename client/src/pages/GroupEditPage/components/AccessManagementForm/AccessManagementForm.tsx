import { Stack, Select, Button, Group as MantineGroup } from '@mantine/core';
import { useState } from 'react';
import { GroupRole, GroupMember } from '../../../../types/group';
import { UserMultiSelect } from '../../../../components/UserMultiSelect/UserMultiSelect';

interface AccessManagementFormProps {
  members: GroupMember[];
  onAddMember: (userId: string, role: GroupRole) => void;
  onRemoveMember: (userId: string) => void;
}

export function AccessManagementForm({
  members,
  onAddMember,
  onRemoveMember,
}: AccessManagementFormProps) {
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);
  const [selectedRole, setSelectedRole] = useState<GroupRole>('ADVISOR');

  const handleAddMembers = () => {
    selectedUsers.forEach(userId => onAddMember(userId, selectedRole));
    setSelectedUsers([]);
  };

  return (
    <Stack>
      <MantineGroup grow>
        <UserMultiSelect
          label="Select Users"
          value={selectedUsers}
          onChange={setSelectedUsers}
          excludeUserIds={members.map(m => m.userId)}
        />

        <Select
          label="Role"
          value={selectedRole}
          onChange={(value) => setSelectedRole(value as GroupRole)}
          data={[
            { value: 'GROUP_ADMIN', label: 'Group Admin' },
            { value: 'SUPERVISOR', label: 'Supervisor' },
            { value: 'ADVISOR', label: 'Advisor' },
          ]}
        />
      </MantineGroup>

      <Button onClick={handleAddMembers} disabled={selectedUsers.length === 0}>
        Add Members
      </Button>

      {/* TODO: Add member list with remove functionality */}
    </Stack>
  );
}
