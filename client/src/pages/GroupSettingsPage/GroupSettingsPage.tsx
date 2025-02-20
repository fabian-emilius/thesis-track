import React, { useEffect, useState } from 'react'
import { Container, Title, Tabs, Stack, LoadingOverlay, Alert, Text } from '@mantine/core'
import { useParams, useNavigate } from 'react-router-dom'
import { GroupSettingsForm } from '../../components/GroupSettingsForm/GroupSettingsForm'
import { GroupMemberManager } from '../../components/GroupMemberManager/GroupMemberManager'
import { useGroupContext } from '../../providers/GroupContext/hooks'
import { showSimpleSuccess, showSimpleError } from '../../utils/notification'
import { doRequest } from '../../requests/request'
import { IconAlertCircle } from '@tabler/icons-react'

interface GroupSettings {
  acceptanceEmailTemplate: string
  postAcceptanceInstructions: string
  emailFooter: string
}

interface GroupUpdate {
  name: string
  description: string
  externalLink: string
}

interface GroupMember {
  user: { id: string; name: string }
  role: 'supervisor' | 'advisor' | 'group_admin'
}

export const GroupSettingsPage: React.FC = () => {
  const { groupSlug } = useParams<{ groupSlug: string }>()
  const { groups, updateGroup, updateGroupSettings } = useGroupContext()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [settings, setSettings] = useState<GroupSettings | null>(null)
  const [members, setMembers] = useState<GroupMember[]>([])
  const [isInitialLoading, setIsInitialLoading] = useState(true)

  const group = groups.find(g => g.slug === groupSlug)

  useEffect(() => {
    async function loadData() {
      if (!group) return

      try {
        setError(null)
        const [settingsResponse, membersResponse] = await Promise.all([
          doRequest(`/v2/groups/${group.id}/settings`, {
            method: 'GET',
            requiresAuth: true,
          }),
          doRequest<GroupMember[]>(`/v2/groups/${group.id}/members`, {
            method: 'GET',
            requiresAuth: true,
          })
        ])

        if (settingsResponse.ok && membersResponse.ok) {
          setSettings(settingsResponse.data)
          setMembers(membersResponse.data)
        } else {
          throw new Error('Failed to load group data')
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An unexpected error occurred')
        showSimpleError('Failed to load group settings')
      } finally {
        setIsInitialLoading(false)
      }
    }

    void loadData()
  }, [group])

  const handleSubmit = async (values: GroupSettings & GroupUpdate) => {
    if (!group) return

    try {
      setLoading(true)

      const groupUpdate: GroupUpdate = {
        name: values.name,
        description: values.description,
        externalLink: values.externalLink,
      }

      const settingsUpdate: GroupSettings = {
        acceptanceEmailTemplate: values.acceptanceEmailTemplate,
        postAcceptanceInstructions: values.postAcceptanceInstructions,
        emailFooter: values.emailFooter,
      }

      await Promise.all([
        updateGroup(group.id, groupUpdate),
        updateGroupSettings(group.id, settingsUpdate),
      ])

      showSimpleSuccess('Group settings updated successfully')
      setError(null)
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to update group settings'
      setError(errorMessage)
      showSimpleError(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  if (!group) {
    return (
      <Container size="xl">
        <Alert icon={<IconAlertCircle size={16} />} title="Not Found" color="red">
          <Text>The requested group could not be found. It may have been deleted or you may not have access.</Text>
        </Alert>
      </Container>
    )
  }

  if (isInitialLoading) {
    return (
      <Container size="xl" pos="relative">
        <LoadingOverlay visible={true} />
      </Container>
    )
  }

  return (
    <Container size="xl" pos="relative">
      <LoadingOverlay visible={loading} />
      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red" mb="xl">
          {error}
        </Alert>
      )}

      <Stack gap="xl">
        <Title order={1}>{group.name} Settings</Title>

        <Tabs defaultValue="general">
          <Tabs.List>
            <Tabs.Tab value="general">General</Tabs.Tab>
            <Tabs.Tab value="members">Members</Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="general" pt="xl">
            <GroupSettingsForm
              group={group}
              settings={settings}
              onSubmit={handleSubmit}
              isLoading={loading}
            />
          </Tabs.Panel>

          <Tabs.Panel value="members" pt="xl">
            <GroupMemberManager
              groupId={group.id}
              members={members}
            />
          </Tabs.Panel>
        </Tabs>
      </Stack>
    </Container>
  )
}

export default GroupSettingsPage
