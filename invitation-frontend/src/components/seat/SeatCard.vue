<template>
  <div class="seat-card">
    <div class="card-header">
      <h4>{{ table.name }}</h4>
      <el-button type="danger" link size="small" @click="$emit('delete')">
        <el-icon><Delete /></el-icon>
      </el-button>
    </div>
    <div class="card-body">
      <div class="capacity-info">
        <span>{{ table.assignments.length }}/{{ table.capacity }}</span>
        <el-progress :percentage="(table.assignments.length / table.capacity) * 100" :show-text="false" :stroke-width="4" />
      </div>
      <div class="assigned-list" v-if="table.assignments.length > 0">
        <el-tag v-for="a in table.assignments" :key="a.id" size="small" closable @close="handleUnassign(a.id)">
          {{ a.guestName }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SeatTable } from '@/types'

defineProps<{ table: SeatTable }>()
const emit = defineEmits<{
  (e: 'delete'): void
  (e: 'unassign', id: number): void
}>()

function handleUnassign(id: number) {
  emit('unassign', id)
}
</script>

<style scoped lang="scss">
.seat-card {
  border: 1px solid var(--border-color-light);
  border-radius: var(--radius-md);
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  h4 { margin: 0; font-size: 14px; }
}

.card-body {
  margin-top: 8px;
}

.capacity-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #909399;
}

.assigned-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
</style>
