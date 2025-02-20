import React from 'react';
import { Stack, Button, Group } from '@mantine/core';
import { useForm } from '@mantine/form';
import { DocumentEditor } from '../DocumentEditor/DocumentEditor';
import { GroupSettings } from '../../requests/responses/group';

interface EmailTemplateEditorProps {
  settings: GroupSettings;
  onSave: (templates: Partial<GroupSettings>) => void;
}

export function EmailTemplateEditor({ settings, onSave }: EmailTemplateEditorProps) {
  const form = useForm({
    initialValues: {
      acceptanceEmailTemplate: settings.acceptanceEmailTemplate || '',
      postAcceptanceInstructions: settings.postAcceptanceInstructions || '',
      emailFooter: settings.emailFooter || '',
    },
  });

  const handleSubmit = form.onSubmit((values) => {
    onSave(values);
  });

  return (
    <form onSubmit={handleSubmit}>
      <Stack gap="xl">
        <Stack gap="xs">
          <DocumentEditor
            label="Acceptance Email Template"
            value={form.values.acceptanceEmailTemplate}
            onChange={(value) => form.setFieldValue('acceptanceEmailTemplate', value)}
            description="Template for emails sent when a thesis application is accepted."
          />
        </Stack>

        <Stack gap="xs">
          <DocumentEditor
            label="Post-Acceptance Instructions"
            value={form.values.postAcceptanceInstructions}
            onChange={(value) => form.setFieldValue('postAcceptanceInstructions', value)}
            description="Instructions sent to students after their application is accepted."
          />
        </Stack>

        <Stack gap="xs">
          <DocumentEditor
            label="Email Footer"
            value={form.values.emailFooter}
            onChange={(value) => form.setFieldValue('emailFooter', value)}
            description="Footer appended to all emails sent by the system."
          />
        </Stack>

        <Group justify="flex-end">
          <Button type="submit">Save Templates</Button>
        </Group>
      </Stack>
    </form>
  );
}