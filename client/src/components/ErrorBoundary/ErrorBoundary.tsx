import React, { Component, ErrorInfo, ReactNode } from 'react'
import { Container, Title, Text, Button, Stack } from '@mantine/core'

interface Props {
  children: ReactNode
  fallback?: ReactNode
}

interface State {
  hasError: boolean
  error?: Error
}

/**
 * Error Boundary component to catch and handle React errors
 */
export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
  }

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught error:', error, errorInfo)
  }

  private handleReset = () => {
    this.setState({ hasError: false, error: undefined })
  }

  public render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback
      }

      return (
        <Container size='sm'>
          <Stack align='center' mt='xl'>
            <Title order={2}>Something went wrong</Title>
            <Text c='dimmed'>{this.state.error?.message || 'An unexpected error occurred'}</Text>
            <Button onClick={this.handleReset}>Try again</Button>
          </Stack>
        </Container>
      )
    }

    return this.props.children
  }
}
