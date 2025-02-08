import { TextInput, Textarea, Button, Box, Alert } from '@mantine/core';
import { useState, useEffect } from 'react';
import { request } from '../../../requests/request';
import { Group } from '../../../types/group';
import { useGroupPermissions } from '../../../hooks/group-permissions';
import { useForm } from '@mantine/form';
import { sanitizeInput } from '../../../utils/security';
import { getCsrfToken } from '../../../utils/csrf';
import { handleApiError } from '../../../utils/error-handling';
import { groupSchema } from '../../../validation/group-schema';

interface GroupSettingsFormProps {
  groupId: string;
}

export function GroupSettingsForm({ groupId }: GroupSettingsFormProps) {
  const [group, setGroup] = useState<Group | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { canManageGroup } = useGroupPermissions({ groupId });
  
  const form = useForm({
    initialValues: {
      name: '',
      description: '',
    },
    validate: groupSchema,
  });

  useEffect(() => {
    const fetchGroup = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await request<Group>('GET', `/api/groups/${groupId}`);
        setGroup(response);
        form.setValues({
          name: response.name,
          description: response.description,
        });
      } catch (error) {
        const errorMessage = handleApiError(error);
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchGroup();
  }, [groupId, form]);

  const handleSubmit = async (values: typeof form.values) => {
    if (!canManageGroup) return;
    
    try {
      setError(null);
      const sanitizedValues = {
        name: sanitizeInput(values.name),
        description: sanitizeInput(values.description),
      };
      
      const csrfToken = await getCsrfToken();
      const response = await request<Group>('PUT', `/api/groups/${groupId}`, sanitizedValues, {
        headers: {
          'X-CSRF-Token': csrfToken,
        },
      });
      
      setGroup(response);
      form.setValues({
        name: response.name,
        description: response.description,
      });
    } catch (error) {
      const errorMessage = handleApiError(error);
      setError(errorMessage);
    }
  };

  if (loading || !group) return null;

  return (
    <Box component="form" onSubmit={form.onSubmit(handleSubmit)}>
      {error && (
        <Alert color="red" mb="md">
          {error}
        </Alert>
      )}
      <TextInput
        label="Group Name"
        required
        mb="md"
        {...form.getInputProps('name')}
      />
      <Textarea
        label="Description"
        mb="xl"
        {...form.getInputProps('description')}
      />
      <Button 
        type="submit" 
        disabled={!canManageGroup || loading}
        loading={loading}
      >
        Save Changes
      </Button>
    </Box>
  );
}
