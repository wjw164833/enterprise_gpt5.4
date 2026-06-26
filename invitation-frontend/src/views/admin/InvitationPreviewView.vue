<template>
  <div class="invitation-preview-page page-container">
    <div class="page-header">
      <el-button link @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <h2>预览邀请函</h2>
      <div class="header-actions">
        <el-radio-group v-model="previewMode" size="small">
          <el-radio-button label="mobile">手机端</el-radio-button>
          <el-radio-button label="desktop">桌面端</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div class="preview-container" v-loading="loading">
      <div class="phone-wrapper" :class="{ 'desktop-mode': previewMode === 'desktop' }">
        <div class="phone-device">
          <div class="phone-notch"></div>
          <div class="phone-screen">
            <div class="preview-header" v-if="detail">
              <img v-if="detail.coverImage" :src="detail.coverImage" class="preview-cover" />
              <div class="preview-overlay">
                <h1 class="preview-title">{{ detail.title }}</h1>
                <p class="preview-meta" v-if="detail.activityDate">
                  <el-icon><Calendar /></el-icon> {{ formatDate(detail.activityDate) }}
                </p>
                <p class="preview-meta" v-if="detail.activityAddress">
                  <el-icon><Location /></el-icon> {{ detail.activityAddress }}
                </p>
              </div>
            </div>
            <div class="preview-body" v-if="detail">
              <div v-if="detail.aiGreeting" class="ai-greeting">
                <el-icon><MagicStick /></el-icon>
                <p>{{ detail.aiGreeting }}</p>
              </div>
              <div class="rich-content" v-html="detail.content || '<p style=color:#999>暂无内容</p>'"></div>
            </div>
          </div>
          <div class="phone-home-bar"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getInvitationDetail } from '@/api/invitation'
import { formatDate } from '@/utils/format'
import type { InvitationDetail } from '@/types'

const route = useRoute()
const invitationId = Number(route.params.id)
const loading = ref(false)
const previewMode = ref('mobile')
const detail = ref<InvitationDetail | null>(null)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getInvitationDetail(invitationId)
    detail.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  h2 { margin: 0; flex: 1; }
}

.preview-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.phone-wrapper {
  &.desktop-mode .phone-device {
    width: 768px;
    height: auto;
    border-radius: 12px;
    border: 2px solid #1a1a1a;
  }
}

.phone-device {
  width: 375px;
  height: 812px;
  border: 8px solid #1a1a1a;
  border-radius: 44px;
  overflow: hidden;
  position: relative;
  background: #fff;
}

.phone-notch {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 150px;
  height: 30px;
  background: #1a1a1a;
  border-radius: 0 0 20px 20px;
  z-index: 10;
}

.phone-screen {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.preview-header {
  position: relative;
  height: 300px;
}

.preview-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: #fff;
}

.preview-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;
  font-family: 'Noto Serif SC', serif;
}

.preview-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  opacity: 0.9;
  margin-bottom: 4px;
}

.preview-body {
  padding: 20px;
}

.ai-greeting {
  background: linear-gradient(135deg, #f5f7fa, #e8ecf1);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
  align-items: flex-start;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.rich-content {
  line-height: 1.8;
  font-size: 14px;
  color: #333;
}

.phone-home-bar {
  position: absolute;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  width: 134px;
  height: 5px;
  background: #1a1a1a;
  border-radius: 3px;
}
</style>
