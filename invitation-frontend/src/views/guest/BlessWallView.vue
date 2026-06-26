<template>
  <div class="bless-wall-page">
    <div class="page-header-guest">
      <el-icon @click="$router.back()" :size="20" color="#fff"><ArrowLeft /></el-icon>
      <h2>留言墙</h2>
    </div>

    <BlessWall :invitation-id="invitationId" @send="handleSendBless" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import BlessWall from '@/components/guest/BlessWall.vue'
import type { BlessCreateDTO } from '@/types'

const route = useRoute()
const invitationId = computed(() => Number(route.query.invitationId) || 0)

async function handleSendBless(data: BlessCreateDTO) {
  // BlessWall component handles the API call internally
}
</script>

<style scoped lang="scss">
.bless-wall-page {
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
</style>
