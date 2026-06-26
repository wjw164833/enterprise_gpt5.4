import { computed } from 'vue'
import { useUserStore } from '@/store/user'

export function usePermission() {
  const userStore = useUserStore()

  const isAdmin = computed(() => userStore.isAdmin)
  const isEnterprise = computed(() => userStore.isEnterprise)
  const isLoggedIn = computed(() => userStore.isLoggedIn)

  function hasPermission(requiredType: number): boolean {
    if (!userStore.userInfo) return false
    return userStore.userInfo.userType >= requiredType
  }

  function canCreateInvitation(): boolean {
    return userStore.isLoggedIn
  }

  function canUseAi(): boolean {
    return userStore.isLoggedIn
  }

  function canAccessAdmin(): boolean {
    return isAdmin.value
  }

  return {
    isAdmin,
    isEnterprise,
    isLoggedIn,
    hasPermission,
    canCreateInvitation,
    canUseAi,
    canAccessAdmin
  }
}
