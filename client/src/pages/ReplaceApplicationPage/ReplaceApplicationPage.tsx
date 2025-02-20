import React, { useState } from 'react'
import { Container, Stepper, Title } from '@mantine/core'
import { useNavigate, useParams } from 'react-router-dom'
import { GroupSelectionStep } from './components/GroupSelectionStep/GroupSelectionStep'
import MotivationStep from './components/MotivationStep/MotivationStep'
import { useGroupContext } from '../../providers/GroupContext/hooks'
import { IGroupResponse } from '../../providers/GroupContext/types'

export const ReplaceApplicationPage: React.FC = () => {
  const navigate = useNavigate()
  const { groupId } = useParams<{ groupId?: string }>()
  const { groups } = useGroupContext()
  const [selectedGroupId, setSelectedGroupId] = useState<string | undefined>(groupId)
  const [active, setActive] = useState(groupId ? 1 : 0)

  const handleGroupSelect = (groupId: string): void => {
    setSelectedGroupId(groupId)
    setActive(1)
    const group = groups.find(g => g.id === groupId)
    if (group) {
      navigate(`/groups/${group.slug}/submit-application`)
    }
  }

  const handleComplete = (): void => {
    const group = groups.find((group: IGroupResponse) => group.id === selectedGroupId)
    if (group) {
      navigate(`/groups/${group.slug}/applications`)
    }
  }

  return (
    <Container size="xl">
      <Title order={1} mb="xl">
        Submit Thesis Application
      </Title>

      <Stepper active={active} allowNextStepsSelect={false}>
        <Stepper.Step label="Select Group" description="Choose a research group">
          <GroupSelectionStep onGroupSelect={handleGroupSelect} />
        </Stepper.Step>

        <Stepper.Step label="Application" description="Provide application details">
          <MotivationStep topic={undefined} application={undefined} onComplete={handleComplete} />
        </Stepper.Step>
      </Stepper>
    </Container>
  )
}

export default ReplaceApplicationPage
