import { Table, Select, Button, Group, Text, LoadingOverlay } from '@mantine/core';
import { useState } from 'react';
import { GroupMember, GroupRole } from '../../types/group';
import { useGroupContext } from '../../providers/GroupContext/context';
import { notifications } from '@mantine/notifications';

interface GroupRoleManagementProps {
  /** The ID of the group being managed */
  groupId: string;
  /** List of current group members */
  members: GroupMember[];
  /** Callback fired when a member's role is updated */
  onUpdateRole: (userId: string, role: GroupRole) => Promise<void>;
  /** Callback fired when a member is removed from the group */
  onRemoveMember: (userId: string) => Promise<void>;
}

const ROLE_OPTIONS = [
  { value: 'admin', label: 'Admin' },
  { value: 'supervisor', label: 'Supervisor' },
  { value: 'advisor', label: 'Advisor' },
] as const;

/**
 * Component for managing group member roles and permissions
 * Provides interface for updating roles and removing members
 */
export function GroupRoleManagement({
  groupId,
  members,
  onUpdateRole,
  onRemoveMember,
}: GroupRoleManagementProps) {
  const { isGroupAdmin, isLoading } = useGroupContext();
  const [updatingUser, setUpdatingUser] = useState<string>();
  const [removingUser, setRemovingUser] = useState<string>();

  if (!isGroupAdmin(groupId)) {
    return <Text>You don't have permission to manage group roles.</Text>;
  }

  const handleRoleUpdate = async (userId: string, role: GroupRole) => {
    try {
      setUpdatingUser(userId);
      await onUpdateRole(userId, role);
      notifications.show({
        title: 'Success',
        message: 'Member role updated successfully',
        color: 'green',
      });
    } catch (error) {
      console.error('Failed to update role:', error);
      notifications.show({
        title: 'Error',
        message: 'Failed to update member role. Please try again.',
        color: 'red',
      });
    } finally {
      setUpdatingUser(undefined);
    }
  };

  const handleRemoveMember = async (userId: string) => {
    try {
      setRemovingUser(userId);
      await onRemoveMember(userId);
      notifications.show({
        title: 'Success',
        message: 'Member removed successfully',
        color: 'green',
      });
    } catch (error) {
      console.error('Failed to remove member:', error);
      notifications.show({
        title: 'Error',
        message: 'Failed to remove member. Please try again.',
        color: 'red',
      });
    } finally {
      setRemovingUser(undefined);
    }
  };

  return (
    <div style={{ position: 'relative' }}>
      <LoadingOverlay visible={isLoading} />
      <Table>
        <thead>
          <tr>
            <th>User</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {members.map((member) => (
            <tr key={member.userId}>
              <td>{member.userId}</td>
              <td>
                <Select
                  value={member.role}
                  onChange={(value: GroupRole) => value && handleRoleUpdate(member.userId, value)}
                  data={ROLE_OPTIONS}
                  disabled={updatingUser === member.userId || isLoading}
                />
              </td>
              <td>
                <Group gap="xs">
                  <Button
                    variant="subtle"
                    color="red"
                    onClick={() => handleRemoveMember(member.userId)}
                    loading={removingUser === member.userId}
                    disabled={isLoading}
                  >
                    Remove
                  </Button>
                </Group>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}
