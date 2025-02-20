import React from 'react'
import { Stack, TextInput, Textarea } from '@mantine/core'
import { RichTextEditor } from '@mantine/tiptap'
import { useEditor } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextAlign from '@tiptap/extension-text-align'

/**
 * Props for the EmailTemplateEditor component
 * @interface EmailTemplateEditorProps
 */
interface EmailTemplateEditorProps {
  /** The HTML content of the email template */
  value: string
  /** Callback function called when the content changes */
  onChange: (value: string) => void
  /** Optional label for the editor */
  label?: string
  /** Optional description text */
  description?: string
  /** Optional placeholder text */
  placeholder?: string
  /** Optional error message */
  error?: string
  /** Optional array of variables that can be used in the template */
  variables?: Array<{ 
    /** The name of the variable to be used like {{variableName}} */
    name: string
    /** Description of what the variable represents */
    description: string 
  }>
}

/**
 * A rich text editor component for editing email templates with variable support.
 * 
 * @component
 * @example
 * ```tsx
 * // Basic usage
 * const [content, setContent] = useState('<p>Hello!</p>')
 * 
 * <EmailTemplateEditor
 *   value={content}
 *   onChange={setContent}
 *   label="Welcome Email Template"
 * />
 * 
 * // With variables
 * const variables = [
 *   { name: 'userName', description: 'The recipient\'s full name' },
 *   { name: 'groupName', description: 'The name of the user\'s group' }
 * ]
 * 
 * <EmailTemplateEditor
 *   value={content}
 *   onChange={setContent}
 *   variables={variables}
 *   label="Group Assignment Email"
 *   description="Template for notifying users about group assignments"
 * />
 * ```
 */
export const EmailTemplateEditor = ({
  value,
  onChange,
  label,
  description,
  placeholder,
  error,
  variables,
}: EmailTemplateEditorProps): JSX.Element => {
  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      TextAlign.configure({ types: ['heading', 'paragraph'] }),
    ],
    content: value,
    onUpdate: ({ editor }) => {
      onChange(editor.getHTML())
    },
  })

  return (
    <Stack gap="xs">
      {variables && (
        <Stack gap="xs">
          <TextInput
            label="Available Variables"
            description="Click to copy"
            readOnly
            value={variables.map(v => `{{${v.name}}}`).join(' ')}
            onClick={(e) => {
              const input = e.currentTarget
              input.select()
              void navigator.clipboard.writeText(input.value)
            }}
          />
          {variables.map((variable) => (
            <TextInput
              key={variable.name}
              size="xs"
              label={`{{${variable.name}}}`}
              description={variable.description}
              readOnly
              variant="filled"
            />
          ))}
        </Stack>
      )}

      <RichTextEditor
        editor={editor}
        styles={{
          root: { minHeight: 200 },
          content: { minHeight: 150 },
        }}
      >
        <RichTextEditor.Toolbar sticky stickyOffset={0}>
          <RichTextEditor.ControlsGroup>
            <RichTextEditor.Bold />
            <RichTextEditor.Italic />
            <RichTextEditor.Underline />
            <RichTextEditor.Strikethrough />
          </RichTextEditor.ControlsGroup>

          <RichTextEditor.ControlsGroup>
            <RichTextEditor.H1 />
            <RichTextEditor.H2 />
            <RichTextEditor.H3 />
          </RichTextEditor.ControlsGroup>

          <RichTextEditor.ControlsGroup>
            <RichTextEditor.AlignLeft />
            <RichTextEditor.AlignCenter />
            <RichTextEditor.AlignRight />
          </RichTextEditor.ControlsGroup>

          <RichTextEditor.ControlsGroup>
            <RichTextEditor.BulletList />
            <RichTextEditor.OrderedList />
          </RichTextEditor.ControlsGroup>
        </RichTextEditor.Toolbar>

        <RichTextEditor.Content />
      </RichTextEditor>
    </Stack>
  )
}
