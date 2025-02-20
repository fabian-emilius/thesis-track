import { useNavigate, useParams } from 'react-router'
import { useTopic } from '../../hooks/fetcher'
import { Card, Center, Stack, Stepper, Text, Title } from '@mantine/core'
import { useEffect, useState } from 'react'
import SelectTopicStep from './components/SelectTopicStep/SelectTopicStep'
import StudentInformationStep from './components/StudentInformationStep/StudentInformationStep'
import MotivationStep from './components/MotivationStep/MotivationStep'
import TopicsProvider from '../../providers/TopicsProvider/TopicsProvider'
import { IApplication } from '../../requests/responses/application'
import { doRequest } from '../../requests/request'
import { usePageTitle } from '../../hooks/theme'
import { GroupSelectionStep } from './components/GroupSelectionStep/GroupSelectionStep'
import { useGroupContext } from '../../providers/GroupContext/context'

const ReplaceApplicationPage = () => {
  const { topicId, applicationId } = useParams<{ topicId: string; applicationId: string }>()

  usePageTitle('Submit Application')

  const [application, setApplication] = useState<IApplication>()
  const [selectedGroupId, setSelectedGroupId] = useState('')
  const [groupError, setGroupError] = useState('')

  useEffect(() => {
    setApplication(undefined)

    if (applicationId) {
      return doRequest<IApplication>(
        `/v2/applications/${applicationId}`,
        {
          method: 'GET',
          requiresAuth: true,
        },
        (res) => {
          if (res.ok) {
            setApplication(res.data)
            setSelectedGroupId(res.data.groupId)
          }
        },
      )
    }
  }, [applicationId])

  const navigate = useNavigate()
  const topic = useTopic(topicId)
  const { availableGroups } = useGroupContext()

  const [step, setStep] = useState(0)

  const updateStep = (value: number) => {
    if (value > step) {
      return
    }

    if (value === 0 && (topicId || applicationId)) {
      navigate(`/submit-application`, { replace: true })
    }

    window.scrollTo(0, 0)
    setStep(value)
  }

  const handleGroupSelect = (groupId: string) => {
    setSelectedGroupId(groupId)
    setGroupError('')
  }

  const handleGroupStepComplete = () => {
    if (!selectedGroupId) {
      setGroupError('Please select a group')
      return
    }
    setStep(1)
  }

  return (
    <Stack>
      <Title>{applicationId ? 'Edit Application' : 'Submit Application'}</Title>
      <Stepper active={Math.max(step, topicId || applicationId ? 2 : 0)} onStepClick={updateStep}>
        <Stepper.Step label='First Step' description='Select Group'>
          <GroupSelectionStep
            value={selectedGroupId}
            onChange={handleGroupSelect}
            error={groupError}
          />
          <Center mt="xl">
            <button onClick={handleGroupStepComplete}>Continue</button>
          </Center>
        </Stepper.Step>
        <Stepper.Step label='Second Step' description='Select Topic'>
          <TopicsProvider limit={100} filters={{ groupId: selectedGroupId }}>
            <SelectTopicStep
              onComplete={(x) => {
                navigate(`/submit-application/${x?.topicId || ''}`, { replace: true })
                setStep(2)
              }}
            />
          </TopicsProvider>
        </Stepper.Step>
        <Stepper.Step label='Third step' description='Update Information'>
          <StudentInformationStep onComplete={() => setStep(3)} />
        </Stepper.Step>
        <Stepper.Step label='Final step' description='Submit your Application'>
          <MotivationStep
            onComplete={() => setStep(4)}
            topic={topic || undefined}
            application={application}
          />
        </Stepper.Step>
        <Stepper.Completed>
          <Center style={{ height: '50vh' }}>
            <Card withBorder p='xl'>
              <Stack gap='sm'>
                <Text ta='center'>
                  {application
                    ? 'Your application was successfully updated!'
                    : 'Your application was successfully submitted!'}
                </Text>
                <Text ta='center' size='sm' c='muted'>
                  We will contact you as soon as we have reviewed your application.
                </Text>
              </Stack>
            </Card>
          </Center>
        </Stepper.Completed>
      </Stepper>
    </Stack>
  )
}

export default ReplaceApplicationPage
