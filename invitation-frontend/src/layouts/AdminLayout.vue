<template>
  <el-container class="admin-layout">
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="sidebar-container">
      <div class="logo-container">
        <div class="logo-icon logo-placeholder">邀</div>
        <span v-show="!appStore.sidebarCollapsed" class="logo-text">邀请函管理</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="currentRoute"
          :collapse="appStore.sidebarCollapsed"
          :collapse-transition="true"
          background-color="transparent"
          text-color="var(--text-color-regular)"
          active-text-color="var(--color-primary)"
          router
        >
          <template v-for="route in menuRoutes" :key="route.path">
            <el-menu-item :index="route.path">
              <el-icon><component :is="route.meta?.icon" /></el-icon>
              <template #title>{{ route.meta?.title }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header-container">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="appStore.toggleSidebar">
            <Fold v-if="!appStore.sidebarCollapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute?.meta?.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-switch
            v-model="isDarkMode"
            inline-prompt
            active-text="🌙"
            inactive-text="☀️"
            @change="appStore.toggleDarkMode"
          />
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.avatar || undefined">
                {{ userStore.nickname?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userStore.nickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="content-container">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'
import { useAuth } from '@/composables/useAuth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const { handleLogout } = useAuth()

const isDarkMode = computed({
  get: () => appStore.darkMode,
  set: () => {}
})

const currentRoute = computed(() => route.path)

const menuRoutes = computed(() => {
  const adminRoute = router.options.routes.find(r => r.path === '/admin')
  if (adminRoute?.children) {
    return adminRoute.children.filter(child => !child.meta?.hidden)
  }
  return []
})

function handleCommand(command: string) {
  switch (command) {
    case 'profile':
      router.push('/admin/settings')
      break
    case 'settings':
      router.push('/admin/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}
</script>

<style scoped lang="scss">
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.sidebar-container {
  background: var(--bg-color);
  border-right: 1px solid var(--border-color-light);
  transition: width 0.3s ease;
  overflow: hidden;

  :deep(.el-menu) {
    border-right: none;
  }

  :deep(.el-menu-item.is-active) {
    background-color: var(--color-primary);
    color: #fff !important;
    border-radius: var(--radius-sm);
    margin: 2px 8px;
  }
}

.logo-container {
  height: $header-height;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid var(--border-color-light);
  padding: 0 16px;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.logo-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: var(--color-primary);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-primary);
}

.main-container {
  overflow: hidden;
}

.header-container {
  height: $header-height;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--bg-color);
  border-bottom: 1px solid var(--border-color-light);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-color-regular);
  &:hover {
    color: var(--color-primary);
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name {
  font-size: 14px;
  color: var(--text-color-regular);
}

.content-container {
  background: var(--bg-color-page);
  overflow-y: auto;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>
