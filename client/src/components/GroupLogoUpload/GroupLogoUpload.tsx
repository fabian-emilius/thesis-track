import React, { useState } from 'react'
import { Group, Text, useMantineTheme, rem } from '@mantine/core'
import { Dropzone, IMAGE_MIME_TYPE } from '@mantine/dropzone'
import { IconUpload, IconPhoto, IconX } from '@tabler/icons-react'

interface GroupLogoUploadProps {
  /** Callback function called when a file is successfully uploaded */
  onUpload: (file: File) => void
  /** Maximum file size in bytes (default: 5MB) */
  maxSize?: number
  /** Optional error message to display */
  error?: string
  /** Optional loading state */
  loading?: boolean
}

/**
 * Component for uploading group logo images
 * Supports drag and drop or click to upload
 * Validates file type and size
 */
const GroupLogoUpload: React.FC<GroupLogoUploadProps> = ({
  onUpload,
  maxSize = 5 * 1024 * 1024,
  error: externalError,
  loading,
}) => {
  const theme = useMantineTheme()
  const [error, setError] = useState<string | null>(null)

  const displayError = externalError || error

  return (
    <Dropzone
      onDrop={(files) => {
        setError(null)
        onUpload(files[0])
      }}
      onReject={() => setError('Invalid file')}
      maxSize={maxSize}
      accept={IMAGE_MIME_TYPE}
      multiple={false}
      loading={loading}
    >
      <Group position='center' spacing='xl' style={{ minHeight: rem(220), pointerEvents: 'none' }}>
        <Dropzone.Accept>
          <IconUpload
            size='3.2rem'
            stroke={1.5}
            color={theme.colors[theme.primaryColor][theme.colorScheme === 'dark' ? 4 : 6]}
          />
        </Dropzone.Accept>
        <Dropzone.Reject>
          <IconX
            size='3.2rem'
            stroke={1.5}
            color={theme.colors.red[theme.colorScheme === 'dark' ? 4 : 6]}
          />
        </Dropzone.Reject>
        <Dropzone.Idle>
          <IconPhoto size='3.2rem' stroke={1.5} />
        </Dropzone.Idle>

        <div>
          <Text size='xl' inline>
            Drag group logo here or click to select
          </Text>
          <Text size='sm' color='dimmed' inline mt={7}>
            Upload a square image, preferably 512x512px (max {maxSize / 1024 / 1024}MB)
          </Text>
          {displayError && (
            <Text color='red' size='sm' mt={5}>
              {displayError}
            </Text>
          )}
        </div>
      </Group>
    </Dropzone>
  )
}

export default GroupLogoUpload
