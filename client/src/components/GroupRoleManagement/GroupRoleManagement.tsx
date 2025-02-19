import { Table, Select, Button, Group as MantineGroup, Text } from '@mantine/core';
import { useState } from 'react';
import { UserMultiSelect } from '../UserMultiSelect/UserMultiSelect';
import { GroupRole } from '../../types/group';

interface GroupRoleManagementProps {
  groupId: string;
  roles: GroupRole[];
  onAddRole: (userId: string, role: string) => void;
  onRemoveRole: (userId: string) => void;
  isLoading?: boolean;
}

export function GroupRoleManagement({
  groupId,
  roles,
  onAddRole,
  onRemoveRole,
  isLoading,
}: GroupRoleManagementProps) {
  const [selectedUser, setSelectedUser] = useState('');
  const [selectedRole, setSelectedRole] = useState('advisor');

  const handleAddRole = () => {
    if (selectedUser && selectedRole) {
      onAddRole(selectedUser, selectedRole);
      setSelectedUser('');
      setSelectedRole('advisor');
    }
  };

  return (
    <div>
      <MantineGroup position="apart" mb="md">
        <Text size="lg" fw={500}>
          Group Members
        </Text>

        <MantineGroup spacing="sm">
          <UserMultiSelect
            label=""
            placeholder="Select user"
            value={selectedUser ? [selectedUser] : []}
            onChange={(value) => setSelectedUser(value[0] || '')}
            maxSelectedValues={1}
          />

          <Select
            value={selectedRole}
            onChange={(value) => setSelectedRole(value || 'advisor')}
            data={[
              { value: 'admin', label: 'Admin' },
              { value: 'supervisor', label: 'Supervisor' },
              { value: 'advisor', label: 'Advisor' },
            ]}
            style={{ width: 120 }}
          />

          <Button
            onClick={handleAddRole}
            disabled={!selectedUser || !selectedRole}
            loading={isLoading}
          >
            Add Member
          </Button>
        </MantineGroup>
      </MantineGroup>

      <Table>
        <thead>
          <tr>
            <th>User</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {roles.map((role) => (
            <tr key={`${role.groupId}-${role.role}`}>
              <td>{role.groupId}</td>
              <td style={{ textTransform: 'capitalize' }}>{role.role}</td>
              <td>
                <Button
                  variant="subtle"
                  color="red"
                  compact
                  onClick={() => onRemoveRole(role.groupId)}
                  loading={isLoading}
                >
                  Remove
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}
