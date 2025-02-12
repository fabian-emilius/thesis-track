import { Center, Checkbox, Grid, Stack, Select } from '@mantine/core'
import { GLOBAL_CONFIG } from '../../config/global'
import React from 'react'
import { useTopicsContext } from '../../providers/TopicsProvider/hooks'
import { formatThesisType } from '../../utils/format'
import { useGroups } from '../../hooks/useGroups'
import { useGroupAdvisors } from '../../hooks/useGroupAdvisors'

interface ITopicsFiltersProps {
  visible: Array<'type' | 'closed' | 'group' | 'advisor'>
  groupId?: string
}

const TopicsFilters = (props: ITopicsFiltersProps) => {
  const { visible, groupId } = props

  const { filters, setFilters } = useTopicsContext()
  const { data: advisors } = useGroupAdvisors(groupId)
  const { data: groups } = useGroups()

  return (
    <Stack spacing='md'>
      {visible.includes('group') && groups && (
        <Select
          label='Filter by Group'
          data={groups.map((g) => ({ value: g.id, label: g.name }))}
          value={filters.groupId}
          onChange={(value) => setFilters({ groupId: value })}
          clearable
        />
      )}
      {visible.includes('advisor') && advisors && (
        <Select
          label='Filter by Advisor'
          data={advisors.map((a) => ({ value: a.id, label: a.name }))}
          value={filters.advisorId}
          onChange={(value) => setFilters({ advisorId: value })}
          clearable
          disabled={!filters.groupId}
        />
      )}
      {visible.includes('closed') && (
        <Checkbox
          label='Show Closed Topics'
          checked={!!filters.includeClosed}
          onChange={(e) => {
            setFilters({
              includeClosed: e.target.checked,
            })
          }}
        />
      )}
      {visible.includes('type') && (
        <Grid grow>
          {Object.keys(GLOBAL_CONFIG.thesis_types).map((key) => (
            <Grid.Col key={key} span={{ md: 3 }}>
              <Center>
                <Checkbox
                  label={formatThesisType(key)}
                  checked={!!filters.types?.includes(key)}
                  onChange={(e) => {
                    setFilters((prev) => ({
                      types: [...(prev.types || []), key].filter(
                        (row) => e.target.checked || row !== key,
                      ),
                    }))
                  }}
                />
              </Center>
            </Grid.Col>
          ))}
        </Grid>
      )}
    </Stack>
  )
}

export default TopicsFilters
