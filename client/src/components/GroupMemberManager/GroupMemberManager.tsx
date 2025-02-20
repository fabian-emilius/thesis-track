import { useState } from 'react';
import { Stack, Title, Table, Group, Select, Text, LoadingOverlay } from '@mantine/core';
import { IconTrash } from '@tabler/icons-react';
import { useGroupsContext } from '../../providers/GroupsProvider/hooks';
import { GroupMember } from '../../requests/responses/group';
import { UserMultiSelect } from '../UserMultiSelect/UserMultiSelect';
import { notifications } from '@mantine/notifications';
import ConfirmationButton from '../ConfirmationButton/ConfirmationButton';
import { AvatarUser } from '../AvatarUser/AvatarUser';

type MemberAction = {
  type: 'add' | 'remove' | 'update';
  userId: string;
  role?: GroupMember['role'];
};

type OptimisticUpdate = {
  action: MemberAction;
  timestamp: number;
};

/**
 * Props for the GroupMemberManager component
 */
interface GroupMemberManagerProps {
  /** The ID of the group to manage members for */
  groupId: string;
  /** List of current group members */
  members: GroupMember[];
  /** Optional callback when members are updated */
  onMembersChange?: (members: GroupMember[]) => void;
}

/**
 * Component for managing group members and their roles
 * Handles adding, removing, and updating member roles with optimistic updates
 */
export function GroupMemberManager({ 
  groupId, 
  members,
  onMembersChange 
}: GroupMemberManagerProps) {
  const { addGroupMember, removeGroupMember } = useGroupsContext();
  const [loading, setLoading] = useState(false);
  const [optimisticUpdates, setOptimisticUpdates] = useState<OptimisticUpdate[]>([]);

  // Apply optimistic updates to members list
  const optimisticMembers = members
    .filter(member => 
      !optimisticUpdates.find(update => 
        update.action.type === 'remove' && 
        update.action.userId === member.userId
      )
    )
    .map(member => {
      const update = optimisticUpdates
        .filter(update => update.action.userId === member.userId)
        .sort((a, b) => b.timestamp - a.timestamp)[0];
      
      if (!update || update.action.type === 'remove') return member;
      return { ...member, role: update.action.role! };
    })
    .sort((a, b) => a.userId.localeCompare(b.userId));

  const applyOptimisticUpdate = (action: MemberAction) => {
    setOptimisticUpdates(prev => [...prev, { action, timestamp: Date.now() }]);
  };

  const removeOptimisticUpdate = (userId: string) => {
    setOptimisticUpdates(prev => prev.filter(update => update.action.userId !== userId));
  };

  const handleAddMember = async (userId: string, role: GroupMember['role']) => {
    applyOptimisticUpdate({ type: 'add', userId, role });
    setLoading(true);
    try {
      await addGroupMember(groupId, userId, role);
      notifications.show({
        title: 'Success',
        message: 'Member added successfully',
        color: 'green',
      });
      onMembersChange?.([...members, { userId, groupId, role }]);
    } catch (err) {
      notifications.show({
        title: 'Error',
        message: 'Failed to add member',
        color: 'red',
      });
    } finally {
      removeOptimisticUpdate(userId);
      setLoading(false);
    }
  };

  const handleRemoveMember = async (userId: string) => {
    applyOptimisticUpdate({ type: 'remove', userId });
    setLoading(true);
    try {
      await removeGroupMember(groupId, userId);
      notifications.show({
        title: 'Success',
        message: 'Member removed successfully',
        color: 'green',
      });
      onMembersChange?.(members.filter(m => m.userId !== userId));
    } catch (err) {
      notifications.show({
        title: 'Error',
        message: 'Failed to remove member',
        color: 'red',
      });
    } finally {
      removeOptimisticUpdate(userId);
      setLoading(false);
    }
  };

  const handleUpdateRole = async (userId: string, role: GroupMember['role']) => {
    const previousRole = members.find(m => m.userId === userId)?.role;
    if (previousRole === role) return;
    
    applyOptimisticUpdate({ type: 'update', userId, role });
    setLoading(true);
    try {
      await addGroupMember(groupId, userId, role);
      notifications.show({
        title: 'Success',
        message: 'Member role updated successfully',
        color: 'green',
      });
      onMembersChange?.(
        members.map(m => 
          m.userId === userId 
            ? { ...m, role } 
            : m
        )
      );
    } catch (err) {
      notifications.show({
        title: 'Error',
        message: 'Failed to update member role',
        color: 'red',
      });
    } finally {
      removeOptimisticUpdate(userId);
      setLoading(false);
    }
  };

  return (
    <Stack gap="md" pos="relative">
      <LoadingOverlay visible={loading} />
      <Title order={2}>Group Members</Title>

      <Group align="flex-end" grow>
        <UserMultiSelect
          label="Add Member"
          placeholder="Select user"
          onChange={(userId) => handleAddMember(userId, 'advisor')}
          disabled={loading}
          excludeUsers={members.map(m => m.userId)}
        />
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
          {optimisticMembers.map((member) => (
            <Table.Tr key={member.userId}>
              <Table.Td>
                <AvatarUser userId={member.userId} withName />
              </Table.Td>
              <Table.Td>
                <Select
                  value={member.role}
                  onChange={(role: GroupMember['role']) =>
                    handleUpdateRole(member.userId, role)
                  }
                  data={[
                    { value: 'supervisor', label: 'Supervisor' },
                    { value: 'advisor', label: 'Advisor' },
                    { value: 'group_admin', label: 'Group Admin' },
                  ]}
                  disabled={loading}
                />
              </Table.Td>
              <Table.Td>
                <ConfirmationButton
                  variant="subtle"
                  color="red"
                  size="sm"
                  confirmationTitle="Remove Member"
                  confirmationText="Are you sure you want to remove this member from the group? This action cannot be undone."
                  onClick={() => handleRemoveMember(member.userId)}
                  disabled={loading}
                >
                  <IconTrash size={16} />
                </ConfirmationButton>
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </Stack>
  );
}