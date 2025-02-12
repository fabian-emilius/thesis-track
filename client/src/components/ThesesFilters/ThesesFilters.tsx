import React from 'react'
import { Grid, MultiSelect, Select, TextInput, SegmentedControl } from '@mantine/core'
import { useThesesContext } from '../../providers/ThesesProvider/hooks'
import { useGroupContext } from '../../providers/GroupProvider/hooks'
import { ThesisState } from '../../requests/responses/thesis'
import { MagnifyingGlass, Users } from 'phosphor-react'
import { formatThesisState, formatThesisType, formatUserName } from '../../utils/format'
import { GLOBAL_CONFIG } from '../../config/global'

const ThesesFilters = () => {
  const { filters, setFilters, sort, setSort, presets, activePreset, setActivePreset } =
    useThesesContext()
  const { currentGroup, groupMembers } = useGroupContext()

  return (
    <Grid gutter='xs'>
      {currentGroup && (
        <Grid.Col span={12}>
          <SegmentedControl
            fullWidth
            data={[
              { label: 'All Theses', value: 'all' },
              { label: 'My Group', value: 'group' },
              { label: 'My Theses', value: 'personal' },
              ...(presets?.map((p) => ({ label: p.name, value: p.id })) || []),
            ]}
            value={activePreset}
            onChange={setActivePreset}
          />
        </Grid.Col>
      )}
      <Grid.Col span={6}>
        <TextInput
          label='Search'
          placeholder='Search theses...'
          leftSection={<MagnifyingGlass size={16} />}
          value={filters.search || ''}
          onChange={(x) => setFilters((prev) => ({ ...prev, search: x.target.value || undefined }))}
        />
      </Grid.Col>
      <Grid.Col span={6}>
        <Select
          label='Sort By'
          data={[
            { label: 'Start Date Ascending', value: 'startDate:asc' },
            { label: 'Start Date Descending', value: 'startDate:desc' },
            { label: 'Created Ascending', value: 'createdAt:asc' },
            { label: 'Created Descending', value: 'createdAt:desc' },
          ]}
          value={sort.column + ':' + sort.direction}
          onChange={(x) =>
            setSort({
              column: (x?.split(':')[0] || 'startDate') as any,
              direction: (x?.split(':')[1] || 'asc') as any,
            })
          }
        />
      </Grid.Col>
      <Grid.Col span={6}>
        <MultiSelect
          hidePickedOptions
          label='Type'
          placeholder='Thesis Types'
          data={Object.keys(GLOBAL_CONFIG.thesis_types).map((key) => ({
            value: key,
            label: formatThesisType(key),
          }))}
          value={filters.types || []}
          onChange={(x) =>
            setFilters((prev) => ({
              ...prev,
              types: x,
            }))
          }
        />
      </Grid.Col>
      <Grid.Col span={6}>
        <MultiSelect
          hidePickedOptions
          label='State'
          placeholder='Thesis States'
          data={Object.values(ThesisState).map((value) => ({
            value: value,
            label: formatThesisState(value),
          }))}
          value={filters.states || []}
          onChange={(x) =>
            setFilters((prev) => ({
              ...prev,
              states: x as ThesisState[],
            }))
          }
        />
      </Grid.Col>
      {currentGroup && (
        <Grid.Col span={6}>
          <MultiSelect
            hidePickedOptions
            label='Group Members'
            placeholder='Filter by group members'
            icon={<Users size={16} />}
            data={groupMembers.map((member) => ({
              value: member.id,
              label: formatUserName(member),
            }))}
            value={filters.memberIds || []}
            onChange={(x) =>
              setFilters((prev) => ({
                ...prev,
                memberIds: x,
              }))
            }
          />
        </Grid.Col>
      )}
    </Grid>
  )
}

export default ThesesFilters
