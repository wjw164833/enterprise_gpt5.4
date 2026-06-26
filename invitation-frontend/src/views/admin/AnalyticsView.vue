<template>
  <div class="analytics-page page-container">
    <div class="page-header">
      <h2>数据分析</h2>
      <el-select v-model="selectedInvitation" placeholder="选择邀请函" style="width: 240px" @change="fetchData">
        <el-option v-for="inv in invitations" :key="inv.id" :label="inv.title" :value="inv.id" />
      </el-select>
    </div>

    <el-row :gutter="20" v-if="selectedInvitation">
      <el-col :xs="24" :lg="16">
        <div class="card-shadow chart-card">
          <div class="card-header">
            <h3>PV/UV趋势</h3>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="small"
              @change="fetchTrend"
            />
          </div>
          <PvUvChart :data="trendData" />
        </div>
      </el-col>
      <el-col :xs="24" :lg="8">
        <div class="card-shadow chart-card">
          <h3>回复分布</h3>
          <ReplyChart :data="replyStats" />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" v-if="selectedInvitation" class="mt-4">
      <el-col :span="24">
        <div class="card-shadow chart-card">
          <h3>访问热力图</h3>
          <HeatmapChart :invitation-id="selectedInvitation" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyInvitations } from '@/api/invitation'
import { getPvUvTrend, getReplyStats } from '@/api/analytics'
import PvUvChart from '@/components/chart/PvUvChart.vue'
import ReplyChart from '@/components/chart/ReplyChart.vue'
import HeatmapChart from '@/components/chart/HeatmapChart.vue'
import type { InvitationListItem, TrendItem, ReplyStats } from '@/types'

const invitations = ref<InvitationListItem[]>([])
const selectedInvitation = ref<number | undefined>(undefined)
const dateRange = ref<[Date, Date] | null>(null)
const trendData = ref<TrendItem[]>([])
const replyStats = ref<ReplyStats>({ attendCount: 0, uncertainCount: 0, declineCount: 0, totalCount: 0, attendRate: 0 })

async function fetchInvitations() {
  try {
    const res = await getMyInvitations({ page: 1, size: 100 })
    invitations.value = res.data.list
    if (invitations.value.length > 0) {
      selectedInvitation.value = invitations.value[0].id
      fetchData()
    }
  } catch { /* ignore */ }
}

async function fetchData() {
  fetchTrend()
  fetchReplyStats()
}

async function fetchTrend() {
  if (!selectedInvitation.value) return
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  try {
    const res = await getPvUvTrend(selectedInvitation.value, {
      startDate: start.toISOString().slice(0, 10),
      endDate: end.toISOString().slice(0, 10)
    })
    trendData.value = res.data
  } catch { /* ignore */ }
}

async function fetchReplyStats() {
  if (!selectedInvitation.value) return
  try {
    const res = await getReplyStats(selectedInvitation.value)
    replyStats.value = res.data
  } catch { /* ignore */ }
}

onMounted(() => {
  fetchInvitations()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.chart-card {
  padding: 20px;
  h3 {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 16px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
}
</style>
