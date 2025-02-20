import React from 'react';
import { TextInput, Textarea, Stack, Group, Button, LoadingOverlay } from '@mantine/core';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import { Group as GroupType } from '../../requests/responses/group';
import { isValidUrl } from '../../utils/validation';

/**
 * Form values interface for group settings
 */
interface GroupFormValues {
  name: string;
  slug: string;
  description: string;
  logoUrl: string;
  externalLink: string;
}

/**
 * Props for the GroupSettingsForm component
 */
interface GroupSettingsFormProps {
  /** Existing group data for editing mode */
  group?: Partial<GroupType>;
  /** Callback function when form is submitted successfully */
  onSubmit: (values: GroupFormValues) => Promise<void>;
  /** Callback function when form is cancelled */
  onCancel: () => void;
  /** Loading state for the form */
  isLoading?: boolean;
}

export function GroupSettingsForm({ group, onSubmit, onCancel }: GroupSettingsFormProps) {
  const form = useForm<GroupFormValues>({
    initialValues: {
      name: group?.name || '',
      slug: group?.slug || '',
      description: group?.description || '',
      logoUrl: group?.logoUrl || '',
      externalLink: group?.externalLink || '',
    },
    validate: {
      name: (value) => {
        if (!value?.trim()) return 'Name is required';
        if (value.length < 3) return 'Name must be at least 3 characters long';
        if (value.length > 100) return 'Name must be less than 100 characters';
        return null;
      },
      slug: (value) => {
        if (!value?.trim()) return 'Slug is required';
        if (value.length < 3) return 'Slug must be at least 3 characters long';
        if (value.length > 50) return 'Slug must be less than 50 characters';
        if (!/^[a-z0-9-]+$/.test(value)) {
          return 'Slug must contain only lowercase letters, numbers, and hyphens';
        }
        return null;
      },
      description: (value) => 
        value && value.length > 1000 ? 'Description must be less than 1000 characters' : null,
      logoUrl: (value) => 
        value && !isValidUrl(value) ? 'Logo URL must be a valid URL' : null,
      externalLink: (value) => 
        value && !isValidUrl(value) ? 'External link must be a valid URL' : null,
    },
  });

  const handleSubmit = form.onSubmit(async (values) => {
    try {
      await onSubmit(values);
      notifications.show({
        title: 'Success',
        message: group ? 'Group updated successfully' : 'Group created successfully',
        color: 'green',
      });
      form.reset();
    } catch (error) {
      notifications.show({
        title: 'Error',
        message: error instanceof Error ? error.message : 'An unexpected error occurred',
        color: 'red',
      });
    }
  });

  const handleReset = () => {
    form.reset();
    onCancel();
  };

  return (
    <form onSubmit={handleSubmit}>
      <LoadingOverlay visible={isLoading ?? false} overlayProps={{ radius: "sm", blur: 2 }} />
      <Stack gap="md">
        <TextInput
          label="Group Name"
          placeholder="Research Group Name"
          required
          {...form.getInputProps('name')}
        />

        <TextInput
          label="URL Slug"
          placeholder="group-name"
          description="Used in URLs. Use lowercase letters, numbers, and hyphens only."
          required
          {...form.getInputProps('slug')}
        />

        <Textarea
          label="Description"
          placeholder="Describe the research group..."
          minRows={3}
          {...form.getInputProps('description')}
        />

        <TextInput
          label="Logo URL"
          placeholder="https://example.com/logo.png"
          {...form.getInputProps('logoUrl')}
        />

        <TextInput
          label="External Website"
          placeholder="https://research-group.university.edu"
          {...form.getInputProps('externalLink')}
        />

        <Group justify="flex-end" gap="sm">
          <Button 
            variant="subtle" 
            onClick={handleReset} 
            disabled={isLoading}
          >
            Cancel
          </Button>
          <Button 
            type="submit" 
            loading={isLoading}
            disabled={!form.isValid() || isLoading}
          >
            {group ? 'Update Group' : 'Create Group'}
          </Button>
        </Group>
      </Stack>
    </form>
  );
}