import React, { useEffect, useState } from 'react';
import { Container, Stepper, Title } from '@mantine/core';
import { useParams } from 'react-router-dom';
import { ITopic } from '../../requests/responses/topic';
import { IApplication } from '../../requests/responses/application';
import { doRequest } from '../../requests/request';
import { Group } from '../../types/group';
import { GroupSelectionStep } from './components/GroupSelectionStep/GroupSelectionStep';
import SelectTopicStep from './components/SelectTopicStep/SelectTopicStep';
import MotivationStep from './components/MotivationStep/MotivationStep';
import { useGroup } from '../../providers/GroupContext/hooks';
import { useGroupApi } from '../../hooks/group';
import { getApiResponseErrorMessage } from '../../requests/handler';
import { showSimpleError } from '../../utils/notification';

const ReplaceApplicationPage: React.FC = () => {
  const { topicId, applicationId } = useParams<{ topicId?: string; applicationId?: string }>();
  const [active, setActive] = useState(0);
  const [selectedTopic, setSelectedTopic] = useState<ITopic>();
  const [application, setApplication] = useState<IApplication>();
  const { setCurrentGroup } = useGroup();
  const { fetchGroupById } = useGroupApi();

  useEffect(() => {
    const fetchApplication = async () => {
      try {
        if (applicationId) {
          const response = await doRequest(`/v2/applications/${applicationId}`, {
            method: 'GET',
            requiresAuth: true,
          });

          if (response.ok) {
            const data = await response.json() as IApplication;
            setApplication(data);
            setSelectedTopic(data.topic);
            
            if (data.groupId) {
              const groupData = await fetchGroupById(data.groupId);
              if (groupData) {
                setCurrentGroup(groupData);
                setActive(1); // Skip group selection for existing applications
              }
            }
          } else {
            showSimpleError(getApiResponseErrorMessage(response));
          }
        } else if (topicId) {
          const response = await doRequest(`/v2/topics/${topicId}`, {
            method: 'GET',
            requiresAuth: true,
          });

          if (response.ok) {
            const data = await response.json() as ITopic;
            setSelectedTopic(data);
            
            if (data.groupId) {
              const groupData = await fetchGroupById(data.groupId);
              if (groupData) {
                setCurrentGroup(groupData);
                setActive(1); // Skip group selection for pre-selected topics
              }
            }
          } else {
            showSimpleError(getApiResponseErrorMessage(response));
          }
        }
      } catch (error) {
        showSimpleError('Failed to load application data');
      }
    };

    fetchApplication();
  }, [applicationId, topicId]);

  const handleGroupSelection = (group: Group) => {
    setCurrentGroup(group);
    setActive(1);
  };

  const handleTopicSelection = (topic: ITopic | undefined) => {
    setSelectedTopic(topic);
    setActive(2);
  };

  const handleComplete = () => {
    window.location.href = '/dashboard';
  };

  return (
    <Container size="xl" py="xl">
      <Title order={1} mb="xl">
        {application ? 'Edit Application' : 'Submit Application'}
      </Title>

      <Stepper active={active} onStepClick={setActive}>
        <Stepper.Step
          label="Select Group"
          description="Choose a research group"
          allowStepSelect={!applicationId && !topicId}
        >
          <GroupSelectionStep onComplete={handleGroupSelection} />
        </Stepper.Step>

        <Stepper.Step
          label="Select Topic"
          description="Choose or suggest a topic"
          allowStepSelect={active > 0}
        >
          <SelectTopicStep onComplete={handleTopicSelection} />
        </Stepper.Step>

        <Stepper.Step
          label="Motivation"
          description="Provide your motivation"
          allowStepSelect={active > 1}
        >
          <MotivationStep
            topic={selectedTopic}
            application={application}
            onComplete={handleComplete}
          />
        </Stepper.Step>
      </Stepper>
    </Container>
  );
};

export default ReplaceApplicationPage;
