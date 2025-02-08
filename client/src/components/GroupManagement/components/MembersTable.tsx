import { Table, Button, Group, Text, LoadingOverlay } from '@mantine/core';
import { useState, useEffect } from 'react';
import { GroupMember, GroupRole } from '../../../types/group';
import { request } from '../../../requests/request';
import { useGroupPermissions } from '../../../hooks/group-permissions';
import { sanitizeHtml } from '../../../utils/security';
import { useNotifications } from '../../../hooks/notifications';
import { validateGroupId, validateMemberId } from '../../../utils/validation';
import { handleApiError } from '../../../utils/error-handling';
import { useCSRFToken } from '../../../hooks/csrf';

interface MembersTableProps {
  groupId: string;
}

export function MembersTable({ groupId }: MembersTableProps) {
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const { canManageGroup } = useGroupPermissions({ groupId });
  const { showError, showSuccess } = useNotifications();
  const csrfToken = useCSRFToken();

  useEffect(() => {
    const fetchMembers = async () => {
      if (!validateGroupId(groupId)) {
        showError('Invalid group ID');
        return;
      }

      try {
        setLoading(true);
        const response = await request<{ members: GroupMember[] }>(
          'GET',
          `/api/groups/${encodeURIComponent(groupId)}/members`,
          undefined,
          {
            headers: {
              'X-CSRF-Token': csrfToken
            }
          }
        );
        
        const sanitizedMembers = response.members.map(member => ({
          ...member,
          name: sanitizeHtml(member.name),
          email: sanitizeHtml(member.email)
        }));
        
        setMembers(sanitizedMembers);
      } catch (error) {
        const errorMessage = handleApiError(error);
        showError(`Failed to fetch members: ${errorMessage}`);
      } finally {
        setLoading(false);
      }
    };

    fetchMembers();
  }, [groupId, csrfToken, showError]);

  const handleRemoveMember = async (memberId: string) => {
    if (!validateMemberId(memberId)) {
      showError('Invalid member ID');
      return;
    }

    try {
      setActionLoading(true);
      await request(
        'DELETE',
        `/api/groups/${encodeURIComponent(groupId)}/members/${encodeURIComponent(memberId)}`,
        undefined,
        {
          headers: {
            'X-CSRF-Token': csrfToken
          }
        }
      );
      setMembers(members.filter((member) => member.id !== memberId));
      showSuccess('Member removed successfully');
    } catch (error) {
      const errorMessage = handleApiError(error);
      showError(`Failed to remove member: ${errorMessage}`);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <Text>Loading members...</Text>;

  return (
    <div style={{ position: 'relative' }}>
      <LoadingOverlay visible={actionLoading} />
      <Table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            {canManageGroup && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {members.map((member) => (
            <tr key={member.id}>
              <td>{member.name}</td>
              <td>{member.email}</td>
              <td>{member.role}</td>
              {canManageGroup && (
                <td>
                  <Group spacing="xs">
                    <Button
                      size="xs"
                      variant="outline"
                      color="red"
                      onClick={() => handleRemoveMember(member.id)}
                      disabled={actionLoading}
                    >
                      Remove
                    </Button>
                  </Group>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}