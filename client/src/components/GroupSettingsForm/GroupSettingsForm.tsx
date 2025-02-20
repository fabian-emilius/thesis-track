import { TextInput, Textarea, Button, Stack, FileInput, LoadingOverlay } from '@mantine/core';
import { useForm } from '@mantine/form';
import { Group } from '../../types/group';
import { useGroupContext } from '../../providers/GroupContext/context';
import { useState } from 'react';

interface GroupSettingsFormProps {
  /** The group to edit, undefined for creating a new group */
  group?: Group;
  /** Callback fired when form is submitted with valid data */
  onSubmit: (values: Partial<Group>) => Promise<void>;
}

/**
 * Form component for creating or editing group settings
 * Handles validation, file uploads, and form state management
 */
export function GroupSettingsForm({ group, onSubmit }: GroupSettingsFormProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { isLoading } = useGroupContext();

  const form = useForm({
    initialValues: {
      name: group?.name || '',
      slug: group?.slug || '',
      description: group?.description || '',
      websiteUrl: group?.websiteUrl || '',
      settings: group?.settings || {},
    },
    validate: {
      name: (value) => {
        if (!value) return 'Name is required';
        if (value.length < 2) return 'Name must be at least 2 characters';
        if (value.length > 100) return 'Name must be less than 100 characters';
        return null;
      },
      slug: (value) => {
        if (!value) return 'Slug is required';
        if (!/^[a-z0-9-]+$/.test(value)) return 'Slug can only contain lowercase letters, numbers, and hyphens';
        if (value.length < 2) return 'Slug must be at least 2 characters';
        if (value.length > 50) return 'Slug must be less than 50 characters';
        return null;
      },
      websiteUrl: (value) =>
        value && !/^https?:\/\/.+/.test(value) ? 'Must be a valid URL starting with http(s)://' : null,
    },
  });

  const handleSubmit = form.onSubmit(async (values) => {
    try {
      setIsSubmitting(true);
      await onSubmit(values);
      form.reset();
    } catch (error) {
      console.error('Form submission failed:', error);
    } finally {
      setIsSubmitting(false);
    }
  });

  return (
    <form onSubmit={handleSubmit}>
      <Stack spacing="md" pos="relative">
        <LoadingOverlay visible={isLoading || isSubmitting} />

        <TextInput
          required
          label="Group Name"
          placeholder="Enter group name"
          description="The display name of your group"
          maxLength={100}
          {...form.getInputProps('name')}
        />

        <TextInput
          required
          label="Group Slug"
          placeholder="enter-group-slug"
          description="Used in URLs. Use lowercase letters, numbers, and hyphens only."
          maxLength={50}
          {...form.getInputProps('slug')}
        />

        <Textarea
          label="Description"
          placeholder="Enter group description"
          description="A brief description of your group"
          maxLength={500}
          autosize
          minRows={3}
          maxRows={5}
          {...form.getInputProps('description')}
        />

        <TextInput
          label="Website URL"
          placeholder="https://example.com"
          description="Your group's website (optional)"
          {...form.getInputProps('websiteUrl')}
        />

        <FileInput
          label="Group Logo"
          placeholder="Upload logo"
          accept="image/*"
          description="Recommended size: 400x400px. Maximum size: 2MB"
        />

        <Button
          type="submit"
          loading={isSubmitting}
          disabled={isLoading}
        >
          {group ? 'Update Group' : 'Create Group'}
        </Button>
      </Stack>
    </form>
  );
}
