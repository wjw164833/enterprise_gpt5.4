import { defineStore } from 'pinia'
import type { UserInfo } from '@/types'
import { getUserInfo } from '@/api/user'
import { setTokens, clearTokens } from '@/api/request'

interface UserState {
  userInfo: UserInfo | null
  isLoggedIn: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: null,
    isLoggedIn: false
  }),

  getters: {
    nickname: (state) => state.userInfo?.nickname || '',
    avatar: (state) => state.userInfo?.avatar || '',
    userType: (state) => state.userInfo?.userType || 1,
    isAdmin: (state) => state.userInfo?.userType === 3,
    isEnterprise: (state) => state.userInfo?.userType === 2
  },

  actions: {
    async fetchUserInfo() {
      try {
        const res = await getUserInfo()
        this.userInfo = res.data
        this.isLoggedIn = true
      } catch (error) {
        this.logout()
        throw error
      }
    },

    setLoginInfo(data: { accessToken: string; refreshToken: string; userInfo?: UserInfo }) {
      setTokens(data.accessToken, data.refreshToken)
      if (data.userInfo) {
        this.userInfo = data.userInfo
      }
      this.isLoggedIn = true
    },

    logout() {
      this.userInfo = null
      this.isLoggedIn = false
      clearTokens()
    }
  }
})
