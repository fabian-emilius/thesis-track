import { ThesisType, ThesisState, Language, PresentationType, ApplicationState } from '../types/enums';

export function formatUser(user: any): string {
  if (!user) return 'Unknown User';
  return [user.firstName, user.lastName].filter(Boolean).join(' ');
}

export function formatThesisType(type: ThesisType): string {
  const types = {
    [ThesisType.BACHELOR]: 'Bachelor Thesis',
    [ThesisType.MASTER]: 'Master Thesis',
  };
  return types[type] || type;
}

export function formatThesisState(state: ThesisState): string {
  const states = {
    [ThesisState.PROPOSED]: 'Proposed',
    [ThesisState.IN_PROGRESS]: 'In Progress',
    [ThesisState.SUBMITTED]: 'Submitted',
    [ThesisState.GRADED]: 'Graded',
    [ThesisState.COMPLETED]: 'Completed',
  };
  return states[state] || state;
}

export function formatLanguage(lang: Language): string {
  const languages = {
    [Language.DE]: 'German',
    [Language.EN]: 'English',
  };
  return languages[lang] || lang;
}

export function getDefaultLanguage(): Language {
  return Language.EN;
}

export function formatPresentationType(type: PresentationType): string {
  const types = {
    [PresentationType.PROPOSAL]: 'Proposal Presentation',
    [PresentationType.FINAL]: 'Final Presentation',
  };
  return types[type] || type;
}

export function formatApplicationState(state: ApplicationState): string {
  const states = {
    [ApplicationState.PENDING]: 'Pending',
    [ApplicationState.ACCEPTED]: 'Accepted',
    [ApplicationState.REJECTED]: 'Rejected',
    [ApplicationState.WITHDRAWN]: 'Withdrawn',
  };
  return states[state] || state;
}

export function formatApplicationFilename(filename: string): string {
  return filename.split('/').pop() || filename;
}

export function formatUserName(firstName?: string, lastName?: string): string {
  if (!firstName && !lastName) return 'Unknown User';
  return [firstName, lastName].filter(Boolean).join(' ');
}

export function formatDate(date: string | Date): string {
  return new Date(date).toLocaleDateString();
}

export function pluralize(count: number, singular: string, plural?: string): string {
  return count === 1 ? singular : (plural || `${singular}s`);
}
