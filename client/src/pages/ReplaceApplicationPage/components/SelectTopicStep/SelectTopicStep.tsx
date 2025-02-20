import { ITopic } from '../../../../requests/responses/topic'
import { Accordion, Button, Center, Skeleton, Stack, Text } from '@mantine/core'
import { useTopicsContext } from '../../../../providers/TopicsProvider/hooks'
import React from 'react'
import TopicAccordionItem from '../../../../components/TopicAccordionItem/TopicAccordionItem'
import TopicsFilters from '../../../../components/TopicsFilters/TopicsFilters'
import { GLOBAL_CONFIG } from '../../../../config/global'
import { useCurrentGroup } from '../../../../providers/GroupContext/hooks'

interface ISelectTopicStepProps {
  onComplete: (topic: ITopic | undefined) => unknown
}

const SelectTopicStep = (props: ISelectTopicStepProps) => {
  const { onComplete } = props
  const { topics } = useTopicsContext()
  const currentGroup = useCurrentGroup()

  if (!currentGroup) {
    return <Text c='dimmed'>Please select a group first.</Text>
  }

  // Filter topics by current group
  const groupTopics = topics?.content.filter((topic) => topic.groupId === currentGroup.id)

  if (
    !GLOBAL_CONFIG.allow_suggested_topics &&
    (!groupTopics || groupTopics.length === 0) &&
    topics?.pageNumber === 0
  ) {
    return (
      <Text ta='center' fw='bold' my='md'>
        {currentGroup.name} is currently not searching for theses.
      </Text>
    )
  }

  return (
    <Stack>
      <TopicsFilters visible={['type']} />
      {!topics && (
        <Stack>
          <Skeleton height={48} />
          <Skeleton height={48} />
          <Skeleton height={48} />
        </Stack>
      )}
      <Accordion variant='separated'>
        {groupTopics?.map((topic) => (
          <TopicAccordionItem key={topic.topicId} topic={topic}>
            <Center mt='md'>
              <Button onClick={() => onComplete(topic)}>Apply for this Topic</Button>
            </Center>
          </TopicAccordionItem>
        ))}
        {GLOBAL_CONFIG.allow_suggested_topics && (
          <Accordion.Item value='custom'>
            <Accordion.Control>Suggest Topic</Accordion.Control>
            <Accordion.Panel>
              <Center>
                <Button onClick={() => onComplete(undefined)}>Suggest your own topic</Button>
              </Center>
            </Accordion.Panel>
          </Accordion.Item>
        )}
      </Accordion>
    </Stack>
  )
}

export default SelectTopicStep
