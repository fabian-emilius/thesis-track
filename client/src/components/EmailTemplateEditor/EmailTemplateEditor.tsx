import { Stack, TextInput, Button, Textarea, Select, Text, LoadingOverlay } from '@mantine/core';
import { useForm } from '@mantine/form';
import { useState } from 'react';
import { Group } from '../../types/group';
import { notifications } from '@mantine/notifications';

interface EmailTemplate {
  key: string;
  name: string;
  subject: string;
  body: string;
}

interface EmailTemplateEditorProps {
  /** The group whose email templates are being edited */
  group: Group;
  /** Callback fired when templates are saved */
  onSave: (templates: Record<string, EmailTemplate>) => Promise<void>;
}

/**
 * Predefined email templates with default content
 * These serve as the base templates that can be customized per group
 */
const DEFAULT_TEMPLATES: EmailTemplate[] = [
  {
    key: 'application_received',
    name: 'Application Received',
    subject: 'Your thesis application has been received',
    body: 'Dear {student},\n\nYour thesis application for "{topic}" has been received and is being reviewed.\n\nBest regards,\n{group}',
  },
  {
    key: 'application_accepted',
    name: 'Application Accepted',
    subject: 'Your thesis application has been accepted',
    body: 'Dear {student},\n\nCongratulations! Your thesis application for "{topic}" has been accepted.\n\nBest regards,\n{group}',
  },
  {
    key: 'application_rejected',
    name: 'Application Rejected',
    subject: 'Your thesis application status',
    body: 'Dear {student},\n\nUnfortunately, your thesis application for "{topic}" has not been accepted.\n\nBest regards,\n{group}',
  },
];

/**
 * Component for editing group email templates
 * Provides interface for customizing predefined email templates
 */
export function EmailTemplateEditor({ group, onSave }: EmailTemplateEditorProps) {
  const [selectedTemplate, setSelectedTemplate] = useState<string>(DEFAULT_TEMPLATES[0].key);
  const [isSaving, setIsSaving] = useState(false);

  const templates = group.settings?.emailTemplates || {};

  const form = useForm({
    initialValues: {
      subject: templates[selectedTemplate]?.subject || DEFAULT_TEMPLATES.find(t => t.key === selectedTemplate)?.subject || '',
      body: templates[selectedTemplate]?.body || DEFAULT_TEMPLATES.find(t => t.key === selectedTemplate)?.body || '',
    },
    validate: {
      subject: (value) => !value ? 'Subject is required' : null,
      body: (value) => !value ? 'Body is required' : null,
    },
  });

  const handleSubmit = form.onSubmit(async (values) => {
    try {
      setIsSaving(true);
      const updatedTemplates = {
        ...templates,
        [selectedTemplate]: {
          key: selectedTemplate,
          name: DEFAULT_TEMPLATES.find(t => t.key === selectedTemplate)?.name || '',
          ...values,
        },
      };
      await onSave(updatedTemplates);
      notifications.show({
        title: 'Success',
        message: 'Email template saved successfully',
        color: 'green',
      });
    } catch (error) {
      console.error('Failed to save template:', error);
      notifications.show({
        title: 'Error',
        message: 'Failed to save template. Please try again.',
        color: 'red',
      });
    } finally {
      setIsSaving(false);
    }
  });

  const handleReset = () => {
    const defaultTemplate = DEFAULT_TEMPLATES.find(t => t.key === selectedTemplate);
    if (defaultTemplate) {
      form.setValues({
        subject: defaultTemplate.subject,
        body: defaultTemplate.body,
      });
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Stack spacing="md" pos="relative">
        <LoadingOverlay visible={isSaving} />

        <Select
          label="Template"
          value={selectedTemplate}
          onChange={(value) => value && setSelectedTemplate(value)}
          data={DEFAULT_TEMPLATES.map(template => ({
            value: template.key,
            label: template.name,
          }))}
        />

        <TextInput
          required
          label="Subject"
          placeholder="Email subject"
          {...form.getInputProps('subject')}
        />

        <Textarea
          required
          label="Body"
          placeholder="Email body"
          minRows={10}
          {...form.getInputProps('body')}
        />

        <Text size="sm" c="dimmed">
          Available variables: {'{student}'}, {'{topic}'}, {'{group}'}, {'{advisor}'}, {'{supervisor}'}
        </Text>

        <Stack spacing="xs" direction="row">
          <Button type="submit" loading={isSaving}>Save Template</Button>
          <Button variant="light" onClick={handleReset} disabled={isSaving}>Reset to Default</Button>
        </Stack>
      </Stack>
    </form>
  );
}
