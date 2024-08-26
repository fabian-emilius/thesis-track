import { IPublishedThesis, isThesis, IThesis } from '../../requests/responses/thesis'
import { Grid, Stack } from '@mantine/core'
import LabeledItem from '../LabeledItem/LabeledItem'
import { formatDate, formatUser } from '../../utils/format'
import ThesisStateBadge from '../ThesisStateBadge/ThesisStateBadge'
import DocumentEditor from '../DocumentEditor/DocumentEditor'
import { GLOBAL_CONFIG } from '../../config/global'

type availableAdditionalInformation = 'title' | 'abstract' | 'info' | 'state' | 'keywords'

interface IThesisDataProps {
  thesis: IThesis | IPublishedThesis
  additionalInformation?: availableAdditionalInformation[]
}

const ThesisData = (props: IThesisDataProps) => {
  const { thesis, additionalInformation = [] } = props

  return (
    <Stack gap='md'>
      {additionalInformation.includes('title') && (
        <LabeledItem label='Title' value={thesis.title} />
      )}
      <Grid>
        <Grid.Col span={{ md: 4 }}>
          <LabeledItem
            label='Supervisor'
            value={thesis.supervisors.map((supervisor) => formatUser(supervisor)).join(', ')}
          />
        </Grid.Col>
        <Grid.Col span={{ md: 4 }}>
          <LabeledItem
            label='Advisor'
            value={thesis.advisors.map((advisor) => formatUser(advisor)).join(', ')}
          />
        </Grid.Col>
        <Grid.Col span={{ md: 4 }}>
          <LabeledItem
            label='Student'
            value={thesis.students.map((student) => formatUser(student)).join(', ')}
          />
        </Grid.Col>
        <Grid.Col span={{ md: 4 }}>
          <LabeledItem
            label='Thesis Type'
            value={GLOBAL_CONFIG.thesis_types[thesis.type] ?? thesis.type}
          />
        </Grid.Col>
        {thesis.startDate && (
          <Grid.Col span={{ md: 4 }}>
            <LabeledItem
              label='Start Date'
              value={formatDate(thesis.startDate, { withTime: false })}
            />
          </Grid.Col>
        )}
        {thesis.endDate && (
          <Grid.Col span={{ md: 4 }}>
            <LabeledItem label='End Date' value={formatDate(thesis.endDate, { withTime: false })} />
          </Grid.Col>
        )}
        {additionalInformation.includes('keywords') &&
          isThesis(thesis) &&
          thesis.keywords.length > 0 && (
            <Grid.Col span={{ md: 4 }}>
              <LabeledItem label='Keywords' value={thesis.keywords.join(', ')} />
            </Grid.Col>
          )}
        {additionalInformation.includes('state') && isThesis(thesis) && (
          <Grid.Col span={{ md: 4 }}>
            <LabeledItem label='State' value={<ThesisStateBadge state={thesis.state} />} />
          </Grid.Col>
        )}
      </Grid>
      {additionalInformation.includes('abstract') && (
        <DocumentEditor label='Abstract' value={thesis.abstractText} />
      )}
      {additionalInformation.includes('info') && isThesis(thesis) && (
        <DocumentEditor label='Additional Information' value={thesis.infoText} />
      )}
    </Stack>
  )
}

export default ThesisData