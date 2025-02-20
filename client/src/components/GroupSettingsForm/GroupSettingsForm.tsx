import React from 'react'
import { TextInput, Textarea, Button, Stack, Group } from '@mantine/core'
import { useForm } from '@mantine/form'
import {
  IGroup,
  IGroupSettings,
  IUpdateGroupSettingsPayload,
} from '../../providers/GroupContext/types'
import { EmailTemplateEditor } from '../EmailTemplateEditor/EmailTemplateEditor'
import { showSimpleSuccess, showSimpleError } from '../../utils/notification'

export interface GroupSettingsFormProps {
  group?: IGroup
  settings?: IGroupSettings
  onSubmit: (values: IUpdateGroupSettingsPayload & Partial<IGroup>) => Promise<void>
  isLoading?: boolean
}

export const GroupSettingsForm: React.FC<GroupSettingsFormProps> = ({
  group,
  settings,
  onSubmit,
  isLoading,
}) => {
  const form = useForm<IUpdateGroupSettingsPayload & Partial<IGroup>>({
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
    <form
      onSubmit={form.onSubmit(async (values) => {
        try {
          await onSubmit(values)
          showSimpleSuccess('Group settings updated successfully')
        } catch (error) {
          showSimpleError('Failed to update group settings')
        }
      })}
    >
      <Stack>
        <TextInput
          required
          label='Group Name'
          placeholder='Enter group name'
          {...form.getInputProps('name')}
        />

        <Textarea
          required
          label='Description'
          placeholder='Enter group description'
          minRows={3}
          {...form.getInputProps('description')}
        />

        <TextInput
          label='External Link'
          placeholder='https://example.com'
          {...form.getInputProps('externalLink')}
        />

        <EmailTemplateEditor
          label='Acceptance Email Template'
          placeholder='Enter email template'
          value={form.values.acceptanceEmailTemplate}
          onChange={(value) => form.setFieldValue('acceptanceEmailTemplate', value)}
          description="Template used when accepting a student's application"
          variables={[
            { name: 'studentName', description: 'Name of the accepted student' },
            { name: 'topicTitle', description: 'Title of the accepted thesis topic' },
            { name: 'advisorName', description: 'Name of the thesis advisor' },
          ]}
        />

        <EmailTemplateEditor
          label='Post-Acceptance Instructions'
          placeholder='Enter instructions'
          value={form.values.postAcceptanceInstructions}
          onChange={(value) => form.setFieldValue('postAcceptanceInstructions', value)}
          description='Instructions sent to students after their application is accepted'
          variables={[
            { name: 'studentName', description: 'Name of the student' },
            { name: 'groupName', description: 'Name of the research group' },
            { name: 'nextSteps', description: 'Next steps in the process' },
          ]}
        />

        <EmailTemplateEditor
          label='Email Footer'
          placeholder='Enter email footer'
          value={form.values.emailFooter}
          onChange={(value) => form.setFieldValue('emailFooter', value)}
          description='Footer appended to all emails sent by the group'
          variables={[
            { name: 'groupName', description: 'Name of the research group' },
            { name: 'contactEmail', description: 'Contact email address' },
          ]}
        />

        <Group justify='flex-end'>
          <Button type='submit' loading={isLoading}>
            Save Changes
          </Button>
        </Group>
      </Stack>
    </form>
  )
}
