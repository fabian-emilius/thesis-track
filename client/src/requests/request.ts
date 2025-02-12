import { ApiRequestOptions } from '../types/api';

const BASE_URL = process.env.REACT_APP_API_URL || '';

/**
 * Generic request function for API calls
 */
export const request = {
  async get<T>(path: string, options: ApiRequestOptions = {}): Promise<T> {
    const response = await fetch(`${BASE_URL}${path}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      credentials: 'include',
      signal: options.signal,
    });

    if (!response.ok) {
      throw await response.json();
    }

    return response.json();
  },

  async post<T>(path: string, data?: any, options: ApiRequestOptions = {}): Promise<T> {
    const response = await fetch(`${BASE_URL}${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      body: data instanceof FormData ? data : JSON.stringify(data),
      credentials: 'include',
      signal: options.signal,
    });

    if (!response.ok) {
      throw await response.json();
    }

    return response.json();
  },

  async put<T>(path: string, data: any, options: ApiRequestOptions = {}): Promise<T> {
    const response = await fetch(`${BASE_URL}${path}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      body: JSON.stringify(data),
      credentials: 'include',
      signal: options.signal,
    });

    if (!response.ok) {
      throw await response.json();
    }

    return response.json();
  },

  async delete<T>(path: string, options: ApiRequestOptions = {}): Promise<T> {
    const response = await fetch(`${BASE_URL}${path}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      credentials: 'include',
      signal: options.signal,
    });

    if (!response.ok) {
      throw await response.json();
    }

    return response.json();
  },
};
