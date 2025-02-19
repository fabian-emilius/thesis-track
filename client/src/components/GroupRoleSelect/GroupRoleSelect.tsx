import React from 'react'
import { Select } from '@mantine/core'
import { GroupMember } from '../../requests/responses/group'

interface GroupRoleSelectProps {
  value: GroupMember['role']
  onChange: (value: GroupMember['role']) => void
  disabled?: boolean
}

const GroupRoleSelect: React.FC<GroupRoleSelectProps> = ({ value, onChange, disabled }) => {
  return (
    <Select
      label='Role'
      value={value}
      onChange={(val) => onChange(val as GroupMember['role'])}
      data={[
        { value: 'SUPERVISOR', label: 'Supervisor' },
        { value: 'ADVISOR', label: 'Advisor' },
        { value: 'GROUP_ADMIN', label: 'Group Admin' },
      ]}
      disabled={disabled}
      required
    />
  )
}

export default GroupRoleSelect
