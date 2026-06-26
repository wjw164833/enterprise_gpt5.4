<template>
  <div class="reply-page">
    <div class="page-header-guest">
      <el-icon @click="$router.back()" :size="20" color="#fff"><ArrowLeft /></el-icon>
      <h2>回复出席</h2>
    </div>

    <div class="reply-content">
      <ReplyForm @submit="handleSubmit" :loading="submitting" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { submitReply } from '@/api/guest'
import { ElMessage } from 'element-plus'
import ReplyForm from '@/components/guest/ReplyForm.vue'
import type { GuestReplyDTO } from '@/types'

const route = useRoute()
const router = useRouter()
const shortCode = route.params.shortCode as string
const submitting = ref(false)

async function handleSubmit(data: GuestReplyDTO) {
  submitting.value = true
  try {
    await submitReply(shortCode, data)
    ElMessage.success('回复成功！期待您的到来')
    router.back()
  } catch (error: any) {
    ElMessage.error(error.message || '回复失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.reply-page {
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

.reply-content {
  padding: 16px;
  max-width: 500px;
  margin: 0 auto;
}
</style>
