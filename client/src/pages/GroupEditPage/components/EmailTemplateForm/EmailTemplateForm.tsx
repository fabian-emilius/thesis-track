import { Textarea, Button, Stack } from '@mantine/core'
import { useForm } from '@mantine/form'
import { Group } from '../../../../types/group'

interface EmailTemplateFormProps {
  group: Group
  onSubmit: (values: Partial<Group>) => void
}

export function EmailTemplateForm({ group, onSubmit }: EmailTemplateFormProps) {
  const form = useForm({
    initialValues: {
      mailFooter: group.mailFooter || '',
      acceptanceText: group.acceptanceText || '',
    },
  })

  return (
    <form onSubmit={form.onSubmit(onSubmit)}>
      <Stack>
        <Textarea
          label='Email Footer'
          description='This text will be added to all automated emails'
          placeholder='Enter email footer text'
          minRows={3}
          {...form.getInputProps('mailFooter')}
        />

        <Textarea
          label='Acceptance Email Text'
          description='Custom text for thesis acceptance emails'
          placeholder='Enter acceptance email text'
          minRows={3}
          {...form.getInputProps('acceptanceText')}
        />

        <Button type='submit'>Save Changes</Button>
      </Stack>
    </form>
  )
}
