import { Group, Stack, Text, Divider, Title } from '@mantine/core'
import { usePageTitle } from '../../../../hooks/theme'
import {
  useLoggedInUser,
  useManagementAccess,
  useUserGroups,
} from '../../../../hooks/authentication'
import ThesesTable from '../../../../components/ThesesTable/ThesesTable'
import ThesesProvider from '../../../../providers/ThesesProvider/ThesesProvider'
import React, { useEffect, useState } from 'react'
import { doRequest } from '../../../../requests/request'
import PageLoader from '../../../../components/PageLoader/PageLoader'
import NotificationToggleSwitch from './components/NotificationToggleSwitch/NotificationToggleSwitch'

interface NotificationSetting {
  name: string
  email: string
  groupId?: string
}

const NotificationSettings = () => {
  usePageTitle('Notification Settings')

  const user = useLoggedInUser()
  const managementAccess = useManagementAccess()
  const userGroups = useUserGroups()

  const [settings, setSettings] = useState<NotificationSetting[]>()

  useEffect(() => {
    setSettings(undefined)

    return doRequest<NotificationSetting[]>(
      '/v2/user-info/notifications',
      {
        method: 'GET',
        requiresAuth: true,
      },
      (res) => {
        if (res.ok) {
          setSettings(res.data)
        } else {
          setSettings([])
        }
      },
    )
  }, [user.userId])

  if (!settings) {
    return <PageLoader />
  }

  return (
    <Stack>
      <Title order={3}>General Notifications</Title>
      {managementAccess && (
        <Stack>
          <Group>
            <Stack gap={2}>
              <Text size='sm'>New Applications</Text>
              <Text size='xs' c='dimmed'>
                Receive a summary email on every new application
              </Text>
            </Stack>
            <NotificationToggleSwitch
              name='new-applications'
              settings={settings}
              setSettings={setSettings}
              ml='auto'
            />
          </Group>
          <Group>
            <Stack gap={2}>
              <Text size='sm'>Application Review Reminder</Text>
              <Text size='xs' c='dimmed'>
                Receive a weekly reminder email if you have unreviewed applications
              </Text>
            </Stack>
            <NotificationToggleSwitch
              name='unreviewed-application-reminder'
              settings={settings}
              setSettings={setSettings}
              ml='auto'
            />
          </Group>
        </Stack>
      )}
      <Group>
        <Stack gap={2}>
          <Text size='sm'>Presentation Invitations</Text>
          <Text size='xs' c='dimmed'>
            Get invitations to public thesis presentations when scheduled
          </Text>
        </Stack>
        <NotificationToggleSwitch
          name='presentation-invitations'
          settings={settings}
          setSettings={setSettings}
          ml='auto'
        />
      </Group>
      <Group>
        <Stack gap={2}>
          <Text size='sm'>Thesis Comments</Text>
          <Text size='xs' c='dimmed'>
            Receive an email for every comment that is added to a thesis assigned to you
          </Text>
        </Stack>
        <NotificationToggleSwitch
          name='thesis-comments'
          settings={settings}
          setSettings={setSettings}
          ml='auto'
        />
      </Group>

      {userGroups.map((group) => (
        <React.Fragment key={group.id}>
          <Divider my='md' />
          <Title order={3}>{group.name} Notifications</Title>
          <Group>
            <Stack gap={2}>
              <Text size='sm'>Group Events</Text>
              <Text size='xs' c='dimmed'>
                Receive notifications about meetings, deadlines, and group activities
              </Text>
            </Stack>
            <NotificationToggleSwitch
              name={`group-events-${group.id}`}
              settings={settings}
              setSettings={setSettings}
              ml='auto'
            />
          </Group>
          <Group>
            <Stack gap={2}>
              <Text size='sm'>Member Updates</Text>
              <Text size='xs' c='dimmed'>
                Get notified when members join or leave the group
              </Text>
            </Stack>
            <NotificationToggleSwitch
              name={`member-updates-${group.id}`}
              settings={settings}
              setSettings={setSettings}
              ml='auto'
            />
          </Group>
          <Group>
            <Stack gap={2}>
              <Text size='sm'>Group Announcements</Text>
              <Text size='xs' c='dimmed'>
                Receive important announcements and updates from group administrators
              </Text>
            </Stack>
            <NotificationToggleSwitch
              name={`group-announcements-${group.id}`}
              settings={settings}
              setSettings={setSettings}
              ml='auto'
            />
          </Group>
        </React.Fragment>
      ))}

      <Divider my='md' />
      <Title order={3}>Thesis-Specific Notifications</Title>
      <ThesesProvider limit={10}>
        <ThesesTable
          columns={['title', 'type', 'students', 'advisors', 'supervisors', 'actions']}
          extraColumns={{
            actions: {
              accessor: 'actions',
              title: 'Notifications',
              textAlign: 'center',
              noWrap: true,
              width: 120,
              render: (thesis) => (
                <Group
                  preventGrowOverflow={false}
                  justify='center'
                  onClick={(e) => e.stopPropagation()}
                >
                  <NotificationToggleSwitch
                    name={`thesis-${thesis.thesisId}`}
                    settings={settings}
                    setSettings={setSettings}
                  />
                </Group>
              ),
            },
          }}
        />
      </ThesesProvider>
    </Stack>
  )
}

export default NotificationSettings
