<template>
  <div class="skeleton-loader">
    <div v-for="i in rows" :key="i" class="skeleton-row" :style="{ width: getWidth(i) }">
      <div v-if="avatar" class="skeleton-avatar shimmer-loading"></div>
      <div class="skeleton-lines">
        <div class="skeleton-line shimmer-loading" :style="{ width: '100%' }"></div>
        <div v-if="i === 1" class="skeleton-line short shimmer-loading" style="width: 60%"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  rows?: number
  avatar?: boolean
}>()

function getWidth(index: number): string {
  return index === 1 ? '100%' : `${80 + Math.random() * 20}%`
}
</script>

<style scoped lang="scss">
.skeleton-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: flex-start;
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.skeleton-lines {
  flex: 1;
}

.skeleton-line {
  height: 14px;
  border-radius: 4px;
  margin-bottom: 8px;

  &.short {
    height: 12px;
  }
}
</style>
