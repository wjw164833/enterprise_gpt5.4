<template>
  <div class="guest-music-player">
    <div class="player-btn" :class="{ playing: isPlaying }" @click="togglePlay">
      <div class="music-disc">
        <img v-if="cover" :src="cover" alt="" class="disc-img" />
        <span v-else>🎵</span>
      </div>
    </div>
    <audio ref="audioRef" :src="src" loop preload="none"></audio>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'

defineProps<{
  src?: string
  cover?: string
}>()

const audioRef = ref<HTMLAudioElement | null>(null)
const isPlaying = ref(false)

function togglePlay() {
  if (!audioRef.value || !audioRef.value.src) return
  if (isPlaying.value) {
    audioRef.value.pause()
    isPlaying.value = false
  } else {
    audioRef.value.play().then(() => {
      isPlaying.value = true
    }).catch(() => {
      // Autoplay may be blocked by browser
    })
  }
}

onUnmounted(() => {
  if (audioRef.value) {
    audioRef.value.pause()
    isPlaying.value = false
  }
})
</script>

<style scoped lang="scss">
.player-btn {
  position: fixed;
  bottom: 80px;
  right: 16px;
  z-index: 100;
  cursor: pointer;
}

.music-disc {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255,255,255,0.3);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: transform 0.3s;
}

.playing .music-disc {
  animation: spin 3s linear infinite;
}

.disc-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
