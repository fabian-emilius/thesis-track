import React, { useState } from 'react'
import { Container, Title, TextInput, Textarea, Button, Stack } from '@mantine/core'
import { useForm } from '@mantine/form'
import { useNavigate } from 'react-router-dom'
import { useGroups } from '../../providers/GroupsProvider/hooks'
import GroupLogoUpload from '../../components/GroupLogoUpload/GroupLogoUpload'

interface GroupFormValues {
  name: string
  slug: string
  description: string
  websiteLink?: string
  mailFooter: string
  acceptanceEmailText: string
  acceptanceInstructions: string
}

const CreateGroupPage: React.FC = () => {
  const navigate = useNavigate()
  const { createGroup } = useGroups()
  const [logo, setLogo] = useState<File | null>(null)

  const form = useForm<GroupFormValues>({
    initialValues: {
      name: '',
      slug: '',
      description: '',
      websiteLink: '',
      mailFooter: '',
      acceptanceEmailText: '',
      acceptanceInstructions: '',
    },
    validate: {
      name: (value) => (value.length < 2 ? 'Name must be at least 2 characters' : null),
      slug: (value) =>
        /^[a-z0-9]+(?:-[a-z0-9]+)*$/.test(value)
          ? null
          : 'Slug must be lowercase, alphanumeric with hyphens',
      description: (value) =>
        value.length < 10 ? 'Description must be at least 10 characters' : null,
    },
  })

  const handleSubmit = async (values: GroupFormValues) => {
    try {
      await createGroup({
        ...values,
        logoFilename: logo ? 'pending-upload' : undefined,
      })
      navigate('/groups')
    } catch (error) {
      console.error('Failed to create group:', error)
    }
  }

  return (
    <Container size='md' py='xl'>
      <Title order={1} mb='xl'>
        Create New Group
      </Title>

      <form onSubmit={form.onSubmit(handleSubmit)}>
        <Stack spacing='md'>
          <GroupLogoUpload onUpload={(file) => setLogo(file)} />

          <TextInput
            label='Group Name'
            placeholder='Enter group name'
            required
            {...form.getInputProps('name')}
          />

          <TextInput
            label='URL Slug'
            placeholder='group-slug'
            description='Used in URLs, must be unique'
            required
            {...form.getInputProps('slug')}
          />

          <Textarea
            label='Description'
            placeholder='Enter group description'
            minRows={3}
            required
            {...form.getInputProps('description')}
          />

          <TextInput
            label='Website Link'
            placeholder='https://example.com'
            {...form.getInputProps('websiteLink')}
          />

          <Textarea
            label='Email Footer'
            placeholder='Enter email footer text'
            minRows={3}
            required
            {...form.getInputProps('mailFooter')}
          />

          <Textarea
            label='Acceptance Email Text'
            placeholder='Enter acceptance email text'
            minRows={3}
            required
            {...form.getInputProps('acceptanceEmailText')}
          />

          <Textarea
            label='Acceptance Instructions'
            placeholder='Enter acceptance instructions'
            minRows={3}
            required
            {...form.getInputProps('acceptanceInstructions')}
          />

          <Button type='submit'>Create Group</Button>
        </Stack>
      </form>
    </Container>
  )
}

export default CreateGroupPage
