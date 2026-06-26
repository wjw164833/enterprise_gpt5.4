<template>
  <div class="wx-scan-page">
    <div class="scan-card">
      <h2>微信扫码登录</h2>
      <div class="qr-container">
        <div v-if="loading" class="qr-loading">
          <el-icon class="is-loading" :size="32"><Loading /></el-icon>
          <p>加载中...</p>
        </div>
        <div v-else-if="qrUrl" class="qr-display">
          <img :src="qrUrl" alt="微信扫码" class="qr-image" />
          <p class="qr-tip">请使用微信扫描二维码登录</p>
        </div>
        <div v-else class="qr-error">
          <el-icon :size="48" color="#f56c6c"><CircleCloseFilled /></el-icon>
          <p>二维码加载失败</p>
          <el-button type="primary" @click="fetchQrCode">重新获取</el-button>
        </div>
      </div>
      <div class="scan-status">
        <el-steps :active="scanStep" align-center>
          <el-step title="打开微信" />
          <el-step title="扫描二维码" />
          <el-step title="确认登录" />
        </el-steps>
      </div>
      <el-button link @click="$router.push('/login')">
        <el-icon><ArrowLeft /></el-icon> 返回账号登录
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getWxScanQrCode, checkWxScanStatus } from '@/api/auth'
import { setTokens } from '@/api/request'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(true)
const qrUrl = ref('')
const scanStep = ref(0)
const state = ref('')
let pollTimer: ReturnType<typeof setInterval> | null = null

async function fetchQrCode() {
  loading.value = true
  try {
    const res = await getWxScanQrCode()
    qrUrl.value = res.data.url
    state.value = res.data.state
    scanStep.value = 0
    startPolling()
  } catch {
    qrUrl.value = ''
  } finally {
    loading.value = false
  }
}

function startPolling() {
  stopPolling()
  pollTimer = setInterval(async () => {
    try {
      const res = await checkWxScanStatus(state.value)
      if (res.data) {
        scanStep.value = 3
        setTokens(res.data.accessToken, res.data.refreshToken)
        await userStore.fetchUserInfo()
        stopPolling()
        router.push('/admin/dashboard')
      } else {
        if (scanStep.value === 0) scanStep.value = 1
      }
    } catch {
      // continue polling
    }
  }, 2000)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

onMounted(() => {
  fetchQrCode()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped lang="scss">
.wx-scan-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.scan-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 40px;
  width: 420px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);

  h2 {
    margin-bottom: 24px;
    font-size: 20px;
  }
}

.qr-container {
  margin: 20px 0;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qr-loading, .qr-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #909399;
}

.qr-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.qr-image {
  width: 200px;
  height: 200px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.qr-tip {
  font-size: 13px;
  color: #909399;
}

.scan-status {
  margin: 24px 0;
}
</style>
