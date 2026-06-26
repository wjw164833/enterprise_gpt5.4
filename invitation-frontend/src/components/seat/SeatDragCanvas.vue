<template>
  <div class="seat-drag-canvas" ref="canvasRef">
    <div class="canvas-area" @drop="handleDrop" @dragover.prevent>
      <div
        v-for="table in tables"
        :key="table.id"
        class="table-block"
        :style="getTableStyle(table)"
        draggable="true"
        @dragstart="handleDragStart($event, table)"
      >
        <h5>{{ table.name }}</h5>
        <div class="seat-grid-inner">
          <div
            v-for="seat in table.assignments"
            :key="seat.id"
            class="seat-dot"
            :class="{ occupied: seat.guestName }"
            :title="seat.guestName || '空位'"
            @dblclick="handleSeatClick(seat, table.id)"
          >
            {{ seat.seatNo }}
          </div>
          <div v-for="i in (table.capacity - table.assignments.length)" :key="'empty-' + i" class="seat-dot empty">
            {{ table.assignments.length + i }}
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showAssignDialog" title="分配座位" width="360px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="座位号">
          <el-input v-model="assignForm.seatNo" disabled />
        </el-form-item>
        <el-form-item label="宾客姓名">
          <el-input v-model="assignForm.guestName" placeholder="输入宾客姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="assignForm.guestPhone" placeholder="输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { SeatTable, SeatAssignment, SeatAssignmentDTO } from '@/types'

const props = defineProps<{
  tables: SeatTable[]
  invitationId: number
}>()

const emit = defineEmits<{
  (e: 'assign', data: { tableId: number; assignment: SeatAssignmentDTO }): void
  (e: 'unassign', assignmentId: number): void
}>()

const showAssignDialog = ref(false)
const currentTableId = ref(0)
const assignForm = reactive({
  seatNo: '',
  guestName: '',
  guestPhone: ''
})

function getTableStyle(table: SeatTable) {
  const config = table.layoutConfig ? JSON.parse(table.layoutConfig) : {}
  return {
    left: `${config.x || 50}px`,
    top: `${config.y || 50}px`
  }
}

function handleDragStart(event: DragEvent, table: SeatTable) {
  event.dataTransfer?.setData('tableId', String(table.id))
}

function handleDrop(event: DragEvent) {
  // Handle table repositioning
}

function handleSeatClick(seat: SeatAssignment, tableId: number) {
  if (seat.guestName) {
    emit('unassign', seat.id)
  } else {
    currentTableId.value = tableId
    assignForm.seatNo = seat.seatNo
    assignForm.guestName = ''
    assignForm.guestPhone = ''
    showAssignDialog.value = true
  }
}

function confirmAssign() {
  if (!assignForm.guestName) return
  emit('assign', {
    tableId: currentTableId.value,
    assignment: {
      seatNo: assignForm.seatNo,
      guestName: assignForm.guestName,
      guestPhone: assignForm.guestPhone || undefined
    }
  })
  showAssignDialog.value = false
}
</script>

<style scoped lang="scss">
.seat-drag-canvas {
  min-height: 400px;
}

.canvas-area {
  position: relative;
  min-height: 400px;
  background: var(--bg-color-page);
  border-radius: var(--radius-md);
  padding: 20px;
}

.table-block {
  position: absolute;
  background: #fff;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 12px;
  cursor: move;
  min-width: 140px;

  h5 {
    text-align: center;
    font-size: 13px;
    margin: 0 0 8px;
    color: var(--text-color-primary);
  }
}

.seat-grid-inner {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 4px;
}

.seat-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  background: #f0f2f5;
  color: #909399;
  cursor: pointer;
  transition: all 0.2s;

  &.occupied {
    background: #409eff;
    color: #fff;
  }

  &:hover {
    transform: scale(1.2);
  }
}
</style>
