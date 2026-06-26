<template>
  <div class="invitation-edit-page page-container">
    <div class="page-header">
      <el-button link @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <h2>编辑邀请函</h2>
      <div class="header-actions">
        <el-button @click="$router.push(`/admin/invitations/${invitationId}/preview`)">预览</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </div>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="16">
        <div class="card-shadow edit-section">
          <InvitationForm :form="form" @ai-generate="handleAiGenerate" />
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card-shadow side-section">
          <h4>封面图片</h4>
          <CoverUploader v-model="form.coverImage" />
        </div>
        <div class="card-shadow side-section">
          <h4>AI助手</h4>
          <AiGeneratePanel
            :activity-type="form.activityType"
            @select="handleAiSelect"
          />
        </div>
        <div class="card-shadow side-section">
          <h4>分享设置</h4>
          <ShortLinkShare v-if="invitationId" :invitation-id="invitationId" />
          <p v-else class="text-gray-400 text-sm">保存邀请函后可生成分享链接</p>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getInvitationDetail, updateInvitation } from '@/api/invitation'
import { generateGreeting } from '@/api/ai'
import { ElMessage } from 'element-plus'
import InvitationForm from '@/components/invitation/InvitationForm.vue'
import CoverUploader from '@/components/invitation/CoverUploader.vue'
import AiGeneratePanel from '@/components/invitation/AiGeneratePanel.vue'
import ShortLinkShare from '@/components/invitation/ShortLinkShare.vue'
import type { ActivityType } from '@/types'

const route = useRoute()
const invitationId = Number(route.params.id)
const loading = ref(false)
const saving = ref(false)

const form = reactive({
  title: '',
  activityType: 1 as ActivityType,
  templateId: 0,
  coverImage: '',
  content: '',
  activityDate: '',
  activityAddress: '',
  latitude: undefined as number | undefined,
  longitude: undefined as number | undefined,
  musicId: undefined as number | undefined,
  aiGreeting: ''
})

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getInvitationDetail(invitationId)
    const data = res.data
    Object.assign(form, {
      title: data.title,
      activityType: data.activityType,
      templateId: data.templateId,
      coverImage: data.coverImage,
      content: data.content,
      activityDate: data.activityDate,
      activityAddress: data.activityAddress,
      latitude: data.latitude,
      longitude: data.longitude,
      musicId: data.musicId,
      aiGreeting: data.aiGreeting
    })
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  if (!form.title) {
    ElMessage.warning('请填写标题')
    return
  }
  saving.value = true
  try {
    await updateInvitation(invitationId, { ...form })
    ElMessage.success('保存成功')
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleAiGenerate() {
  try {
    const res = await generateGreeting({
      activityType: form.activityType,
      style: 'elegant'
    })
    if (res.data?.result) {
      form.aiGreeting = res.data.result
    }
  } catch {
    ElMessage.error('AI生成失败')
  }
}

function handleAiSelect(text: string) {
  form.aiGreeting = text
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  h2 { margin: 0; flex: 1; }
}

.header-actions {
  display: flex;
  gap: 8px;
}

.edit-section {
  padding: 24px;
}

.side-section {
  padding: 16px;
  margin-bottom: 16px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 12px;
  }
}
</style>
