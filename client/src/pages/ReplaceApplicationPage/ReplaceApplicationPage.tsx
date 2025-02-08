import { useNavigate, useParams } from 'react-router'
import { useTopic, useGroup } from '../../hooks/fetcher'
import { Card, Center, Stack, Stepper, Text, Title } from '@mantine/core'
import { useEffect, useState } from 'react'
import SelectTopicStep from './components/SelectTopicStep/SelectTopicStep'
import SelectGroupStep from './components/SelectGroupStep/SelectGroupStep'
import StudentInformationStep from './components/StudentInformationStep/StudentInformationStep'
import MotivationStep from './components/MotivationStep/MotivationStep'
import TopicsProvider from '../../providers/TopicsProvider/TopicsProvider'
import { IApplication } from '../../requests/responses/application'
import { doRequest } from '../../requests/request'
import { usePageTitle } from '../../hooks/theme'

const ReplaceApplicationPage = () => {
  const { topicId, applicationId, groupId } = useParams<{ topicId: string; applicationId: string; groupId: string }>()

  usePageTitle('Submit Application')

  const [application, setApplication] = useState<IApplication>()
  const [selectedGroupId, setSelectedGroupId] = useState<string | undefined>(groupId)

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
          }
        },
      )
    }
  }, [applicationId])

  const navigate = useNavigate()
  const topic = useTopic(topicId)

  const [step, setStep] = useState(0)

  const updateStep = (value: number) => {
    if (value > step) {
      return
    }

    if (value === 0) {
      navigate(`/submit-application`, { replace: true })
      setSelectedGroupId(undefined)
    } else if (value === 1 && topicId) {
      navigate(`/submit-application/group/${selectedGroupId}`, { replace: true })
    }

    window.scrollTo(0, 0)
    setStep(value)
  }

  return (
    <Stack>
      <Title>{applicationId ? 'Edit Application' : 'Submit Application'}</Title>
      <Stepper active={Math.max(step, groupId ? 1 : 0, topicId || applicationId ? 2 : 0)} onStepClick={updateStep}>
        <Stepper.Step label='First Step' description='Select Group'>
          <SelectGroupStep
            onComplete={(group) => {
              setSelectedGroupId(group.id)
              navigate(`/submit-application/group/${group.id}`, { replace: true })
              setStep(1)
            }}
          />
        </Stepper.Step>
        <Stepper.Step label='Second Step' description='Select Topic'>
          <TopicsProvider limit={100} groupId={selectedGroupId}>
            <SelectTopicStep
              groupId={selectedGroupId}
              onComplete={(x) => {
                navigate(`/submit-application/group/${selectedGroupId}/topic/${x?.topicId || ''}`, { replace: true })
                setStep(2)
              }}
            />
        </Stepper.Step>
        <Stepper.Step label='Second step' description='Update Information'>
          <StudentInformationStep onComplete={() => setStep(3)} />
        </Stepper.Step>
        <Stepper.Step label='Final step' description='Submit your Application'>
          <MotivationStep
            onComplete={() => setStep(3)}
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
