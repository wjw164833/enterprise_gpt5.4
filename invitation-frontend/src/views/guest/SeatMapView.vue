<template>
  <div class="seat-map-page">
    <div class="page-header-guest">
      <el-icon @click="$router.back()" :size="20" color="#fff"><ArrowLeft /></el-icon>
      <h2>座位图</h2>
    </div>

    <div class="seat-map-content" v-loading="loading">
      <div v-for="table in tables" :key="table.id" class="guest-table-card">
        <h4 class="table-name">{{ table.name }}</h4>
        <div class="seat-grid">
          <div
            v-for="assignment in table.assignments"
            :key="assignment.id"
            class="seat-item"
            :class="{ occupied: assignment.guestName }"
          >
            <span class="seat-no">{{ assignment.seatNo }}</span>
            <span class="seat-name" v-if="assignment.guestName">{{ assignment.guestName }}</span>
            <span class="seat-empty" v-else>空位</span>
          </div>
        </div>
      </div>
      <el-empty v-if="!loading && tables.length === 0" description="暂无座位信息" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSeatTables } from '@/api/seat'
import type { SeatTable } from '@/types'

const route = useRoute()
const invitationId = Number(route.query.invitationId) || 0
const loading = ref(false)
const tables = ref<SeatTable[]>([])

async function fetchData() {
  if (!invitationId) return
  loading.value = true
  try {
    const res = await getSeatTables(invitationId)
    tables.value = res.data
  } catch { /* ignore */ }
  finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.seat-map-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #0a0a0a, #1a1a2e);
  color: #fff;
}

.page-header-guest {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  h2 { font-size: 18px; margin: 0; }
}

.seat-map-content {
  padding: 16px;
  max-width: 500px;
  margin: 0 auto;
}

.guest-table-card {
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
}

.table-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px;
  text-align: center;
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 8px;
}

.seat-item {
  text-align: center;
  padding: 8px 4px;
  border-radius: 8px;
  background: rgba(255,255,255,0.05);
  font-size: 12px;
}

.seat-item.occupied {
  background: rgba(64, 158, 255, 0.2);
  border: 1px solid rgba(64, 158, 255, 0.3);
}

.seat-no {
  display: block;
  color: rgba(255,255,255,0.5);
  font-size: 11px;
}

.seat-name {
  color: #fff;
  font-weight: 500;
}

.seat-empty {
  color: rgba(255,255,255,0.3);
}
</style>
