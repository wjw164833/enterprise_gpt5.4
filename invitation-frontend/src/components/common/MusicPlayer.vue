<template>
  <div class="music-player" v-if="src">
    <div class="player-toggle" @click="togglePlay">
      <div class="disc" :class="{ spinning: isPlaying }">
        <img v-if="cover" :src="cover" alt="" class="disc-cover" />
        <span v-else class="disc-icon">🎵</span>
      </div>
    </div>
    <audio ref="audioRef" :src="src" loop preload="none" @ended="isPlaying = false"></audio>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

defineProps<{
  src?: string
  cover?: string
}>()

const audioRef = ref<HTMLAudioElement | null>(null)
const isPlaying = ref(false)

function togglePlay() {
  if (!audioRef.value) return
  if (isPlaying.value) {
    audioRef.value.pause()
    isPlaying.value = false
  } else {
    audioRef.value.play().then(() => {
      isPlaying.value = true
    }).catch(() => {
      // Autoplay may be blocked
    })
  }
}

onUnmounted(() => {
  if (audioRef.value) {
    audioRef.value.pause()
  }
})
</script>

<style scoped lang="scss">
.player-toggle {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 100;
  cursor: pointer;
}

.disc {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 2px solid rgba(255,255,255,0.3);
  transition: transform 0.3s;

  &.spinning {
    animation: spin 3s linear infinite;
  }
}

.disc-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.disc-icon {
  font-size: 20px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
