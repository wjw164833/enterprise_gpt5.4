<template>
  <div class="short-link-share">
    <div v-if="shortUrl" class="link-container">
      <el-input :model-value="shortUrl" readonly>
        <template #append>
          <el-button @click="handleCopy">复制</el-button>
        </template>
      </el-input>
    </div>
    <el-button v-else type="primary" @click="handleGenerate" :loading="generating">
      生成分享链接
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import copy from 'clipboard-copy'
import { ElMessage } from 'element-plus'
import { getInvitationDetail } from '@/api/invitation'

const props = defineProps<{ invitationId: number }>()
const shortUrl = ref('')
const generating = ref(false)

async function handleGenerate() {
  generating.value = true
  try {
    const res = await getInvitationDetail(props.invitationId)
    const data = res.data
    if (data.shortCode) {
      shortUrl.value = `${window.location.origin}/guest/i/${data.shortCode}`
    }
  } catch {
    ElMessage.error('获取分享链接失败')
  } finally {
    generating.value = false
  }
}

async function handleCopy() {
  try {
    await copy(shortUrl.value)
    ElMessage.success('链接已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped lang="scss">
.link-container {
  margin-bottom: 8px;
}
</style>
