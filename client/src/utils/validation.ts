import { ValidationError } from '../types/api';

/**
 * Checks if a user list is not empty
 * @param users - Array of user IDs
 * @returns boolean indicating if list is not empty
 */
export function isNotEmptyUserList(users?: string[]): boolean {
  return Array.isArray(users) && users.length > 0;
}

/**
 * Gets the length of text content from HTML string
 * @param html - HTML string
 * @returns Length of text content
 */
export function getHtmlTextLength(html: string): number {
  const div = document.createElement('div');
  div.innerHTML = html;
  return div.textContent?.length || 0;
}

/**
 * Validates group data
 * @param data - Group data to validate
 * @returns Array of validation errors
 */
export function validateGroupData(data: any): ValidationError[] {
  const errors: ValidationError[] = [];

  if (!data.name?.trim()) {
    errors.push({ field: 'name', message: 'Name is required' });
  }

  if (!data.description?.trim()) {
    errors.push({ field: 'description', message: 'Description is required' });
  }

  return errors;
}

/**
 * Validates member data
 * @param data - Member data to validate
 * @returns Array of validation errors
 */
export function validateMemberData(data: any): ValidationError[] {
  const errors: ValidationError[] = [];

  if (!data.userId) {
    errors.push({ field: 'userId', message: 'User ID is required' });
  }

  if (!data.role) {
    errors.push({ field: 'role', message: 'Role is required' });
  }

  return errors;
}
