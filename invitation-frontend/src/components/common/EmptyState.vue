<template>
  <div class="empty-state">
    <div class="empty-icon">{{ icon }}</div>
    <h4 class="empty-title">{{ title }}</h4>
    <p class="empty-desc">{{ description }}</p>
    <el-button v-if="actionText" type="primary" @click="$emit('action')">{{ actionText }}</el-button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  icon?: string
  title?: string
  description?: string
  actionText?: string
}>()

defineEmits<{ (e: 'action'): void }>()
</script>

<style scoped lang="scss">
.empty-state {
  text-align: center;
  padding: 48px 24px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-primary);
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-color-secondary);
  margin-bottom: 16px;
}
</style>
