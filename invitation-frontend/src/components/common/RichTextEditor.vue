<template>
  <div class="rich-text-editor">
    <div style="border: 1px solid #ccc">
      <Toolbar
        style="border-bottom: 1px solid #ccc"
        :editor="editorRef"
        :defaultConfig="toolbarConfig"
        mode="default"
      />
      <Editor
        style="height: 300px; overflow-y: hidden"
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        mode="default"
        @onCreated="handleCreated"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css'
import { onBeforeUnmount, ref, shallowRef, watch } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import type { IDomEditor, IEditorConfig, IToolbarConfig } from '@wangeditor/editor'
import { uploadImage } from '@/api/upload'

const props = defineProps<{ modelValue: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>()

const editorRef = shallowRef<IDomEditor>()
const valueHtml = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  if (val !== valueHtml.value) {
    valueHtml.value = val
  }
})

watch(valueHtml, (val) => {
  emit('update:modelValue', val)
})

const toolbarConfig: Partial<IToolbarConfig> = {
  excludeKeys: ['group-video']
}

const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file: File, insertFn: (url: string, alt?: string, href?: string) => void) {
        try {
          const formData = new FormData()
          formData.append('file', file)
          const res = await uploadImage(formData)
          insertFn(res.data.url)
        } catch {
          // upload failed
        }
      }
    }
  }
}

function handleCreated(editor: IDomEditor) {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
</script>
