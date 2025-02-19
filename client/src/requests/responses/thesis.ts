import { IUser } from './user';
import { Group } from '../../types/group';

export interface IThesis {
  thesisId: string;
  title: string;
  type: string;
  state: string;
  startDate: string;
  endDate: string;
  students: IUser[];
  advisors: IUser[];
  supervisors: IUser[];
  group?: Group;
}
