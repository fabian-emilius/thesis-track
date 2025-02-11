import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { Stack, Title, Text, Divider, Loader, Center, Grid, Card, Button, Group } from '@mantine/core';
import { doRequest } from '../../requests/request';
import { showSimpleError } from '../../utils/notification';
import { Link } from 'react-router';
import PageLoader from '../../components/PageLoader/PageLoader';

interface IGroup {
  groupId: string;
  name: string;
  description: string;
}

interface ITopic {
  topicId: string;
  title: string;
  types: string[];
  advisor: {
    firstName: string;
    lastName: string;
  };
}

interface IThesis {
  thesisId: string;
  title: string;
  type: string;
  students: Array<{
    firstName: string;
    lastName: string;
  }>;
}

const GroupPage = () => {
  const { groupId } = useParams<{ groupId: string }>();

  const [group, setGroup] = useState<IGroup | null>(null);
  const [topics, setTopics] = useState<ITopic[] | null>(null);
  const [theses, setTheses] = useState<IThesis[] | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (!groupId) return;

    setLoading(true);

    const fetchGroupDetails = async () => {
      try {
        const groupResponse = await doRequest<IGroup>(`/v2/groups/${groupId}`, {
          method: 'GET',
          requiresAuth: true,
        });

        if (groupResponse.ok) {
          setGroup(groupResponse.data);
        } else {
          showSimpleError(`Failed to fetch group details: ${groupResponse.status}`);
        }

        const topicsResponse = await doRequest<ITopic[]>(`/v2/groups/${groupId}/topics`, {
          method: 'GET',
          requiresAuth: true,
        });

        if (topicsResponse.ok) {
          setTopics(topicsResponse.data);
        } else {
          showSimpleError(`Failed to fetch topics: ${topicsResponse.status}`);
        }

        const thesesResponse = await doRequest<IThesis[]>(`/v2/groups/${groupId}/theses`, {
          method: 'GET',
          requiresAuth: true,
        });

        if (thesesResponse.ok) {
          setTheses(thesesResponse.data);
        } else {
          showSimpleError(`Failed to fetch theses: ${thesesResponse.status}`);
        }
      } catch (error) {
        showSimpleError('An error occurred while fetching group details.');
      } finally {
        setLoading(false);
      }
    };

    fetchGroupDetails();
  }, [groupId]);

  if (loading) {
    return <PageLoader />;
  }

  if (!group) {
    return (
      <Center style={{ height: '50vh' }}>
        <Text>Group not found.</Text>
      </Center>
    );
  }

  return (
    <Stack>
      <Title>{group.name}</Title>
      <Text>{group.description}</Text>

      <Divider my="lg" />

      <Stack>
        <Title order={2}>Open Topics</Title>
        {topics && topics.length > 0 ? (
          <Grid>
            {topics.map((topic) => (
              <Grid.Col key={topic.topicId} span={12} sm={6} md={4}>
                <Card shadow="sm" padding="lg" radius="md" withBorder>
                  <Stack>
                    <Title order={4}>{topic.title}</Title>
                    <Text>Advisor: {`${topic.advisor.firstName} ${topic.advisor.lastName}`}</Text>
                    <Button
                      component={Link}
                      to={`/topics/${topic.topicId}`}
                      variant="light"
                      fullWidth
                    >
                      View Topic
                    </Button>
                  </Stack>
                </Card>
              </Grid.Col>
            ))}
          </Grid>
        ) : (
          <Text>No open topics available.</Text>
        )}
      </Stack>

      <Divider my="lg" />

      <Stack>
        <Title order={2}>Published Theses</Title>
        {theses && theses.length > 0 ? (
          <Grid>
            {theses.map((thesis) => (
              <Grid.Col key={thesis.thesisId} span={12} sm={6} md={4}>
                <Card shadow="sm" padding="lg" radius="md" withBorder>
                  <Stack>
                    <Title order={4}>{thesis.title}</Title>
                    <Text>Type: {thesis.type}</Text>
                    <Text>
                      Students:{' '}
                      {thesis.students.map((student) => `${student.firstName} ${student.lastName}`).join(', ')}
                    </Text>
                    <Button
                      component={Link}
                      to={`/theses/${thesis.thesisId}`}
                      variant="light"
                      fullWidth
                    >
                      View Thesis
                    </Button>
                  </Stack>
                </Card>
              </Grid.Col>
            ))}
          </Grid>
        ) : (
          <Text>No published theses available.</Text>
        )}
      </Stack>
    </Stack>
  );
};

export default GroupPage;
