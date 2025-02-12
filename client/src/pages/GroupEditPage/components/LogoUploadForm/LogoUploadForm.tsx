import { Group, Text, Stack, Button } from '@mantine/core'
import { Dropzone, IMAGE_MIME_TYPE } from '@mantine/dropzone'
import { useState } from 'react'

interface LogoUploadFormProps {
  currentLogo?: string
  onUpload: (file: File) => void
}

export function LogoUploadForm({ currentLogo, onUpload }: LogoUploadFormProps) {
  const [file, setFile] = useState<File | null>(null)

  const handleDrop = (files: File[]) => {
    if (files.length > 0) {
      setFile(files[0])
    }
  }

  const handleUpload = () => {
    if (file) {
      onUpload(file)
    }
  }

  return (
    <Stack>
      {currentLogo && (
        <div>
          <Text size='sm' mb='xs'>
            Current Logo
          </Text>
          <img src={currentLogo} alt='Current logo' style={{ maxWidth: 200, maxHeight: 200 }} />
        </div>
      )}

      <Dropzone onDrop={handleDrop} accept={IMAGE_MIME_TYPE} maxSize={5 * 1024 * 1024}>
        <Group justify='center' gap='xl' style={{ minHeight: 120, pointerEvents: 'none' }}>
          <div>
            <Text size='xl' inline>
              Drag logo here or click to select
            </Text>
            <Text size='sm' c='dimmed' inline mt={7}>
              File should not exceed 5MB
            </Text>
          </div>
        </Group>
      </Dropzone>

      {file && <Button onClick={handleUpload}>Upload New Logo</Button>}
    </Stack>
  )
}
