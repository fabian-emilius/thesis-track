import DOMPurify from 'dompurify'

/**
 * Sanitizes HTML content to prevent XSS attacks
 * @param html - The HTML content to sanitize
 * @returns Sanitized HTML string
 */
export function sanitizeHtml(html: string): string {
  return DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ['p', 'b', 'i', 'em', 'strong', 'a', 'ul', 'ol', 'li'],
    ALLOWED_ATTR: ['href', 'target', 'rel'],
  })
}

/**
 * Escapes special characters in text to prevent XSS
 * @param text - The text to escape
 * @returns Escaped text string
 */
export function escapeText(text: string): string {
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

/**
 * Validates and sanitizes a URL string
 * @param url - The URL to validate and sanitize
 * @returns Sanitized URL or null if invalid
 */
export function sanitizeUrl(url?: string): string | null {
  if (!url) return null

  try {
    const parsed = new URL(url)
    return parsed.protocol === 'javascript:' ? null : parsed.toString()
  } catch {
    return null
  }
}

/**
 * Creates a nonce for CSP headers
 * @returns Random nonce string
 */
export function generateNonce(): string {
  return Math.random().toString(36).substring(2)
}
