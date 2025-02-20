import { IUser } from './user'
import { Group } from '../../types/group'

export interface ITopic {
  topicId: string
  title: string
  description: string
  thesisTypes?: string[]
  advisors: IUser[]
  supervisors: IUser[]
  createdAt: string
  closedAt?: string
  group?: Group
}
