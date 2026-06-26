<template>
  <div class="invitation-list-page page-container">
    <div class="page-header">
      <h2>邀请函管理</h2>
      <el-button type="primary" @click="$router.push('/admin/invitations/create')">
        <el-icon><Plus /></el-icon> 创建邀请函
      </el-button>
    </div>

    <div class="card-shadow filter-bar">
      <el-form :inline="true" :model="queryParams" @submit.prevent="handleSearch">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="搜索标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="queryParams.activityType" placeholder="全部类型" clearable style="width: 120px">
            <el-option v-for="t in activityTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option v-for="s in statusList" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card-shadow">
      <el-table :data="invitationStore.list" v-loading="invitationStore.loading" stripe>
        <el-table-column prop="coverImage" label="封面" width="100">
          <template #default="{ row }">
            <el-image :src="row.coverImage" style="width: 60px; height: 40px" fit="cover" :preview-src-list="[row.coverImage]" preview-teleported />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="activityType" label="类型" width="100">
          <template #default="{ row }">{{ formatActivityType(row.activityType) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ formatInvitationStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pv" label="PV" width="80" />
        <el-table-column prop="replyCount" label="回复" width="80" />
        <el-table-column prop="activityDate" label="活动日期" width="160">
          <template #default="{ row }">{{ formatDate(row.activityDate, 'YYYY-MM-DD HH:mm') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/admin/invitations/${row.id}/edit`)">编辑</el-button>
            <el-button link type="primary" @click="$router.push(`/admin/invitations/${row.id}/preview`)">预览</el-button>
            <el-button v-if="row.status === 0" link type="success" @click="handlePublish(row.id)">发布</el-button>
            <el-button v-if="row.status === 1" link type="warning" @click="handleUnpublish(row.id)">下架</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="invitationStore.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { useInvitationStore } from '@/store/invitation'
import { publishInvitation, unpublishInvitation, deleteInvitation } from '@/api/invitation'
import { formatActivityType, formatInvitationStatus, formatDate } from '@/utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ActivityType, InvitationStatus } from '@/types'

const invitationStore = useInvitationStore()

const activityTypes = [
  { label: '婚礼', value: 1 },
  { label: '生日', value: 2 },
  { label: '商务', value: 3 },
  { label: '升学', value: 4 },
  { label: '其他', value: 5 }
]

const statusList = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 },
  { label: '已下架', value: 2 }
]

const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  activityType: undefined as ActivityType | undefined,
  status: undefined as InvitationStatus | undefined
})

function fetchData() {
  invitationStore.fetchList(queryParams)
}

function handleSearch() {
  queryParams.page = 1
  fetchData()
}

function resetSearch() {
  queryParams.keyword = ''
  queryParams.activityType = undefined
  queryParams.status = undefined
  queryParams.page = 1
  fetchData()
}

function statusTagType(status: number): string {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || 'info'
}

async function handlePublish(id: number) {
  try {
    await ElMessageBox.confirm('确定发布此邀请函？', '确认')
    await publishInvitation(id)
    ElMessage.success('发布成功')
    fetchData()
  } catch { /* cancelled */ }
}

async function handleUnpublish(id: number) {
  try {
    await ElMessageBox.confirm('确定下架此邀请函？', '确认')
    await unpublishInvitation(id)
    ElMessage.success('已下架')
    fetchData()
  } catch { /* cancelled */ }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除此邀请函？删除后不可恢复', '警告', { type: 'warning' })
    await deleteInvitation(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch { /* cancelled */ }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 { margin: 0; font-size: 20px; }
}

.filter-bar {
  padding: 16px 20px;
  margin-bottom: 16px;
}

.pagination-container {
  padding: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
