import { showSimpleSuccess } from '../../../../utils/notification'
import UserInformationForm from '../../../../components/UserInformationForm/UserInformationForm'
import React from 'react'
import { usePageTitle } from '../../../../hooks/theme'
import { Stack, Tabs } from '@mantine/core'
import { GroupMemberships } from '../../../../components/GroupMemberships/GroupMemberships'
import { GroupPreferences } from '../../../../components/GroupPreferences/GroupPreferences'
import { GroupActivity } from '../../../../components/GroupActivity/GroupActivity'
import { GroupNotifications } from '../../../../components/GroupNotifications/GroupNotifications'

const MyInformation = () => {
  usePageTitle('My Information')

  return (
    <Stack>
      <UserInformationForm
        requireCompletion={false}
        includeAvatar={true}
        onComplete={() => showSimpleSuccess('You successfully updated your profile')}
      />
      
      <Tabs defaultValue="memberships">
        <Tabs.List>
          <Tabs.Tab value="memberships">Group Memberships</Tabs.Tab>
          <Tabs.Tab value="preferences">Group Preferences</Tabs.Tab>
          <Tabs.Tab value="activity">Activity History</Tabs.Tab>
          <Tabs.Tab value="notifications">Notifications</Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="memberships">
          <GroupMemberships />
        </Tabs.Panel>
        
        <Tabs.Panel value="preferences">
          <GroupPreferences />
        </Tabs.Panel>
        
        <Tabs.Panel value="activity">
          <GroupActivity />
        </Tabs.Panel>
        
        <Tabs.Panel value="notifications">
          <GroupNotifications />
        </Tabs.Panel>
      </Tabs>
    </Stack>
  )
}

export default MyInformation
