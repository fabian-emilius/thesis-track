```typescript
import { Box, Title, Tabs } from '@mantine/core';
import { useState } from 'react';
import { MembersTable } from './components/MembersTable';
import { GroupSettingsForm } from './components/GroupSettingsForm';
import { useGroupPermissions } from '../../hooks/group-permissions';
import { useGroup } from '../../providers/GroupProvider';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { useGroupManagement } from '../../hooks/useGroupManagement';

interface TabContentProps {
  groupId: string;
}

const MembersTabContent = ({ groupId }: TabContentProps) => (
  <ErrorBoundary fallback={<div>Error loading members table</div>}>
    <MembersTable groupId={groupId} />
  </ErrorBoundary>
);

const SettingsTabContent = ({ groupId }: TabContentProps) => (
  <ErrorBoundary fallback={<div>Error loading settings form</div>}>
    <GroupSettingsForm groupId={groupId} />
  </ErrorBoundary>
);

interface TabConfig {
  value: string;
  label: string;
  component: React.FC<TabContentProps>;
}

const TAB_CONFIG: TabConfig[] = [
  {
    value: 'members',
    label: 'Members',
    component: MembersTabContent
  },
  {
    value: 'settings',
    label: 'Settings',
    component: SettingsTabContent
  }
];

export function GroupManagement() {
  const [activeTab, setActiveTab] = useState<string>('members');
  const { currentGroup } = useGroup();
  const { canManageGroup } = useGroupPermissions();
  const { isLoading, error } = useGroupManagement(currentGroup?.id);

  if (!currentGroup) return null;
  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  const ActiveTabComponent = TAB_CONFIG.find(tab => tab.value === activeTab)?.component;

  return (
    <Box>
      <Title order={2} mb="xl">
        Group Management
      </Title>

      <ErrorBoundary fallback={<div>Error in group management interface</div>}>
        <Tabs value={activeTab} onChange={(value) => setActiveTab(value || 'members')}>
          <Tabs.List>
            {TAB_CONFIG.map(tab => (
              <Tabs.Tab key={tab.value} value={tab.value}>
                {tab.label}
              </Tabs.Tab>
            ))}
          </Tabs.List>

          {ActiveTabComponent && (
            <Tabs.Panel value={activeTab} pt="xl">
              <ActiveTabComponent groupId={currentGroup.id} />
            </Tabs.Panel>
          )}
        </Tabs>
      </ErrorBoundary>
    </Box>
  );
}
```