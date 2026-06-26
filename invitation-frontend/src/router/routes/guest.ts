import type { RouteRecordRaw } from 'vue-router'

export const guestRoutes: RouteRecordRaw[] = [
  {
    path: '/guest',
    component: () => import('@/layouts/GuestLayout.vue'),
    children: [
      {
        path: 'i/:shortCode',
        name: 'GuestInvitationShow',
        component: () => import('@/views/guest/InvitationShowView.vue'),
        meta: { title: '邀请函', requiresAuth: false }
      },
      {
        path: 'i/:shortCode/reply',
        name: 'GuestReply',
        component: () => import('@/views/guest/ReplyView.vue'),
        meta: { title: '回复出席', requiresAuth: false }
      },
      {
        path: 'i/:shortCode/bless',
        name: 'GuestBlessWall',
        component: () => import('@/views/guest/BlessWallView.vue'),
        meta: { title: '留言墙', requiresAuth: false }
      },
      {
        path: 'i/:shortCode/seat',
        name: 'GuestSeatMap',
        component: () => import('@/views/guest/SeatMapView.vue'),
        meta: { title: '座位图', requiresAuth: false }
      },
      {
        path: 'i/:shortCode/gift',
        name: 'GuestGiftPay',
        component: () => import('@/views/guest/GiftPayView.vue'),
        meta: { title: '送礼金', requiresAuth: false }
      },
      {
        path: 'i/:shortCode/chat',
        name: 'GuestChatRoom',
        component: () => import('@/views/guest/ChatRoomView.vue'),
        meta: { title: '聊天室', requiresAuth: false }
      }
    ]
  }
]
