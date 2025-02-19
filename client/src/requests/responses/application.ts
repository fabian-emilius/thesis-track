import { ITopic } from './topic'
import { ILightUser } from './user'

export interface IApplication {
  applicationId: string
  groupId: string
  topic?: ITopic
  thesisTitle: string | null
  thesisType: string
  desiredStartDate: string
  motivation: string
  state: string
  createdAt: string
  createdBy: ILightUser
  reviewedAt: string | null
  reviewedBy: ILightUser | null
  reviewComment: string | null
}
