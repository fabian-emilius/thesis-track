import { ValidationError } from '../types/api';

/**
 * Validates group name according to requirements
 * @param name - The group name to validate
 * @returns Array of validation errors or empty array if valid
 */
export function validateGroupName(name: string): ValidationError[] {
  const errors: ValidationError[] = [];
  
  if (!name.trim()) {
    errors.push({
      field: 'name',
      message: 'Name is required',
    });
  }

  if (name.length > 100) {
    errors.push({
      field: 'name',
      message: 'Name must be less than 100 characters',
    });
  }

  return errors;
}

/**
 * Validates group description according to requirements
 * @param description - The group description to validate
 * @returns Array of validation errors or empty array if valid
 */
export function validateGroupDescription(description: string): ValidationError[] {
  const errors: ValidationError[] = [];
  
  if (!description.trim()) {
    errors.push({
      field: 'description',
      message: 'Description is required',
    });
  }

  if (description.length > 1000) {
    errors.push({
      field: 'description',
      message: 'Description must be less than 1000 characters',
    });
  }

  return errors;
}

/**
 * Validates a URL string
 * @param url - The URL to validate
 * @returns Array of validation errors or empty array if valid
 */
export function validateUrl(url?: string): ValidationError[] {
  const errors: ValidationError[] = [];
  
  if (url && !url.match(/^https?:\/\/.+/)) {
    errors.push({
      field: 'url',
      message: 'Invalid URL format',
    });
  }

  return errors;
}

/**
 * Validates file size and type
 * @param file - The file to validate
 * @param maxSize - Maximum file size in bytes
 * @param allowedTypes - Array of allowed MIME types
 * @returns Array of validation errors or empty array if valid
 */
export function validateFile(
  file: File,
  maxSize: number,
  allowedTypes: string[]
): ValidationError[] {
  const errors: ValidationError[] = [];
  
  if (file.size > maxSize) {
    errors.push({
      field: 'file',
      message: `File size must be less than ${maxSize / 1024 / 1024}MB`,
    });
  }

  if (!allowedTypes.includes(file.type)) {
    errors.push({
      field: 'file',
      message: 'Invalid file type',
    });
  }

  return errors;
}
