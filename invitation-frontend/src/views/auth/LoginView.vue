<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand-section">
          <h1 class="brand-title animate-fade-in">邀请函管理系统</h1>
          <p class="brand-desc animate-slide-up">创建精美邀请函，让每一次邀请都成为美好回忆</p>
          <div class="feature-list">
            <div v-for="(feat, idx) in features" :key="idx" class="feature-item animate-slide-up" :style="{ animationDelay: `${idx * 0.1}s` }">
              <el-icon :size="20"><component :is="feat.icon" /></el-icon>
              <span>{{ feat.text }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-card">
          <h2 class="login-title">欢迎登录</h2>
          <el-tabs v-model="loginType" class="login-tabs">
            <el-tab-pane label="手机号登录" name="sms">
              <el-form ref="smsFormRef" :model="smsForm" :rules="smsRules" label-position="top">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="smsForm.phone" placeholder="请输入手机号" maxlength="11" prefix-icon="Phone" />
                </el-form-item>
                <el-form-item label="验证码" prop="code">
                  <div class="code-input">
                    <el-input v-model="smsForm.code" placeholder="请输入验证码" maxlength="6" prefix-icon="Message" />
                    <el-button :disabled="countdown > 0" @click="handleSendSms(smsForm.phone)" :loading="sending">
                      {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
                    </el-button>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
                    登录
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="微信登录" name="wechat">
              <div class="wx-login-section">
                <div class="wx-qrcode-placeholder">
                  <el-icon :size="48" color="#07c160"><ChatDotRound /></el-icon>
                  <p>微信扫码登录</p>
                </div>
                <el-button class="wx-login-btn" @click="$router.push('/wx-scan')">
                  <el-icon><ChatDotRound /></el-icon>
                  打开微信扫码
                </el-button>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { sendSmsCode } from '@/api/auth'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const { loading, handleSmsLogin } = useAuth()
const loginType = ref('sms')
const smsFormRef = ref<FormInstance>()
const countdown = ref(0)
const sending = ref(false)
let timer: ReturnType<typeof setInterval> | null = null

const smsForm = reactive({
  phone: '',
  code: ''
})

const smsRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{4,6}$/, message: '请输入4-6位数字验证码', trigger: 'blur' }
  ]
}

const features = [
  { icon: 'MagicStick', text: 'AI智能生成邀请语' },
  { icon: 'PictureFilled', text: '精美模板一键使用' },
  { icon: 'DataAnalysis', text: '实时数据分析' },
  { icon: 'ChatDotRound', text: '在线互动聊天' }
]

async function handleSendSms(phone: string) {
  if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  sending.value = true
  try {
    await sendSmsCode({ phone })
    ElMessage.success('验证码已发送')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0 && timer) {
        clearInterval(timer)
        timer = null
      }
    }, 1000)
  } catch (error: any) {
    ElMessage.error(error.message || '发送失败')
  } finally {
    sending.value = false
  }
}

async function handleLogin() {
  const valid = await smsFormRef.value?.validate().catch(() => false)
  if (!valid) return
  await handleSmsLogin(smsForm.phone, smsForm.code)
}
</script>

<style scoped lang="scss">
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  display: flex;
  width: 900px;
  max-width: 95vw;
  min-height: 520px;
  background: #fff;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 40px;
  display: flex;
  align-items: center;
  color: #fff;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
}

.brand-desc {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.login-right {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
  max-width: 360px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: var(--text-color-primary);
}

.login-tabs {
  :deep(.el-tabs__nav) {
    width: 100%;
  }
  :deep(.el-tabs__item) {
    width: 50%;
    text-align: center;
  }
}

.code-input {
  display: flex;
  gap: 8px;
  width: 100%;
  .el-input {
    flex: 1;
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.wx-login-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  gap: 20px;
}

.wx-qrcode-placeholder {
  width: 180px;
  height: 180px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
}

.wx-login-btn {
  width: 100%;
  height: 44px;
  background: #07c160;
  color: #fff;
  border: none;
  &:hover {
    background: #06ad56;
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    min-height: auto;
  }
  .login-left {
    padding: 30px 20px;
  }
  .login-right {
    padding: 30px 20px;
  }
}
</style>
