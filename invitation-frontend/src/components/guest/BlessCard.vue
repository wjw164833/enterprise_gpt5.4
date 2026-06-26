<template>
  <div class="bless-card" :style="{ background: bgColor }">
    <div class="card-avatar">{{ bless.guestName.charAt(0) }}</div>
    <div class="card-name">{{ bless.guestName }}</div>
    <div class="card-content">{{ bless.content }}</div>
    <div class="card-time">{{ formatRelativeTime(bless.createdAt) }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { formatRelativeTime } from '@/utils/format'
import type { BlessMessage } from '@/types'

const props = defineProps<{ bless: BlessMessage }>()

const themes: Record<string, string> = {
  '': 'rgba(255,255,255,0.08)',
  'love': 'rgba(245, 175, 25, 0.12)',
  'star': 'rgba(64, 158, 255, 0.12)',
  'flower': 'rgba(245, 154, 180, 0.12)',
  'default': 'rgba(255,255,255,0.08)'
}

const bgColor = computed(() => themes[props.bless.theme] || themes['default'])
</script>

<style scoped lang="scss">
.bless-card {
  border-radius: 12px;
  padding: 14px;
  backdrop-filter: blur(10px);
}

.card-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.card-name {
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 6px;
}

.card-content {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255,255,255,0.85);
  margin-bottom: 6px;
  word-break: break-word;
}

.card-time {
  font-size: 11px;
  color: rgba(255,255,255,0.4);
}
</style>
