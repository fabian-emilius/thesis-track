import { IApplication } from '../../requests/responses/application'
import { Stack, Group, Grid, Title, Badge, Accordion } from '@mantine/core'
import React, { ReactNode } from 'react'
import { GLOBAL_CONFIG } from '../../config/global'
import { AVAILABLE_COUNTRIES } from '../../config/countries'
import {
  formatApplicationFilename,
  formatApplicationState,
  formatDate,
  formatThesisType,
} from '../../utils/format'
import LabeledItem from '../LabeledItem/LabeledItem'
import DocumentEditor from '../DocumentEditor/DocumentEditor'
import { ApplicationStateColor } from '../../config/colors'
import TopicAccordionItem from '../TopicAccordionItem/TopicAccordionItem'
import { enrollmentDateToSemester } from '../../utils/converter'
import AuthenticatedFilePreview from '../AuthenticatedFilePreview/AuthenticatedFilePreview'

interface IApplicationDataProps {
  application: IApplication
  bottomSection?: ReactNode
  rightTitleSection?: ReactNode
  showGroupInfo?: boolean
  canManageGroup?: boolean
}

const ApplicationData = (props: IApplicationDataProps) => {
  const { application, bottomSection, rightTitleSection } = props

  return (
    <Grid>
      <Grid.Col span={{ md: 8 }} py={0}>
        <Stack>
          <Group justify="space-between" wrap="nowrap">
            <Group wrap="nowrap">
              <Title>
                {application.user.firstName} {application.user.lastName}
              </Title>
              {application.group && (
                <Badge size="lg" variant="light">
                  Group: {application.group.name}
                </Badge>
              )}
            </Group>
            {rightTitleSection}
          </Group>
          {application.topic ? (
            <Accordion variant='separated'>
              <TopicAccordionItem topic={application.topic} />
            </Accordion>
          ) : (
            <LabeledItem label='Thesis Title' value={application.thesisTitle} />
          )}
          <DocumentEditor label='Motivation' value={application.motivation} />
          <DocumentEditor label='Interests' value={application.user.interests || ''} />
          <DocumentEditor label='Projects' value={application.user.projects || ''} />
          <DocumentEditor label='Special Skills' value={application.user.specialSkills || ''} />
          <Grid>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Email'
                value={application.user.email}
                copyText={application.user.email || undefined}
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Gender'
                value={
                  GLOBAL_CONFIG.genders[application.user.gender || ''] ?? application.user.gender
                }
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Nationality'
                value={
                  AVAILABLE_COUNTRIES[application.user.nationality || ''] ??
                  application.user.nationality
                }
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='University ID'
                value={application.user.universityId}
                copyText={application.user.universityId}
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Matriculation Number'
                value={application.user.matriculationNumber}
                copyText={application.user.matriculationNumber || undefined}
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Study Degree'
                value={
                  GLOBAL_CONFIG.study_degrees[application.user.studyDegree || ''] ??
                  application.user.studyDegree
                }
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Study Program'
                value={
                  GLOBAL_CONFIG.study_programs[application.user.studyProgram || ''] ??
                  application.user.studyProgram
                }
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Semester'
                value={enrollmentDateToSemester(application.user.enrolledAt || '')}
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Desired Start Date'
                value={formatDate(application.desiredStartDate, { withTime: false })}
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem label='Thesis Type' value={formatThesisType(application.thesisType)} />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='Submission Date'
                value={formatDate(application.createdAt, { withTime: true })}
              />
            </Grid.Col>
            <Grid.Col span={{ xs: 4, sm: 3 }}>
              <LabeledItem
                label='State'
                value={
                  <Badge color={ApplicationStateColor[application.state]}>
                    {formatApplicationState(application.state)}
                  </Badge>
                }
              />
            </Grid.Col>
            {application.group && (
              <>
                <Grid.Col span={{ xs: 4, sm: 3 }}>
                  <LabeledItem
                    label='Group Role'
                    value={
                      <Badge color="blue">
                        {GLOBAL_CONFIG.group_roles[application.groupRole || ''] ?? application.groupRole}
                      </Badge>
                    }
                  />
                </Grid.Col>
                <Grid.Col span={{ xs: 4, sm: 3 }}>
                  <LabeledItem
                    label='Group Join Date'
                    value={formatDate(application.groupJoinedAt, { withTime: true })}
                  />
                </Grid.Col>
              </>
            )}
            {application.reviewedAt && (
              <Grid.Col span={{ xs: 4, sm: 3 }}>
                <LabeledItem
                  label='Reviewed At'
                  value={formatDate(application.reviewedAt, { withTime: true })}
                />
              </Grid.Col>
            )}
            {application.user.customData &&
              Object.entries(application.user.customData).map(([key, value]) => (
                <Grid.Col key={key} span={{ md: 6 }}>
                  <LabeledItem label={GLOBAL_CONFIG.custom_data[key]?.label ?? key} value={value} />
                </Grid.Col>
              ))}
          </Grid>
          {application.group && showGroupInfo && (
            <>
              <Title order={3}>Group Information</Title>
              <DocumentEditor label='Group Motivation' value={application.groupMotivation || ''} />
              <DocumentEditor label='Group Contribution' value={application.groupContribution || ''} />
              {application.groupFiles?.map((file, index) => (
                <AuthenticatedFilePreview
                  key={`${file.fileId}-${index}`}
                  url={`/v2/applications/${application.applicationId}/group-files/${file.fileId}`}
                  filename={file.filename}
                  type={file.type}
                  aspectRatio={16 / 11}
                />
              ))}
            </>
          )}
          {bottomSection}
        </Stack>
      </Grid.Col>
      <Grid.Col span={{ md: 4 }}>
        <Stack gap='md' key={application.applicationId}>
          {application.user.hasCv && (
            <AuthenticatedFilePreview
              url={`/v2/users/${application.user.userId}/cv`}
              filename={formatApplicationFilename(application, 'CV', 'file.pdf')}
              type='pdf'
              aspectRatio={16 / 11}
              key={application.user.userId}
            />
          )}
          {application.user.hasExaminationReport && (
            <AuthenticatedFilePreview
              url={`/v2/users/${application.user.userId}/examination-report`}
              filename={formatApplicationFilename(application, 'Examination Report', 'file.pdf')}
              type='pdf'
              aspectRatio={16 / 11}
              key={application.user.userId}
            />
          )}
          {application.user.hasDegreeReport && (
            <AuthenticatedFilePreview
              url={`/v2/users/${application.user.userId}/degree-report`}
              filename={formatApplicationFilename(application, 'Degree Report', 'file.pdf')}
              type='pdf'
              aspectRatio={16 / 11}
              key={application.user.userId}
            />
          )}
        </Stack>
      </Grid.Col>
    </Grid>
  )
}

export default ApplicationData
