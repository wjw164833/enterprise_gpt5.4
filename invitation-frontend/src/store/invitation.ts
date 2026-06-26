import { defineStore } from 'pinia'
import type { InvitationDetail, InvitationListItem } from '@/types'
import { getMyInvitations, getInvitationDetail } from '@/api/invitation'

interface InvitationState {
  list: InvitationListItem[]
  total: number
  currentInvitation: InvitationDetail | null
  loading: boolean
}

export const useInvitationStore = defineStore('invitation', {
  state: (): InvitationState => ({
    list: [],
    total: 0,
    currentInvitation: null,
    loading: false
  }),

  actions: {
    async fetchList(params: { page: number; size: number; keyword?: string; activityType?: number; status?: number }) {
      this.loading = true
      try {
        const res = await getMyInvitations(params)
        this.list = res.data.list
        this.total = res.data.total
      } finally {
        this.loading = false
      }
    },

    async fetchDetail(id: number) {
      this.loading = true
      try {
        const res = await getInvitationDetail(id)
        this.currentInvitation = res.data
      } finally {
        this.loading = false
      }
    },

    clearCurrent() {
      this.currentInvitation = null
    }
  }
})
