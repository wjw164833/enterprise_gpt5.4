<template>
  <el-form :model="localForm" label-width="100px" label-position="top">
    <el-form-item label="邀请函标题" required>
      <el-input v-model="localForm.title" placeholder="请输入邀请函标题" maxlength="128" show-word-limit />
    </el-form-item>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="localForm.activityDate"
            type="datetime"
            placeholder="选择活动时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="活动地点">
          <el-input v-model="localForm.activityAddress" placeholder="活动地址" />
        </el-form-item>
      </el-col>
    </el-row>

    <el-form-item label="AI邀请语">
      <div class="ai-greeting-section">
        <el-input
          v-model="localForm.aiGreeting"
          type="textarea"
          :rows="3"
          placeholder="AI将为您生成邀请语，或手动输入"
        />
        <el-button type="primary" link @click="$emit('aiGenerate')">
          <el-icon><MagicStick /></el-icon> AI生成
        </el-button>
      </div>
    </el-form-item>

    <el-form-item label="邀请函内容">
      <RichTextEditor v-model="localForm.content" />
    </el-form-item>

    <el-form-item label="背景音乐">
      <el-select v-model="localForm.musicId" placeholder="选择背景音乐" clearable style="width: 100%">
        <el-option v-for="m in musicOptions" :key="m.id" :label="`${m.name} - ${m.artist}`" :value="m.id" />
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import { getMusicLibrary } from '@/api/music'
import RichTextEditor from '@/components/common/RichTextEditor.vue'
import type { MusicInfo } from '@/types'

const props = defineProps<{
  form: {
    title: string
    activityType: number
    templateId: number
    coverImage: string
    content: string
    activityDate: string
    activityAddress: string
    latitude?: number
    longitude?: number
    musicId?: number
    aiGreeting: string
  }
}>()

const emit = defineEmits<{
  (e: 'update:form', value: typeof props.form): void
  (e: 'aiGenerate'): void
}>()

const localForm = reactive({ ...props.form })
const musicOptions = ref<MusicInfo[]>([])

watch(() => props.form, (val) => {
  Object.assign(localForm, val)
}, { deep: true })

watch(localForm, (val) => {
  emit('update:form', val)
}, { deep: true })

async function fetchMusic() {
  try {
    const res = await getMusicLibrary({ category: undefined, page: 1, size: 50 })
    musicOptions.value = res.data
  } catch { /* ignore */ }
}

fetchMusic()
</script>

<script lang="ts">
import { ref } from 'vue'
</script>

<style scoped lang="scss">
.ai-greeting-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
