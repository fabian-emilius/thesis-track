import { IUser } from './user'
import { Group } from '../../types/group'

export enum ThesisState {
  WRITING = 'WRITING',
  SUBMITTED = 'SUBMITTED',
  GRADING = 'GRADING',
  COMPLETED = 'COMPLETED',
  ARCHIVED = 'ARCHIVED',
}

export interface IThesis {
  thesisId: string
  title: string
  type: string
  state: ThesisState
  startDate: string
  endDate: string
  students: IUser[]
  advisors: IUser[]
  supervisors: IUser[]
  group?: Group
}

export interface IThesisPresentation {
  presentationId: string
  thesis: IThesis
  date: string
  room: string
  published: boolean
}

export interface IPublishedPresentation extends IThesisPresentation {
  published: true
}

export interface IPublishedThesis {
  thesisId: string
  title: string
  type: string
  students: IUser[]
  advisors: IUser[]
  supervisors: IUser[]
  group?: Group
}

export function isPublishedPresentation(
  presentation: IThesisPresentation,
): presentation is IPublishedPresentation {
  return presentation.published
}

export function isThesis(thesis: IThesis | IPublishedThesis): thesis is IThesis {
  return 'state' in thesis
}
