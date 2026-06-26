<template>
  <div class="dashboard-page page-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in statCards" :key="stat.label">
        <div class="stat-card card-shadow">
          <div class="stat-icon" :style="{ background: stat.bgColor }">
            <el-icon :size="24" :color="stat.color"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-trend" :class="stat.trendUp ? 'up' : 'down'">
            <el-icon><Top v-if="stat.trendUp" /><Bottom v-else /></el-icon>
            {{ stat.trend }}
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <div class="card-shadow chart-card">
          <div class="card-header">
            <h3>PV/UV 趋势</h3>
            <el-radio-group v-model="chartRange" size="small">
              <el-radio-button label="7天" />
              <el-radio-button label="30天" />
              <el-radio-button label="90天" />
            </el-radio-group>
          </div>
          <v-chart :option="pvUvOption" autoresize style="height: 350px" />
        </div>
      </el-col>
      <el-col :xs="24" :lg="8">
        <div class="card-shadow chart-card">
          <div class="card-header">
            <h3>回复分布</h3>
          </div>
          <v-chart :option="replyOption" autoresize style="height: 350px" />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <div class="card-shadow">
          <div class="card-header">
            <h3>最近邀请函</h3>
            <el-button type="primary" link @click="$router.push('/admin/invitations')">查看全部</el-button>
          </div>
          <el-table :data="recentInvitations" stripe>
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="activityType" label="类型" width="100">
              <template #default="{ row }">{{ formatActivityType(row.activityType) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">{{ formatInvitationStatus(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="pv" label="浏览量" width="100" />
            <el-table-column prop="replyCount" label="回复数" width="100" />
            <el-table-column prop="createdAt" label="创建时间" width="180">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { getAdminOverview } from '@/api/analytics'
import { formatActivityType, formatInvitationStatus, formatDate, formatNumber } from '@/utils/format'
import type { InvitationListItem } from '@/types'

use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

const chartRange = ref('7天')
const recentInvitations = ref<InvitationListItem[]>([])

const statCards = ref([
  { label: '邀请函总数', value: '0', icon: 'Document', color: '#409eff', bgColor: '#ecf5ff', trend: '+12%', trendUp: true },
  { label: '总浏览量', value: '0', icon: 'View', color: '#67c23a', bgColor: '#f0f9eb', trend: '+8%', trendUp: true },
  { label: '总回复数', value: '0', icon: 'ChatDotSquare', color: '#e6a23c', bgColor: '#fdf6ec', trend: '+5%', trendUp: true },
  { label: '礼金总额', value: '¥0', icon: 'Money', color: '#f56c6c', bgColor: '#fef0f0', trend: '+15%', trendUp: true }
])

const pvUvOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['PV', 'UV'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: 'PV',
      type: 'line',
      smooth: true,
      data: [120, 200, 150, 300, 280, 350, 410],
      areaStyle: { opacity: 0.15 },
      itemStyle: { color: '#409eff' }
    },
    {
      name: 'UV',
      type: 'line',
      smooth: true,
      data: [80, 130, 100, 180, 170, 220, 260],
      areaStyle: { opacity: 0.15 },
      itemStyle: { color: '#67c23a' }
    }
  ]
}))

const replyOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: '0', left: 'center' },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: [
        { value: 60, name: '出席', itemStyle: { color: '#67c23a' } },
        { value: 20, name: '不确定', itemStyle: { color: '#e6a23c' } },
        { value: 15, name: '不出席', itemStyle: { color: '#f56c6c' } }
      ]
    }
  ]
}))

function statusTagType(status: number): string {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || 'info'
}

async function fetchDashboard() {
  try {
    const res = await getAdminOverview()
    const data = res.data
    statCards.value[0].value = formatNumber(data.totalInvitations)
    statCards.value[1].value = formatNumber(data.totalPv)
    statCards.value[2].value = formatNumber(data.totalUsers || 0)
    statCards.value[3].value = `¥${formatNumber(data.totalRevenue || 0)}`
  } catch {
    // Use default data
  }
}

onMounted(() => {
  fetchDashboard()
})
</script>

<style scoped lang="scss">
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  gap: 16px;
  margin-bottom: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-color-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-color-secondary);
  margin-top: 4px;
}

.stat-trend {
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 2px;
  &.up { color: #67c23a; }
  &.down { color: #f56c6c; }
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  h3 {
    font-size: 16px;
    font-weight: 600;
    margin: 0;
  }
}
</style>
