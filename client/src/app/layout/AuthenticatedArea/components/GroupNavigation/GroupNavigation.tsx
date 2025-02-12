import { NavLink, Menu, Button, Text } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { useGroupContext } from '../../../../../providers/GroupContext/context';
import { useAuthContext } from '../../../../../providers/AuthenticationContext/context';

export function GroupNavigation() {
  const navigate = useNavigate();
  const { currentGroup, isGroupAdmin, isSupervisor, isAdvisor } = useGroupContext();
  const { isAdmin } = useAuthContext();

  if (!currentGroup) {
    return null;
  }

  const hasManagementAccess = isAdmin || isGroupAdmin(currentGroup.id);
  const hasAdvisorAccess = hasManagementAccess || isSupervisor(currentGroup.id) || isAdvisor(currentGroup.id);

  return (
    <>
      <Text size="sm" fw={500} c="dimmed" mb="xs">
        {currentGroup.name}
      </Text>

      <NavLink
        label="Dashboard"
        component="a"
        href={`/groups/${currentGroup.slug}/dashboard`}
      />

      {hasAdvisorAccess && (
        <>
          <NavLink
            label="Topics"
            component="a"
            href={`/groups/${currentGroup.slug}/topics`}
          />
          <NavLink
            label="Applications"
            component="a"
            href={`/groups/${currentGroup.slug}/applications`}
          />
        </>
      )}

      {hasManagementAccess && (
        <NavLink
          label="Group Settings"
          component="a"
          href={`/groups/${currentGroup.slug}/edit`}
        />
      )}

      <Menu position="bottom-start">
        <Menu.Target>
          <Button variant="subtle" size="sm" mt="md">
            Switch Group
          </Button>
        </Menu.Target>
        <Menu.Dropdown>
          <Menu.Item onClick={() => navigate('/groups')}>
            Select Another Group
          </Menu.Item>
        </Menu.Dropdown>
      </Menu>
    </>
  );
}
