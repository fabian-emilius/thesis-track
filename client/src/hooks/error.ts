import { useCallback } from 'react';
import { notifications } from '@mantine/notifications';
import { ApiError } from '../types/api';

/**
 * Hook for consistent error handling across the application
 */
export function useErrorHandler() {
  const handleError = useCallback((error: unknown) => {
    if (error instanceof Error) {
      const apiError = error as ApiError;
      
      notifications.show({
        title: 'Error',
        message: apiError.message || 'An unexpected error occurred',
        color: 'red',
      });

      // Log error for monitoring
      console.error('Application error:', {
        message: apiError.message,
        code: apiError.code,
        details: apiError.details,
      });
    } else {
      notifications.show({
        title: 'Error',
        message: 'An unexpected error occurred',
        color: 'red',
      });

      console.error('Unknown error:', error);
    }
  }, []);

  return { handleError };
}
