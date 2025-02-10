import React, { useEffect } from 'react'
import { notifications } from '@mantine/notifications'
import { useGroupContext } from '../../providers/GroupContext/GroupProvider'

const GroupChangeNotification: React.FC = () => {
  const { currentGroup } = useGroupContext()

  useEffect(() => {
    if (currentGroup) {
      notifications.show({
        title: 'Group Changed',
        message: `You are now viewing ${currentGroup.name}`,
        color: 'blue',
      })
    }
  }, [currentGroup?.id])

  return null
}

export default GroupChangeNotification
