import { SimpleGrid, TextInput, Container, Text, Center, Loader } from '@mantine/core'
import { useState } from 'react'
import { useGroups } from '../../hooks/useGroups'
import { GroupCard } from '../GroupCard/GroupCard'
import { EmptyState } from '../EmptyState/EmptyState'
import { useDebounce } from '../../hooks/useDebounce'

export function GroupGrid() {
  const [search, setSearch] = useState('')
  const debouncedSearch = useDebounce(search, 300)
  const { data: groups, isLoading, error } = useGroups({ search: debouncedSearch })

  if (error) {
    return (
      <Center h={200}>
        <Text c='red'>Failed to load groups. Please try again later.</Text>
      </Center>
    )
  }

  if (isLoading) {
    return (
      <Center h={200}>
        <Loader size='lg' />
      </Center>
    )
  }

  return (
    <Container size='xl'>
      <TextInput
        placeholder='Search groups...'
        mb='xl'
        value={search}
        onChange={(event) => setSearch(event.currentTarget.value)}
      />
      {groups?.length === 0 ? (
        <EmptyState
          title='No groups found'
          description='Try adjusting your search or create a new group'
          icon='users'
        />
      ) : (
        <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing='lg' verticalSpacing='xl'>
          {groups?.map((group) => <GroupCard key={group.id} group={group} />)}
        </SimpleGrid>
      )}
    </Container>
  )
}
