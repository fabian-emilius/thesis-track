import { PropsWithChildren } from 'react'
import { Container, MantineSize, Stack } from '@mantine/core'
import { useParams } from 'react-router-dom'
import GroupBreadcrumbs from '../../../components/GroupBreadcrumbs/GroupBreadcrumbs'

interface IContentContainerProps {
  size?: MantineSize
}

const ContentContainer = (props: PropsWithChildren<IContentContainerProps>) => {
  const { size, children } = props
  const { groupSlug } = useParams<{ groupSlug: string }>()

  return (
    <Container my='md' size={size} fluid={!size}>
      <Stack gap='md'>
        {groupSlug && <GroupBreadcrumbs groupSlug={groupSlug} />}
        {children}
      </Stack>
    </Container>
  )
}

export default ContentContainer
