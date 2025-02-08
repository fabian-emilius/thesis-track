import { useGroup } from '../providers/GroupProvider';
import { GroupRole } from '../types/group';

interface UseGroupPermissionsProps {
  groupId?: string;
}

export function useGroupPermissions({ groupId }: UseGroupPermissionsProps = {}) {
  const { currentGroup, groups } = useGroup();

  const targetGroup = groupId
    ? groups.find((g) => g.id === groupId)
    : currentGroup;

  const isGroupMember = (userId: string): boolean => {
    if (!targetGroup) return false;
    return targetGroup.members?.some((member) => member.userId === userId);
  };

  const hasGroupRole = (userId: string, role: GroupRole): boolean => {
    if (!targetGroup) return false;
    return targetGroup.members?.some(
      (member) => member.userId === userId && member.role === role
    );
  };

  const canManageGroup = (userId: string): boolean => {
    if (!targetGroup) return false;
    return targetGroup.members?.some(
      (member) =>
        member.userId === userId &&
        [GroupRole.ADMIN, GroupRole.SUPERVISOR].includes(member.role)
    );
  };

  const canViewGroupContent = (userId: string): boolean => {
    if (!targetGroup) return false;
    return isGroupMember(userId) || targetGroup.members?.some(
      (member) => member.role === GroupRole.ADMIN
    );
  };

  const canCreateTopic = (userId: string): boolean => {
    if (!targetGroup) return false;
    return targetGroup.members?.some(
      (member) =>
        member.userId === userId &&
        [GroupRole.ADMIN, GroupRole.SUPERVISOR, GroupRole.ADVISOR].includes(
          member.role
        )
    );
  };

  return {
    isGroupMember,
    hasGroupRole,
    canManageGroup,
    canViewGroupContent,
    canCreateTopic,
  };
}
