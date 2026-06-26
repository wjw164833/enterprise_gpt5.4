<template>
  <div class="countdown-timer">
    <div class="countdown-label">距离活动开始</div>
    <div class="countdown-grid">
      <div class="countdown-item">
        <span class="countdown-num">{{ days }}</span>
        <span class="countdown-unit">天</span>
      </div>
      <div class="countdown-sep">:</div>
      <div class="countdown-item">
        <span class="countdown-num">{{ hours }}</span>
        <span class="countdown-unit">时</span>
      </div>
      <div class="countdown-sep">:</div>
      <div class="countdown-item">
        <span class="countdown-num">{{ minutes }}</span>
        <span class="countdown-unit">分</span>
      </div>
      <div class="countdown-sep">:</div>
      <div class="countdown-item">
        <span class="countdown-num">{{ seconds }}</span>
        <span class="countdown-unit">秒</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps<{ targetDate: string }>()

const days = ref(0)
const hours = ref(0)
const minutes = ref(0)
const seconds = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

function updateCountdown() {
  const target = new Date(props.targetDate).getTime()
  const now = Date.now()
  const diff = Math.max(0, target - now)

  days.value = Math.floor(diff / (1000 * 60 * 60 * 24))
  hours.value = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  minutes.value = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  seconds.value = Math.floor((diff % (1000 * 60)) / 1000)
}

onMounted(() => {
  updateCountdown()
  timer = setInterval(updateCountdown, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped lang="scss">
.countdown-timer {
  text-align: center;
  padding: 16px 0;
}

.countdown-label {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
  margin-bottom: 12px;
  letter-spacing: 2px;
}

.countdown-grid {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.countdown-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.countdown-num {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(10px);
  padding: 8px 12px;
  border-radius: 10px;
  min-width: 52px;
  text-align: center;
  font-variant-numeric: tabular-nums;
}

.countdown-unit {
  font-size: 11px;
  color: rgba(255,255,255,0.6);
  margin-top: 4px;
}

.countdown-sep {
  font-size: 24px;
  color: rgba(255,255,255,0.5);
  font-weight: 700;
  padding-bottom: 16px;
}
</style>
