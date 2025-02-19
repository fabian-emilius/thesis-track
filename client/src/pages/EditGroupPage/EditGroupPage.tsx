import React, { useEffect, useState } from 'react'
import { Container, Title, TextInput, Textarea, Button, Stack, Group, Card } from '@mantine/core'
import { useForm } from '@mantine/form'
import { useNavigate, useParams } from 'react-router-dom'
import { useGroups } from '../../providers/GroupsProvider/hooks'
import GroupLogoUpload from '../../components/GroupLogoUpload/GroupLogoUpload'
import GroupRoleSelect from '../../components/GroupRoleSelect/GroupRoleSelect'
import { GroupMember } from '../../requests/responses/group'
import PageLoader from '../../components/PageLoader/PageLoader'

interface GroupFormValues {
  name: string
  description: string
  websiteLink?: string
  mailFooter: string
  acceptanceEmailText: string
  acceptanceInstructions: string
}

const EditGroupPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>()
  const navigate = useNavigate()
  const { selectedGroup, loading, error, fetchGroup, updateGroup, addGroupMember, removeGroupMember } =
    useGroups()
  const [newMemberEmail, setNewMemberEmail] = useState('')
  const [newMemberRole, setNewMemberRole] = useState<GroupMember['role']>('ADVISOR')

  useEffect(() => {
    if (slug) {
      fetchGroup(slug)
    }
  }, [slug, fetchGroup])

  const form = useForm<GroupFormValues>({
    initialValues: {
      name: selectedGroup?.name || '',
      description: selectedGroup?.description || '',
      websiteLink: selectedGroup?.websiteLink || '',
      mailFooter: selectedGroup?.mailFooter || '',
      acceptanceEmailText: selectedGroup?.acceptanceEmailText || '',
      acceptanceInstructions: selectedGroup?.acceptanceInstructions || '',
    },
  })

  useEffect(() => {
    if (selectedGroup) {
      form.setValues({
        name: selectedGroup.name,
        description: selectedGroup.description,
        websiteLink: selectedGroup.websiteLink,
        mailFooter: selectedGroup.mailFooter,
        acceptanceEmailText: selectedGroup.acceptanceEmailText,
        acceptanceInstructions: selectedGroup.acceptanceInstructions,
      })
    }
  }, [selectedGroup])

  const handleSubmit = async (values: GroupFormValues) => {
    if (!slug) return
    try {
      await updateGroup(slug, values)
      navigate(`/groups/${slug}`)
    } catch (error) {
      console.error('Failed to update group:', error)
    }
  }

  if (loading) return <PageLoader />
  if (error) return <div>Error: {error.message}</div>
  if (!selectedGroup) return <div>Group not found</div>

  return (
    <Container size='md' py='xl'>
      <Title order={1} mb='xl'>
        Edit Group: {selectedGroup.name}
      </Title>

      <Stack spacing='xl'>
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <Stack spacing='md'>
            <GroupLogoUpload
              onUpload={(file) => {
                // TODO: Implement logo upload
                console.log('Logo upload:', file)
              }}
            />

            <TextInput
              label='Group Name'
              placeholder='Enter group name'
              required
              {...form.getInputProps('name')}
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

            <Button type='submit'>Save Changes</Button>
          </Stack>
        </form>

        <Card withBorder p='md'>
          <Title order={2} size='h3' mb='md'>
            Group Members
          </Title>

          <Stack spacing='md'>
            {selectedGroup.members?.map((member) => (
              <Group key={member.userId} position='apart'>
                <div>
                  <Text>{member.userId}</Text>
                  <Text size='sm' color='dimmed'>
                    {member.role}
                  </Text>
                </div>
                <Button
                  variant='subtle'
                  color='red'
                  onClick={() => removeGroupMember(slug, member.userId)}
                >
                  Remove
                </Button>
              </Group>
            ))}

            <Group grow>
              <TextInput
                placeholder='Enter member email'
                value={newMemberEmail}
                onChange={(e) => setNewMemberEmail(e.target.value)}
              />
              <GroupRoleSelect value={newMemberRole} onChange={setNewMemberRole} />
              <Button
                onClick={() => {
                  if (newMemberEmail && slug) {
                    addGroupMember(slug, newMemberEmail, newMemberRole)
                    setNewMemberEmail('')
                  }
                }}
              >
                Add Member
              </Button>
            </Group>
          </Stack>
        </Card>
      </Stack>
    </Container>
  )
}

export default EditGroupPage
