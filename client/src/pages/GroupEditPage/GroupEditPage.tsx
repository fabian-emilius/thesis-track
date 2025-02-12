import { Container, Title, Tabs, LoadingOverlay, Alert } from '@mantine/core';
import { useParams, Navigate } from 'react-router-dom';
import { notifications } from '@mantine/notifications';
import { IconAlertCircle } from '@tabler/icons-react';
import { BasicInformationForm } from './forms/BasicInformationForm';
import { LogoUploadForm } from './forms/LogoUploadForm';
import { EmailTemplateForm } from './forms/EmailTemplateForm';
import { AccessManagementForm } from './forms/AccessManagementForm';
import { useGroup } from '../../hooks/useGroup';
import { useUpdateGroup } from '../../hooks/useUpdateGroup';
import { useGroupMembers } from '../../hooks/useGroupMembers';

export function GroupEditPage() {
  const { groupSlug } = useParams();
  const { data: group, isLoading: isGroupLoading, error: groupError } = useGroup(groupSlug);
  const { data: members, isLoading: isMembersLoading } = useGroupMembers(groupSlug);
  const { mutate: updateGroup, isLoading: isUpdating } = useUpdateGroup();
  const [activeTab, setActiveTab] = useState<string | null>('basic');

  if (groupError) {
    return (
      <Alert icon={<IconAlertCircle size="1rem" />} title="Error" color="red">
        Failed to load group information. Please try again later.
      </Alert>
    );
  }

  if (!group) {
    return isGroupLoading ? <LoadingOverlay visible /> : <Navigate to="/groups" />;
  }

  if (!group.isAdmin) {
    return <Navigate to={`/groups/${groupSlug}`} />;
  }

  return (
    <Container size="xl">
      <Title order={2} mb="xl">Edit Group Settings</Title>

      <LoadingOverlay visible={isGroupLoading || isMembersLoading || isUpdating} />
      <Tabs value={activeTab} onChange={setActiveTab}>
        <Tabs.List>
          <Tabs.Tab value="basic">Basic Information</Tabs.Tab>
          <Tabs.Tab value="logo">Logo</Tabs.Tab>
          <Tabs.Tab value="email">Email Templates</Tabs.Tab>
          <Tabs.Tab value="access">Access Management</Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="basic">
          <BasicInformationForm
            initialData={group}
            onSubmit={(data) => {
              updateGroup(
                { groupId: group.id, data },
                {
                  onSuccess: () => {
                    notifications.show({
                      title: 'Success',
                      message: 'Group information updated successfully',
                      color: 'green'
                    });
                  },
                  onError: () => {
                    notifications.show({
                      title: 'Error',
                      message: 'Failed to update group information',
                      color: 'red'
                    });
                  }
                }
              );
            }}
          />
        </Tabs.Panel>

        <Tabs.Panel value="logo">
          <LogoUploadForm
            groupId={group.id}
            currentLogo={group.logo}
            onSuccess={async () => {
              await refreshGroup();
              notifications.show({
                title: 'Success',
                message: 'Group logo updated successfully',
                color: 'green'
              });
            }}
            onError={() => {
              notifications.show({
                title: 'Error',
                message: 'Failed to update group logo',
                color: 'red'
              });
            }}
          />
        </Tabs.Panel>

        <Tabs.Panel value="email">
          <EmailTemplateForm
            groupId={group.id}
            templates={group.emailTemplates}
            onSubmit={(templates) => {
              updateGroup(
                { groupId: group.id, data: { emailTemplates: templates } },
                {
                  onSuccess: () => {
                    notifications.show({
                      title: 'Success',
                      message: 'Email templates updated successfully',
                      color: 'green'
                    });
                  },
                  onError: () => {
                    notifications.show({
                      title: 'Error',
                      message: 'Failed to update email templates',
                      color: 'red'
                    });
                  }
                }
              );
            }}
          />
        </Tabs.Panel>

        <Tabs.Panel value="access">
          <AccessManagementForm
            groupId={group.id}
            members={members}
            onUpdate={async () => {
              await refreshGroup();
              notifications.show({
                title: 'Success',
                message: 'Access permissions updated successfully',
                color: 'green'
              });
            }}
            onError={() => {
              notifications.show({
                title: 'Error',
                message: 'Failed to update access permissions',
                color: 'red'
              });
            }}
          />
        </Tabs.Panel>
      </Tabs>
    </Container>
  );
}
