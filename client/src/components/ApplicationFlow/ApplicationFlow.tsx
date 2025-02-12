import { useState } from 'react';
import { Container, Stepper } from '@mantine/core';
import { useParams } from 'react-router-dom';
import { GroupSelectionStep } from '../GroupSelectionStep/GroupSelectionStep';
import { StudentInformationStep } from '../../pages/ReplaceApplicationPage/components/StudentInformationStep/StudentInformationStep';
import { SelectTopicStep } from '../../pages/ReplaceApplicationPage/components/SelectTopicStep/SelectTopicStep';
import { MotivationStep } from '../../pages/ReplaceApplicationPage/components/MotivationStep/MotivationStep';
import { useGroupContext } from '../../providers/GroupContext/context';

export function ApplicationFlow() {
  const { groupSlug } = useParams();
  const { currentGroup } = useGroupContext();
  const [active, setActive] = useState(groupSlug ? 1 : 0);
  const [groupId, setGroupId] = useState<string | null>(currentGroup?.id || null);

  const nextStep = () => setActive((current) => current + 1);
  const prevStep = () => setActive((current) => current - 1);

  const handleGroupSelect = (selectedGroupId: string) => {
    setGroupId(selectedGroupId);
    nextStep();
  };

  return (
    <Container size="xl">
      <Stepper active={active} onStepClick={setActive}>
        <Stepper.Step label="Select Group" description="Choose a research group">
          <GroupSelectionStep onGroupSelect={handleGroupSelect} />
        </Stepper.Step>

        <Stepper.Step
          label="Personal Information"
          description="Your details"
          disabled={!groupId}
        >
          <StudentInformationStep onComplete={nextStep} onBack={prevStep} />
        </Stepper.Step>

        <Stepper.Step
          label="Select Topic"
          description="Choose a thesis topic"
          disabled={!groupId}
        >
          <SelectTopicStep onComplete={nextStep} onBack={prevStep} />
        </Stepper.Step>

        <Stepper.Step
          label="Motivation"
          description="Write your motivation"
          disabled={!groupId}
        >
          <MotivationStep onBack={prevStep} />
        </Stepper.Step>
      </Stepper>
    </Container>
  );
}
