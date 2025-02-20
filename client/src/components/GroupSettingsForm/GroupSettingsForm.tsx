import { TextInput, Textarea, Stack, Button, ColorInput, FileInput } from '@mantine/core'
import { useForm } from '@mantine/form'
import { Group, GroupSettings } from '../../types/group'

interface GroupSettingsFormProps {
  /** Existing group data for editing, undefined for new group creation */
  group?: Group
  /** Callback function called when form is submitted */
  onSubmit: (values: GroupFormValues) => void
  /** Loading state for the submit button */
  isLoading?: boolean
}

interface GroupFormValues {
  name: string
  slug: string
  description: string
  websiteUrl: string
  settings: GroupSettings
  logo?: File
}

/**
 * A form component for creating or editing group settings.
 * Handles:
 * - Basic group information (name, slug, description)
 * - Group customization (colors, logo)
 * - Website URL
 * - Form validation and submission
 */
export function GroupSettingsForm({ group, onSubmit, isLoading }: GroupSettingsFormProps) {
  const form = useForm<GroupFormValues>({
    initialValues: {
      name: group?.name || '',
      slug: group?.slug || '',
      description: group?.description || '',
      websiteUrl: group?.websiteUrl || '',
      settings: group?.settings || {
        customization: {
          primaryColor: '#1971c2',
          secondaryColor: '#228be6',
        },
      },
      logo: undefined,
    },
    validate: {
      name: (value) => (value.length < 2 ? 'Name must be at least 2 characters' : null),
      slug: (value) => {
        if (!value) return 'Slug is required'
        if (!/^[a-z0-9-]+$/.test(value))
          return 'Slug can only contain lowercase letters, numbers, and hyphens'
        return null
      },
      websiteUrl: (value) => {
        if (!value) return null
        try {
          new URL(value)
          return null
        } catch {
          return 'Please enter a valid URL'
        }
      },
    },
  })

  return (
    <form onSubmit={form.onSubmit(onSubmit)}>
      <Stack gap='md'>
        <TextInput
          required
          label='Group Name'
          placeholder='Enter group name'
          {...form.getInputProps('name')}
        />

        <TextInput
          required
          label='Group Slug'
          placeholder='enter-group-slug'
          description='Used in URLs. Use lowercase letters, numbers, and hyphens only.'
          {...form.getInputProps('slug')}
        />

        <Textarea
          label='Description'
          placeholder='Enter group description'
          minRows={3}
          {...form.getInputProps('description')}
        />

        <TextInput
          label='Website URL'
          placeholder='https://example.com'
          {...form.getInputProps('websiteUrl')}
        />

        <ColorInput
          label='Primary Color'
          {...form.getInputProps('settings.customization.primaryColor')}
        />

        <ColorInput
          label='Secondary Color'
          {...form.getInputProps('settings.customization.secondaryColor')}
        />

        <FileInput
          label='Group Logo'
          accept='image/*'
          placeholder='Upload logo'
          onChange={(file) => form.setFieldValue('logo', file)}
        />

        <Button type='submit' loading={isLoading}>
          {group ? 'Update Group' : 'Create Group'}
        </Button>
      </Stack>
    </form>
  )
}

export default GroupSettingsForm
