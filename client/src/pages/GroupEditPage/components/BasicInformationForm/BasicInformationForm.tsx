import { TextInput, Textarea, Button, Stack } from '@mantine/core';
import { useForm } from '@mantine/form';
import { Group } from '../../../../types/group';

interface BasicInformationFormProps {
  group: Group;
  onSubmit: (values: Partial<Group>) => void;
}

export function BasicInformationForm({ group, onSubmit }: BasicInformationFormProps) {
  const form = useForm({
    initialValues: {
      name: group.name,
      description: group.description,
      link: group.link || '',
    },
    validate: {
      name: (value) => (!value ? 'Name is required' : null),
      description: (value) => (!value ? 'Description is required' : null),
    },
  });

  return (
    <form onSubmit={form.onSubmit(onSubmit)}>
      <Stack>
        <TextInput
          label="Group Name"
          placeholder="Enter group name"
          required
          {...form.getInputProps('name')}
        />

        <Textarea
          label="Description"
          placeholder="Enter group description"
          required
          minRows={3}
          {...form.getInputProps('description')}
        />

        <TextInput
          label="Website Link"
          placeholder="https://"
          {...form.getInputProps('link')}
        />

        <Button type="submit">Save Changes</Button>
      </Stack>
    </form>
  );
}
