<template>
  <div class="error-state">
    <div class="error-icon">⚠️</div>
    <h4 class="error-title">{{ title }}</h4>
    <p class="error-desc">{{ description }}</p>
    <el-button type="primary" @click="$emit('retry')">{{ retryText }}</el-button>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  title?: string
  description?: string
  retryText?: string
}>(), {
  title: '加载失败',
  description: '请检查网络后重试',
  retryText: '重试'
})

defineEmits<{ (e: 'retry'): void }>()
</script>

<style scoped lang="scss">
.error-state {
  text-align: center;
  padding: 48px 24px;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-title {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
  margin-bottom: 8px;
}

.error-desc {
  font-size: 14px;
  color: var(--text-color-secondary);
  margin-bottom: 16px;
}
</style>
