import { RichTextEditor, Link } from '@mantine/tiptap'
import { useEditor } from '@tiptap/react'
import Highlight from '@tiptap/extension-highlight'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextAlign from '@tiptap/extension-text-align'
import Superscript from '@tiptap/extension-superscript'
import SubScript from '@tiptap/extension-subscript'
import { ChangeEvent, useEffect, useRef } from 'react'
import { Input } from '@mantine/core'
import { InputWrapperProps } from '@mantine/core/lib/components/Input/InputWrapper/InputWrapper'

interface IDocumentEditorProps extends InputWrapperProps {
  value?: string
  onChange?: (e: ChangeEvent<HTMLInputElement>) => unknown
  editMode?: boolean
}

const DocumentEditor = (props: IDocumentEditorProps) => {
  const { value, onChange, editMode = false, onBlur, onFocus, ...wrapperProps } = props

  const onChangeRef = useRef(onChange)
  const inputRef = useRef<HTMLInputElement>(null)

  const editor = useEditor({
    editable: editMode,
    extensions: [
      StarterKit,
      Underline,
      Link,
      Superscript,
      SubScript,
      Highlight,
      TextAlign.configure({ types: ['heading', 'paragraph'] }),
    ],
    content: value,
    onUpdate: ({ editor: x }) => {
      const newValue = x.getText() === '' ? '' : x.getHTML()

      if (inputRef.current) {
        if (inputRef.current.value === newValue) {
          return
        }

        inputRef.current.value = newValue
        onChangeRef.current?.({
          target: inputRef.current,
          currentTarget: inputRef.current,
          preventDefault: () => {},
          stopPropagation: () => {},
          isDefaultPrevented: () => false,
          isPropagationStopped: () => false,
          nativeEvent: {} as Event,
          bubbles: false,
          cancelable: false,
          defaultPrevented: false,
          eventPhase: 0,
          isTrusted: true,
          timeStamp: Date.now(),
          type: 'change',
          persist: () => {},
        })
      }
    },
  })

  useEffect(() => {
    if (editor) {
      if (
        typeof value !== 'undefined' &&
        editor.getHTML() !== value &&
        editor.getText() !== value
      ) {
        editor.commands.setContent(value)
      }

      editor.setEditable(editMode)
    }
  }, [value, editMode, editor])

  return (
    <Input.Wrapper {...wrapperProps}>
      <RichTextEditor
        editor={editor}
        onBlur={onBlur}
        onFocus={onFocus}
        style={{ minHeight: '170px' }}
      >
        {editMode && (
          <RichTextEditor.Toolbar sticky stickyOffset={60}>
            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Bold />
              <RichTextEditor.Italic />
              <RichTextEditor.Underline />
              <RichTextEditor.Strikethrough />
              <RichTextEditor.ClearFormatting />
              <RichTextEditor.Highlight />
              <RichTextEditor.Code />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Blockquote />
              <RichTextEditor.Hr />
              <RichTextEditor.BulletList />
              <RichTextEditor.OrderedList />
              <RichTextEditor.Subscript />
              <RichTextEditor.Superscript />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Link />
              <RichTextEditor.Unlink />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.AlignLeft />
              <RichTextEditor.AlignCenter />
              <RichTextEditor.AlignJustify />
              <RichTextEditor.AlignRight />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Undo />
              <RichTextEditor.Redo />
            </RichTextEditor.ControlsGroup>
          </RichTextEditor.Toolbar>
        )}
        <RichTextEditor.Content />
      </RichTextEditor>
      <input
        type='text'
        value={value ?? ''}
        onChange={onChange}
        ref={inputRef}
        style={{ display: 'none' }}
      />
    </Input.Wrapper>
  )
}

export default DocumentEditor
