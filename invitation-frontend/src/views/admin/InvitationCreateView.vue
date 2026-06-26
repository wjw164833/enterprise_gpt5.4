<template>
  <div class="invitation-create-page page-container">
    <div class="page-header">
      <el-button link @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <h2>创建邀请函</h2>
    </div>

    <el-steps :active="currentStep" align-center class="step-bar">
      <el-step title="选择类型" />
      <el-step title="选择模板" />
      <el-step title="填写内容" />
      <el-step title="预览确认" />
    </el-steps>

    <div class="step-content card-shadow">
      <!-- Step 1: 活动类型 -->
      <div v-if="currentStep === 0" class="type-selector">
        <h3>选择活动类型</h3>
        <div class="type-grid">
          <div
            v-for="t in activityTypes"
            :key="t.value"
            class="type-card"
            :class="{ active: form.activityType === t.value }"
            @click="form.activityType = t.value"
          >
            <el-icon :size="36"><component :is="t.icon" /></el-icon>
            <span>{{ t.label }}</span>
          </div>
        </div>
      </div>

      <!-- Step 2: 选择模板 -->
      <div v-if="currentStep === 1" class="template-selector">
        <h3>选择模板</h3>
        <div class="template-filters">
          <el-radio-group v-model="templateStyle" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="classic">经典</el-radio-button>
            <el-radio-button label="modern">现代</el-radio-button>
            <el-radio-button label="chinese">中式</el-radio-button>
            <el-radio-button label="fresh">清新</el-radio-button>
          </el-radio-group>
        </div>
        <div class="template-grid" v-loading="templatesLoading">
          <div
            v-for="tpl in templates"
            :key="tpl.id"
            class="template-card"
            :class="{ active: form.templateId === tpl.id }"
            @click="form.templateId = tpl.id"
          >
            <img :src="tpl.coverImage" :alt="tpl.name" class="template-cover" />
            <div class="template-name">{{ tpl.name }}</div>
            <el-tag v-if="tpl.isFree" type="success" size="small">免费</el-tag>
            <el-tag v-else type="warning" size="small">付费</el-tag>
          </div>
        </div>
      </div>

      <!-- Step 3: 填写内容 -->
      <div v-if="currentStep === 2">
        <InvitationForm :form="form" @ai-generate="handleAiGenerate" />
      </div>

      <!-- Step 4: 预览确认 -->
      <div v-if="currentStep === 3" class="preview-step">
        <div class="preview-phone">
          <div class="phone-frame">
            <div class="phone-screen">
              <div class="preview-content" v-html="form.content || '<p style=color:#999>暂无内容</p>'"></div>
            </div>
          </div>
        </div>
        <div class="preview-info">
          <h3>{{ form.title }}</h3>
          <p>活动类型：{{ formatActivityType(form.activityType) }}</p>
          <p>活动时间：{{ form.activityDate || '未设置' }}</p>
          <p>活动地点：{{ form.activityAddress || '未设置' }}</p>
        </div>
      </div>

      <div class="step-actions">
        <el-button v-if="currentStep > 0" @click="currentStep--">上一步</el-button>
        <el-button v-if="currentStep < 3" type="primary" @click="handleNext" :disabled="!canNext">下一步</el-button>
        <el-button v-if="currentStep === 3" type="success" @click="handleCreate" :loading="creating">创建邀请函</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTemplateList } from '@/api/template'
import { createInvitation } from '@/api/invitation'
import { generateGreeting } from '@/api/ai'
import { formatActivityType } from '@/utils/format'
import { ElMessage } from 'element-plus'
import InvitationForm from '@/components/invitation/InvitationForm.vue'
import type { TemplateInfo, ActivityType } from '@/types'

const router = useRouter()
const currentStep = ref(0)
const creating = ref(false)
const templatesLoading = ref(false)
const templates = ref<TemplateInfo[]>([])
const templateStyle = ref('')

const activityTypes = [
  { label: '婚礼', value: 1, icon: 'Love' },
  { label: '生日', value: 2, icon: 'Birthday' },
  { label: '商务', value: 3, icon: 'Briefcase' },
  { label: '升学', value: 4, icon: 'School' },
  { label: '其他', value: 5, icon: 'MoreFilled' }
]

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

const canNext = computed(() => {
  if (currentStep.value === 0) return form.activityType > 0
  if (currentStep.value === 1) return form.templateId > 0
  if (currentStep.value === 2) return !!form.title
  return true
})

async function fetchTemplates() {
  templatesLoading.value = true
  try {
    const res = await getTemplateList({
      page: 1,
      size: 50,
      templateType: form.activityType,
      style: templateStyle.value || undefined
    })
    templates.value = res.data.list
  } finally {
    templatesLoading.value = false
  }
}

function handleNext() {
  if (!canNext.value) return
  if (currentStep.value === 1) {
    // Template selected
  }
  currentStep.value++
}

async function handleAiGenerate() {
  try {
    const res = await generateGreeting({
      activityType: form.activityType,
      style: 'elegant'
    })
    if (res.data?.result) {
      form.aiGreeting = res.data.result
      ElMessage.success('AI邀请语生成成功')
    }
  } catch {
    ElMessage.error('AI生成失败，请稍后重试')
  }
}

async function handleCreate() {
  creating.value = true
  try {
    const res = await createInvitation({
      title: form.title,
      activityType: form.activityType,
      templateId: form.templateId,
      coverImage: form.coverImage,
      content: form.content,
      activityDate: form.activityDate,
      activityAddress: form.activityAddress,
      latitude: form.latitude,
      longitude: form.longitude,
      musicId: form.musicId,
      aiGreeting: form.aiGreeting
    })
    ElMessage.success('创建成功')
    router.push(`/admin/invitations/${res.data.id}/edit`)
  } catch (error: any) {
    ElMessage.error(error.message || '创建失败')
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  fetchTemplates()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.step-bar {
  margin-bottom: 24px;
}

.step-content {
  padding: 24px;
  min-height: 400px;
}

.type-selector {
  h3 { margin-bottom: 20px; }
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}

.type-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px 16px;
  border: 2px solid var(--border-color-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;

  &:hover { border-color: var(--color-primary); }
  &.active {
    border-color: var(--color-primary);
    background: var(--color-primary);
    color: #fff;
  }
}

.template-selector {
  h3 { margin-bottom: 16px; }
}

.template-filters {
  margin-bottom: 16px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.template-card {
  border: 2px solid var(--border-color-light);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;

  &:hover { border-color: var(--color-primary); }
  &.active { border-color: var(--color-primary); box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3); }
}

.template-cover {
  width: 100%;
  height: 140px;
  object-fit: cover;
}

.template-name {
  padding: 8px;
  font-size: 13px;
  text-align: center;
}

.preview-step {
  display: flex;
  gap: 32px;
}

.preview-phone {
  flex-shrink: 0;
}

.phone-frame {
  width: 375px;
  height: 667px;
  border: 8px solid #1a1a1a;
  border-radius: 36px;
  overflow: hidden;
  background: #fff;
}

.phone-screen {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.preview-content {
  padding: 20px;
}

.preview-info {
  flex: 1;
  h3 { font-size: 18px; margin-bottom: 12px; }
  p { color: var(--text-color-regular); margin-bottom: 8px; }
}

.step-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color-light);
}
</style>
