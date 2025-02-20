import React from 'react'
import { TextInput, Textarea, Button, Stack, Group } from '@mantine/core'
import { useForm } from '@mantine/form'
import { IGroup, IGroupSettings } from '../../providers/GroupContext/types'
import { EmailTemplateEditor } from '../EmailTemplateEditor/EmailTemplateEditor'

interface GroupSettingsFormProps {
  group?: IGroup
  settings?: IGroupSettings
  onSubmit: (values: Partial<IGroup & IGroupSettings>) => Promise<void>
  isLoading?: boolean
}

export const GroupSettingsForm: React.FC<GroupSettingsFormProps> = ({
  group,
  settings,
  onSubmit,
  isLoading,
}) => {
  const form = useForm({
    initialValues: {
      name: group?.name || '',
      description: group?.description || '',
      externalLink: group?.externalLink || '',
      acceptanceEmailTemplate: settings?.acceptanceEmailTemplate || '',
      postAcceptanceInstructions: settings?.postAcceptanceInstructions || '',
      emailFooter: settings?.emailFooter || '',
    },
  })

  return (
    <form onSubmit={form.onSubmit((values) => onSubmit(values))}>
      <Stack>
        <TextInput
          required
          label="Group Name"
          placeholder="Enter group name"
          {...form.getInputProps('name')}
        />

        <Textarea
          required
          label="Description"
          placeholder="Enter group description"
          minRows={3}
          {...form.getInputProps('description')}
        />

        <TextInput
          label="External Link"
          placeholder="https://example.com"
          {...form.getInputProps('externalLink')}
        />

        <EmailTemplateEditor
          label="Acceptance Email Template"
          placeholder="Enter email template"
          variables={[
            { name: 'studentName', description: 'Name of the accepted student' },
            { name: 'topicTitle', description: 'Title of the accepted thesis topic' },
            { name: 'advisorName', description: 'Name of the thesis advisor' }
          ]}
          {...form.getInputProps('acceptanceEmailTemplate')}
        />

        <EmailTemplateEditor
          label="Post-Acceptance Instructions"
          placeholder="Enter instructions"
          variables={[
            { name: 'studentName', description: 'Name of the student' },
            { name: 'groupName', description: 'Name of the research group' },
            { name: 'nextSteps', description: 'Next steps in the process' }
          ]}
          {...form.getInputProps('postAcceptanceInstructions')}
        />

        <EmailTemplateEditor
          label="Email Footer"
          placeholder="Enter email footer"
          variables={[
            { name: 'groupName', description: 'Name of the research group' },
            { name: 'contactEmail', description: 'Contact email address' }
          ]}
          {...form.getInputProps('emailFooter')}
        />

        <Group justify="flex-end">
          <Button type="submit" loading={isLoading}>
            Save Changes
          </Button>
        </Group>
      </Stack>
    </form>
  )
}
