<template>
  <div class="ai-generate-panel">
    <el-button type="primary" @click="handleGenerate" :loading="generating" class="generate-btn">
      <el-icon><MagicStick /></el-icon> AI生成邀请语
    </el-button>

    <div v-if="results.length > 0" class="results-list">
      <div v-for="(text, idx) in results" :key="idx" class="result-item" @click="$emit('select', text)">
        <p>{{ text }}</p>
        <el-icon class="select-icon"><Check /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { generateGreeting } from '@/api/ai'
import { ElMessage } from 'element-plus'
import type { ActivityType } from '@/types'

const props = defineProps<{ activityType: ActivityType }>()
const emit = defineEmits<{ (e: 'select', text: string): void }>()

const generating = ref(false)
const results = ref<string[]>([])

const styleOptions = ['elegant', 'warm', 'formal', 'romantic']

async function handleGenerate() {
  generating.value = true
  try {
    const res = await generateGreeting({
      activityType: props.activityType,
      style: styleOptions[Math.floor(Math.random() * styleOptions.length)]
    })
    if (res.data?.result) {
      results.value.unshift(res.data.result)
    }
    ElMessage.success('AI生成成功')
  } catch {
    ElMessage.error('AI生成失败')
  } finally {
    generating.value = false
  }
}
</script>

<style scoped lang="scss">
.generate-btn {
  width: 100%;
  margin-bottom: 12px;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.result-item {
  padding: 12px;
  background: var(--bg-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  transition: background 0.2s;

  &:hover { background: var(--color-primary); color: #fff; }

  p {
    flex: 1;
    font-size: 13px;
    line-height: 1.6;
    margin: 0;
  }
}

.select-icon {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.2s;
}

.result-item:hover .select-icon {
  opacity: 1;
}
</style>
