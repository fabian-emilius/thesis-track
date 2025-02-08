import { encode } from 'html-entities';

/**
 * Sanitizes a string to prevent XSS attacks
 * @param input - The string to sanitize
 * @returns The sanitized string
 */
export function sanitizeHtml(input: string): string {
  return encode(input);
}

/**
 * Validates and sanitizes group data
 * @param data - The group data to validate
 * @returns The sanitized group data
 */
export function validateGroupData<T extends Record<string, unknown>>(data: T): T {
  const sanitized: Record<string, unknown> = {};
  
  for (const [key, value] of Object.entries(data)) {
    if (typeof value === 'string') {
      sanitized[key] = sanitizeHtml(value);
    } else {
      sanitized[key] = value;
    }
  }

  return sanitized as T;
}

/**
 * Creates a Content Security Policy nonce
 * @returns A random nonce string
 */
export function generateCspNonce(): string {
  return Buffer.from(crypto.getRandomValues(new Uint8Array(16))).toString('base64');
}
