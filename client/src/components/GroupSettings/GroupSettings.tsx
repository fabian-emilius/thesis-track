import React from 'react'
import { TextInput, Textarea, Stack, Button, ColorInput } from '@mantine/core'
import { useForm } from '@mantine/form'
import { Group, GroupSettings as GroupSettingsType } from '../../types/group'

interface GroupSettingsProps {
  group: Group
  onSave: (data: Partial<Group>) => Promise<void>
}

const GroupSettings: React.FC<GroupSettingsProps> = ({ group, onSave }) => {
  const form = useForm({
    initialValues: {
      name: group.name,
      description: group.description || '',
      websiteUrl: group.websiteUrl || '',
      settings: group.settings || {},
    },
  })

  const handleSubmit = async (values: typeof form.values) => {
    try {
      await onSave(values)
    } catch (error) {
      console.error('Failed to save group settings:', error)
    }
  }

  return (
    <form onSubmit={form.onSubmit(handleSubmit)}>
      <Stack gap='md'>
        <TextInput
          required
          label='Group Name'
          placeholder='Enter group name'
          {...form.getInputProps('name')}
        />

        <Textarea
          label='Description'
          placeholder='Enter group description'
          {...form.getInputProps('description')}
        />

        <TextInput
          label='Website URL'
          placeholder='https://example.com'
          {...form.getInputProps('websiteUrl')}
        />

        <ColorInput
          label='Primary Color'
          placeholder='Pick a color'
          {...form.getInputProps('settings.customization.primaryColor')}
        />

        <ColorInput
          label='Secondary Color'
          placeholder='Pick a color'
          {...form.getInputProps('settings.customization.secondaryColor')}
        />

        <Button type='submit'>Save Settings</Button>
      </Stack>
    </form>
  )
}

export default GroupSettings
