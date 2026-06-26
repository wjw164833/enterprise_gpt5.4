import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { adminRoutes } from './routes/admin'
import { guestRoutes } from './routes/guest'
import { useUserStore } from '@/store/user'
import { getAccessToken } from '@/api/request'

NProgress.configure({ showSpinner: false })

const routes = [
  ...adminRoutes,
  ...guestRoutes,
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/wx-scan',
    name: 'WxScan',
    component: () => import('@/views/auth/WxScanView.vue'),
    meta: { title: '微信扫码登录', requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

const whiteList = ['/login', '/wx-scan']

router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  document.title = `${to.meta.title || '邀请函'} - 邀请函管理系统`

  const token = getAccessToken()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth !== false && !record.path.startsWith('/guest'))

  if (whiteList.includes(to.path) || to.path.startsWith('/guest')) {
    if (token && to.path === '/login') {
      next('/admin/dashboard')
    } else {
      next()
    }
  } else if (token) {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      try {
        await userStore.fetchUserInfo()
        next()
      } catch {
        userStore.logout()
        next({ path: '/login', query: { redirect: to.fullPath } })
      }
    } else {
      next()
    }
  } else {
    next({ path: '/login', query: { redirect: to.fullPath } })
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
