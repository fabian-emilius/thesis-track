import React, { useEffect, useState } from 'react'
import { MultiSelect, MultiSelectProps } from '@mantine/core'
import { doRequest } from '../../requests/request'

interface IUser {
  id: string
  name: string
  email: string
}

type UserMultiSelectProps = Omit<MultiSelectProps, 'data'> & {
  excludeUsers?: string[]
}

export const UserMultiSelect: React.FC<UserMultiSelectProps> = ({ excludeUsers, ...props }) => {
  const [users, setUsers] = useState<IUser[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    void doRequest<IUser[]>('/v2/users', {
      method: 'GET',
      requiresAuth: true,
    }).then((response) => {
      if (response.ok) {
        setUsers(response.data)
      }
      setLoading(false)
    })
  }, [])

  const filteredUsers = excludeUsers
    ? users.filter((user) => !excludeUsers.includes(user.id))
    : users

  return (
    <MultiSelect
      {...props}
      data={filteredUsers.map((user) => ({
        value: user.id,
        label: `${user.name} (${user.email})`,
      }))}
      searchable
      clearable
      loading={loading}
    />
  )
}

export default UserMultiSelect
