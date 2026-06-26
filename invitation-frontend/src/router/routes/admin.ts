import type { RouteRecordRaw } from 'vue-router'

export const adminRoutes: RouteRecordRaw[] = [
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/DashboardView.vue'),
        meta: { title: '数据看板', icon: 'DataAnalysis' }
      },
      {
        path: 'invitations',
        name: 'InvitationList',
        component: () => import('@/views/admin/InvitationListView.vue'),
        meta: { title: '邀请函管理', icon: 'Document' }
      },
      {
        path: 'invitations/create',
        name: 'InvitationCreate',
        component: () => import('@/views/admin/InvitationCreateView.vue'),
        meta: { title: '创建邀请函', icon: 'Plus', hidden: true }
      },
      {
        path: 'invitations/:id/edit',
        name: 'InvitationEdit',
        component: () => import('@/views/admin/InvitationEditView.vue'),
        meta: { title: '编辑邀请函', hidden: true }
      },
      {
        path: 'invitations/:id/preview',
        name: 'InvitationPreview',
        component: () => import('@/views/admin/InvitationPreviewView.vue'),
        meta: { title: '预览邀请函', hidden: true }
      },
      {
        path: 'guests',
        name: 'GuestList',
        component: () => import('@/views/admin/GuestListView.vue'),
        meta: { title: '宾客管理', icon: 'User' }
      },
      {
        path: 'seats',
        name: 'SeatManage',
        component: () => import('@/views/admin/SeatManageView.vue'),
        meta: { title: '席位管理', icon: 'Grid' }
      },
      {
        path: 'templates',
        name: 'TemplateList',
        component: () => import('@/views/admin/TemplateListView.vue'),
        meta: { title: '模板中心', icon: 'Picture' }
      },
      {
        path: 'analytics',
        name: 'Analytics',
        component: () => import('@/views/admin/AnalyticsView.vue'),
        meta: { title: '数据分析', icon: 'TrendCharts' }
      },
      {
        path: 'chat',
        name: 'ChatRoom',
        component: () => import('@/views/admin/ChatRoomView.vue'),
        meta: { title: '聊天室', icon: 'ChatDotRound' }
      },
      {
        path: 'subscription',
        name: 'Subscription',
        component: () => import('@/views/admin/SubscriptionView.vue'),
        meta: { title: '订阅管理', icon: 'ShoppingCart' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/admin/SettingsView.vue'),
        meta: { title: '系统设置', icon: 'Setting' }
      }
    ]
  }
]
