<template>
  <div class="guest-list-page page-container">
    <div class="page-header">
      <h2>宾客管理</h2>
      <el-button type="primary" @click="handleExport" :loading="exporting">
        <el-icon><Download /></el-icon> 导出Excel
      </el-button>
    </div>

    <div class="card-shadow filter-bar">
      <el-form :inline="true">
        <el-form-item label="邀请函">
          <el-select v-model="selectedInvitation" placeholder="选择邀请函" @change="fetchData" style="width: 240px">
            <el-option v-for="inv in invitations" :key="inv.id" :label="inv.title" :value="inv.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复状态">
          <el-select v-model="replyFilter" placeholder="全部" clearable style="width: 120px" @change="fetchData">
            <el-option label="出席" :value="1" />
            <el-option label="不确定" :value="2" />
            <el-option label="不出席" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <div class="stats-row" v-if="selectedInvitation">
      <div class="mini-stat">
        <span class="mini-stat-value attend">{{ stats.attend }}</span>
        <span class="mini-stat-label">出席</span>
      </div>
      <div class="mini-stat">
        <span class="mini-stat-value uncertain">{{ stats.uncertain }}</span>
        <span class="mini-stat-label">不确定</span>
      </div>
      <div class="mini-stat">
        <span class="mini-stat-value decline">{{ stats.decline }}</span>
        <span class="mini-stat-label">不出席</span>
      </div>
      <div class="mini-stat">
        <span class="mini-stat-value">{{ stats.total }}</span>
        <span class="mini-stat-label">合计</span>
      </div>
    </div>

    <div class="card-shadow">
      <el-table :data="guests" v-loading="loading" stripe>
        <el-table-column prop="guestName" label="姓名" width="120" />
        <el-table-column prop="guestPhone" label="手机号" width="140">
          <template #default="{ row }">{{ formatPhone(row.guestPhone) }}</template>
        </el-table-column>
        <el-table-column prop="replyStatus" label="回复状态" width="120">
          <template #default="{ row }">
            <el-tag :type="replyTagType(row.replyStatus)" size="small">{{ formatReplyStatus(row.replyStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="guestCount" label="出席人数" width="100" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="回复时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getMyInvitations } from '@/api/invitation'
import { getReplies, exportGuests } from '@/api/guest'
import { formatDate, formatPhone, formatReplyStatus } from '@/utils/format'
import type { InvitationListItem, GuestReplyVO } from '@/types'

const loading = ref(false)
const exporting = ref(false)
const invitations = ref<InvitationListItem[]>([])
const guests = ref<GuestReplyVO[]>([])
const selectedInvitation = ref<number | undefined>(undefined)
const replyFilter = ref<number | undefined>(undefined)
const page = ref(1)
const size = ref(20)
const total = ref(0)

const stats = reactive({ attend: 0, uncertain: 0, decline: 0, total: 0 })

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
  if (!selectedInvitation.value) return
  loading.value = true
  try {
    const res = await getReplies(selectedInvitation.value, { page: page.value, size: size.value })
    guests.value = res.data.list
    total.value = res.data.total
    computeStats()
  } finally {
    loading.value = false
  }
}

function computeStats() {
  stats.attend = guests.value.filter(g => g.replyStatus === 1).length
  stats.uncertain = guests.value.filter(g => g.replyStatus === 2).length
  stats.decline = guests.value.filter(g => g.replyStatus === 3).length
  stats.total = guests.value.length
}

async function handleExport() {
  if (!selectedInvitation.value) return
  exporting.value = true
  try {
    const blob = await exportGuests(selectedInvitation.value)
    const url = window.URL.createObjectURL(new Blob([blob as any]))
    const link = document.createElement('a')
    link.href = url
    link.download = `guests-${selectedInvitation.value}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
  } catch {
    // ignore
  } finally {
    exporting.value = false
  }
}

function replyTagType(status: number): string {
  const map: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  fetchInvitations()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.filter-bar {
  padding: 16px 20px;
  margin-bottom: 16px;
}

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.mini-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 24px;
  background: #fff;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.mini-stat-value {
  font-size: 24px;
  font-weight: 700;
  &.attend { color: #67c23a; }
  &.uncertain { color: #e6a23c; }
  &.decline { color: #f56c6c; }
}

.mini-stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.pagination-container {
  padding: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
