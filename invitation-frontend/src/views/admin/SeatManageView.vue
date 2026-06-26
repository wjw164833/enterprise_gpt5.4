<template>
  <div class="seat-manage-page page-container">
    <div class="page-header">
      <h2>席位管理</h2>
      <div class="header-actions">
        <el-select v-model="selectedInvitation" placeholder="选择邀请函" style="width: 240px" @change="fetchData">
          <el-option v-for="inv in invitations" :key="inv.id" :label="inv.title" :value="inv.id" />
        </el-select>
        <el-button type="primary" @click="showAddTable = true" :disabled="!selectedInvitation">
          <el-icon><Plus /></el-icon> 添加餐桌
        </el-button>
      </div>
    </div>

    <el-row :gutter="20" v-if="selectedInvitation">
      <el-col :span="16">
        <div class="card-shadow seat-canvas-wrapper">
          <SeatDragCanvas
            :tables="tables"
            :invitation-id="selectedInvitation"
            @assign="handleAssign"
            @unassign="handleUnassign"
          />
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card-shadow table-list">
          <h4>餐桌列表</h4>
          <div v-for="table in tables" :key="table.id" class="table-item">
            <SeatCard :table="table" @delete="handleDeleteTable" />
          </div>
          <el-empty v-if="tables.length === 0" description="暂无餐桌" />
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="showAddTable" title="添加餐桌" width="400px">
      <el-form :model="newTable" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="newTable.name" placeholder="如：主桌、亲友桌" />
        </el-form-item>
        <el-form-item label="座位数">
          <el-input-number v-model="newTable.capacity" :min="2" :max="30" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddTable = false">取消</el-button>
        <el-button type="primary" @click="handleAddTable">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getMyInvitations } from '@/api/invitation'
import { getSeatTables, createSeatTable, deleteSeatTable as deleteSeatTableApi, assignSeat, unassignSeat } from '@/api/seat'
import { ElMessage, ElMessageBox } from 'element-plus'
import SeatDragCanvas from '@/components/seat/SeatDragCanvas.vue'
import SeatCard from '@/components/seat/SeatCard.vue'
import type { InvitationListItem, SeatTable, SeatAssignmentDTO } from '@/types'

const invitations = ref<InvitationListItem[]>([])
const selectedInvitation = ref<number | undefined>(undefined)
const tables = ref<SeatTable[]>([])
const showAddTable = ref(false)

const newTable = reactive({
  name: '',
  capacity: 10
})

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
  try {
    const res = await getSeatTables(selectedInvitation.value)
    tables.value = res.data
  } catch { /* ignore */ }
}

async function handleAddTable() {
  if (!selectedInvitation.value || !newTable.name) {
    ElMessage.warning('请填写餐桌名称')
    return
  }
  try {
    await createSeatTable(selectedInvitation.value, { ...newTable })
    ElMessage.success('添加成功')
    showAddTable.value = false
    newTable.name = ''
    newTable.capacity = 10
    fetchData()
  } catch (error: any) {
    ElMessage.error(error.message || '添加失败')
  }
}

async function handleDeleteTable(tableId: number) {
  try {
    await ElMessageBox.confirm('确定删除此餐桌？', '确认')
    await deleteSeatTableApi(tableId)
    ElMessage.success('删除成功')
    fetchData()
  } catch { /* cancelled */ }
}

async function handleAssign(data: { tableId: number; assignment: SeatAssignmentDTO }) {
  try {
    await assignSeat(data.tableId, data.assignment)
    ElMessage.success('分配成功')
    fetchData()
  } catch (error: any) {
    ElMessage.error(error.message || '分配失败')
  }
}

async function handleUnassign(assignmentId: number) {
  try {
    await unassignSeat(assignmentId)
    ElMessage.success('取消分配成功')
    fetchData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  fetchInvitations()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.header-actions {
  display: flex;
  gap: 12px;
  margin-left: auto;
}

.seat-canvas-wrapper {
  padding: 20px;
  min-height: 500px;
}

.table-list {
  padding: 16px;
  h4 { margin: 0 0 12px; font-size: 14px; }
}

.table-item {
  margin-bottom: 8px;
}
</style>
