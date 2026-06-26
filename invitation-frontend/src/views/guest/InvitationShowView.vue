<template>
  <div class="invitation-show" ref="containerRef">
    <!-- Background Music Player -->
    <GuestMusicPlayer v-if="invitation" :src="invitation.customMusicUrl" :cover="invitation.coverImage" />

    <!-- Full-screen Hero -->
    <section class="hero-section" :style="{ backgroundImage: `url(${invitation?.coverImage})` }">
      <div class="hero-overlay"></div>
      <div class="hero-content animate-fade-in">
        <h1 class="hero-title font-display">{{ invitation?.title }}</h1>
        <p class="hero-type">{{ formatActivityType(invitation?.activityType || 1) }}</p>
        <CountdownTimer v-if="invitation?.activityDate" :target-date="invitation.activityDate" />
      </div>
      <div class="scroll-hint animate-float" @click="scrollToContent">
        <el-icon :size="24" color="#fff"><ArrowDown /></el-icon>
      </div>
    </section>

    <!-- Content Section -->
    <section class="content-section" ref="contentRef">
      <!-- AI Greeting -->
      <div v-if="invitation?.aiGreeting" class="greeting-card animate-slide-up">
        <div class="greeting-icon">💌</div>
        <p class="greeting-text">{{ invitation.aiGreeting }}</p>
      </div>

      <!-- Event Info -->
      <div class="info-card glass-effect animate-slide-up">
        <div class="info-item">
          <div class="info-icon">📅</div>
          <div class="info-detail">
            <div class="info-label">活动时间</div>
            <div class="info-value">{{ formatDate(invitation?.activityDate, 'YYYY年MM月DD日 HH:mm') }}</div>
          </div>
        </div>
        <div class="info-item" v-if="invitation?.activityAddress">
          <div class="info-icon">📍</div>
          <div class="info-detail">
            <div class="info-label">活动地点</div>
            <div class="info-value">{{ invitation.activityAddress }}</div>
          </div>
        </div>
        <MapNavigation
          v-if="invitation?.latitude && invitation?.longitude"
          :latitude="invitation.latitude"
          :longitude="invitation.longitude"
          :address="invitation.activityAddress"
        />
      </div>

      <!-- Rich Content -->
      <div class="rich-content animate-slide-up" v-if="invitation?.content" v-html="invitation.content"></div>

      <!-- Photo Gallery -->
      <PhotoGallery v-if="invitation" :invitation-id="invitation.id" />

      <!-- Action Buttons -->
      <div class="action-grid animate-slide-up">
        <div class="action-btn" @click="$router.push(`/guest/i/${shortCode}/reply`)">
          <div class="action-icon">✅</div>
          <span>回复出席</span>
        </div>
        <div class="action-btn" @click="$router.push(`/guest/i/${shortCode}/bless`)">
          <div class="action-icon">💝</div>
          <span>留言祝福</span>
        </div>
        <div class="action-btn" @click="$router.push(`/guest/i/${shortCode}/seat`)">
          <div class="action-icon">💺</div>
          <span>查看座位</span>
        </div>
        <div class="action-btn" @click="$router.push(`/guest/i/${shortCode}/gift`)">
          <div class="action-icon">🧧</div>
          <span>送礼金</span>
        </div>
      </div>

      <!-- Stats -->
      <div class="stats-bar animate-slide-up">
        <div class="stat-item">
          <span class="stat-num">{{ invitation?.pv || 0 }}</span>
          <span class="stat-label">浏览</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ invitation?.replyCount || 0 }}</span>
          <span class="stat-label">回复</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ invitation?.blessCount || 0 }}</span>
          <span class="stat-label">祝福</span>
        </div>
      </div>
    </section>

    <!-- Loading State -->
    <div v-if="loading" class="loading-full">
      <div class="loading-spinner"></div>
      <p>加载邀请函中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPublicInvitation } from '@/api/invitation'
import { formatActivityType, formatDate } from '@/utils/format'
import { useAnalytics } from '@/composables/useAnalytics'
import CountdownTimer from '@/components/guest/CountdownTimer.vue'
import GuestMusicPlayer from '@/components/guest/MusicPlayer.vue'
import MapNavigation from '@/components/guest/MapNavigation.vue'
import PhotoGallery from '@/components/guest/PhotoGallery.vue'
import type { InvitationDetail } from '@/types'

const route = useRoute()
const shortCode = route.params.shortCode as string
const loading = ref(true)
const invitation = ref<InvitationDetail | null>(null)
const containerRef = ref<HTMLElement | null>(null)
const contentRef = ref<HTMLElement | null>(null)
const { startAutoTracking, trackShare } = useAnalytics()

async function fetchInvitation() {
  loading.value = true
  try {
    const res = await getPublicInvitation(shortCode)
    invitation.value = res.data
    startAutoTracking(res.data.id)
  } catch {
    // Will show error in UI
  } finally {
    loading.value = false
  }
}

function scrollToContent() {
  contentRef.value?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  fetchInvitation()
})
</script>

<style scoped lang="scss">
.invitation-show {
  min-height: 100vh;
  background: linear-gradient(180deg, #0a0a0a 0%, #1a1a2e 50%, #16213e 100%);
  color: #fff;
  overflow-x: hidden;
}

.hero-section {
  position: relative;
  height: 100vh;
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0,0,0,0.3) 0%, rgba(0,0,0,0.6) 100%);
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 0 24px;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
  line-height: 1.3;
}

.hero-type {
  font-size: 16px;
  opacity: 0.8;
  margin-bottom: 32px;
  letter-spacing: 4px;
}

.scroll-hint {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1;
  cursor: pointer;
}

.content-section {
  padding: 24px 16px 60px;
  max-width: 500px;
  margin: 0 auto;
}

.greeting-card {
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  margin-bottom: 24px;
}

.greeting-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.greeting-text {
  font-size: 15px;
  line-height: 1.8;
  color: rgba(255,255,255,0.9);
  font-style: italic;
}

.info-card {
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.info-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.info-label {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
  margin-bottom: 4px;
}

.info-value {
  font-size: 15px;
  color: #fff;
}

.rich-content {
  margin-bottom: 24px;
  line-height: 1.8;
  font-size: 15px;
  color: rgba(255,255,255,0.85);

  :deep(img) {
    max-width: 100%;
    border-radius: 8px;
  }
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 8px;
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;

  &:hover {
    background: rgba(255,255,255,0.15);
    transform: translateY(-2px);
  }
}

.action-icon {
  font-size: 28px;
}

.stats-bar {
  display: flex;
  justify-content: space-around;
  padding: 20px;
  background: rgba(255,255,255,0.05);
  border-radius: 12px;
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.loading-full {
  position: fixed;
  inset: 0;
  background: #0a0a0a;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  color: #fff;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255,255,255,0.1);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
