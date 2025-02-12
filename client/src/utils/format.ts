/**
 * Formats a user's name for display
 * @param firstName - User's first name
 * @param lastName - User's last name
 * @returns Formatted full name
 */
export function formatUserName(firstName?: string, lastName?: string): string {
  if (!firstName && !lastName) return 'Unknown User';
  return [firstName, lastName].filter(Boolean).join(' ');
}

/**
 * Formats a date string to localized format
 * @param date - Date string or Date object
 * @returns Formatted date string
 */
export function formatDate(date: string | Date): string {
  return new Date(date).toLocaleDateString();
}
