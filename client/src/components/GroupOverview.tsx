import React, { useEffect, useState } from 'react';
import { Grid, Card, Text, Title, Button, Stack, Center, Loader } from '@mantine/core';
import { Link } from 'react-router';
import { doRequest } from '../../requests/request';
import { showSimpleError } from '../../utils/notification';

interface IGroup {
  groupId: string;
  name: string;
  description: string;
}

const GroupOverview = () => {
  const [groups, setGroups] = useState<IGroup[] | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    setLoading(true);

    return doRequest<IGroup[]>(
      '/v2/groups',
      {
        method: 'GET',
        requiresAuth: true,
      },
      (res) => {
        if (res.ok) {
          setGroups(res.data);
        } else {
          showSimpleError(`Failed to fetch groups: ${res.status}`);
          setGroups([]);
        }
        setLoading(false);
      }
    );
  }, []);

  if (loading) {
    return (
      <Center style={{ height: '50vh' }}>
        <Loader />
      </Center>
    );
  }

  if (!groups || groups.length === 0) {
    return (
      <Center style={{ height: '50vh' }}>
        <Text>No groups available.</Text>
      </Center>
    );
  }

  return (
    <Stack>
      <Title order={1}>Groups</Title>
      <Grid>
        {groups.map((group) => (
          <Grid.Col key={group.groupId} span={12} sm={6} md={4}>
            <Card shadow="sm" padding="lg" radius="md" withBorder>
              <Stack>
                <Title order={3}>{group.name}</Title>
                <Text>{group.description}</Text>
                <Button
                  component={Link}
                  to={`/groups/${group.groupId}`}
                  variant="light"
                  fullWidth
                >
                  View Topics and Theses
                </Button>
              </Stack>
            </Card>
          </Grid.Col>
        ))}
      </Grid>
    </Stack>
  );
};

export default GroupOverview;
