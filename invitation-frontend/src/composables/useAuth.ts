import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { smsLogin, sendSmsCode, wxMiniLogin, logout as logoutApi } from '@/api/auth'
import { setTokens } from '@/api/request'
import { ElMessage } from 'element-plus'

export function useAuth() {
  const router = useRouter()
  const userStore = useUserStore()
  const loading = ref(false)
  const countdown = ref(0)
  let countdownTimer: ReturnType<typeof setInterval> | null = null

  async function handleSmsLogin(phone: string, code: string) {
    loading.value = true
    try {
      const res = await smsLogin({ phone, code })
      setTokens(res.data.accessToken, res.data.refreshToken)
      await userStore.fetchUserInfo()
      ElMessage.success('登录成功')
      const redirect = (router.currentRoute.value.query.redirect as string) || '/admin/dashboard'
      router.push(redirect)
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  }

  async function handleSendSms(phone: string) {
    if (countdown.value > 0) return
    try {
      await sendSmsCode({ phone })
      ElMessage.success('验证码已发送')
      countdown.value = 60
      countdownTimer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0 && countdownTimer) {
          clearInterval(countdownTimer)
          countdownTimer = null
        }
      }, 1000)
    } catch (error: any) {
      ElMessage.error(error.message || '发送失败')
    }
  }

  async function handleWxLogin(code: string) {
    loading.value = true
    try {
      const res = await wxMiniLogin({ code })
      setTokens(res.data.accessToken, res.data.refreshToken)
      await userStore.fetchUserInfo()
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    } catch (error: any) {
      ElMessage.error(error.message || '微信登录失败')
    } finally {
      loading.value = false
    }
  }

  async function handleLogout() {
    try {
      await logoutApi()
    } catch {
      // ignore
    }
    userStore.logout()
    router.push('/login')
  }

  return {
    loading,
    countdown,
    handleSmsLogin,
    handleSendSms,
    handleWxLogin,
    handleLogout
  }
}
