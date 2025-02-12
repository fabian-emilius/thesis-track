import { useNavigate, useParams } from 'react-router'
import { useTopic, useApplication } from '../../hooks/fetcher'
import { Alert, LoadingOverlay } from '@mantine/core'
import { useEffect } from 'react'
import { ApplicationFlow } from '../../components/ApplicationFlow'
import { useGroup } from '../../providers/GroupProvider'
import { usePageTitle } from '../../hooks/theme'

const ReplaceApplicationPage = () => {
  const { topicId, applicationId } = useParams<{ topicId: string; applicationId: string }>()
  const { group, isLoading: isLoadingGroup } = useGroup()
  const { data: topic, isLoading: isLoadingTopic } = useTopic(topicId)
  const { data: application, isLoading: isLoadingApplication } = useApplication(applicationId)
  
  usePageTitle(applicationId ? 'Edit Application' : 'Submit Application')
  
  const navigate = useNavigate()

  const isLoading = isLoadingGroup || isLoadingTopic || (applicationId && isLoadingApplication)

  if (isLoading) {
    return <LoadingOverlay visible />
  }

  if (!group) {
    return (
      <Alert color="red" title="Error">
        No group context available. Please select a group first.
      </Alert>
    )
  }

  return (
    <ApplicationFlow
      groupId={group.id}
      topicId={topicId}
      applicationId={applicationId}
      topic={topic}
      application={application}
      onNavigate={(path) => navigate(path, { replace: true })}
    />
  )
}

export default ReplaceApplicationPage
