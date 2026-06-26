<template>
  <div class="template-list-page page-container">
    <div class="page-header">
      <h2>模板中心</h2>
    </div>

    <div class="card-shadow filter-bar">
      <el-form :inline="true">
        <el-form-item label="活动类型">
          <el-select v-model="queryParams.templateType" placeholder="全部" clearable @change="fetchData">
            <el-option label="婚礼" :value="1" />
            <el-option label="生日" :value="2" />
            <el-option label="商务" :value="3" />
            <el-option label="通用" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="风格">
          <el-select v-model="queryParams.style" placeholder="全部" clearable @change="fetchData">
            <el-option label="经典" value="classic" />
            <el-option label="现代" value="modern" />
            <el-option label="中式" value="chinese" />
            <el-option label="清新" value="fresh" />
            <el-option label="奢华" value="luxury" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <div class="template-grid" v-loading="loading">
      <div v-for="tpl in templates" :key="tpl.id" class="template-card card-shadow" @click="handleSelect(tpl)">
        <div class="template-cover-wrapper">
          <img :src="tpl.coverImage" :alt="tpl.name" class="template-cover" />
          <div class="template-overlay">
            <el-button type="primary" size="small">使用此模板</el-button>
          </div>
        </div>
        <div class="template-info">
          <h4>{{ tpl.name }}</h4>
          <div class="template-meta">
            <el-tag v-if="tpl.isFree" type="success" size="small">免费</el-tag>
            <el-tag v-else type="warning" size="small">付费</el-tag>
            <span class="usage-count">{{ tpl.usageCount }}次使用</span>
          </div>
        </div>
      </div>
    </div>

    <div class="load-more" v-if="hasMore">
      <el-button @click="loadMore" :loading="loadingMore">加载更多</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTemplateList, incrementTemplateUsage } from '@/api/template'
import type { TemplateInfo } from '@/types'

const router = useRouter()
const loading = ref(false)
const loadingMore = ref(false)
const templates = ref<TemplateInfo[]>([])
const hasMore = ref(true)

const queryParams = reactive({
  page: 1,
  size: 20,
  templateType: undefined as number | undefined,
  style: undefined as string | undefined
})

async function fetchData() {
  loading.value = true
  queryParams.page = 1
  try {
    const res = await getTemplateList(queryParams)
    templates.value = res.data.list
    hasMore.value = templates.value.length < res.data.total
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  loadingMore.value = true
  queryParams.page++
  try {
    const res = await getTemplateList(queryParams)
    templates.value.push(...res.data.list)
    hasMore.value = templates.value.length < res.data.total
  } finally {
    loadingMore.value = false
  }
}

async function handleSelect(tpl: TemplateInfo) {
  try {
    await incrementTemplateUsage(tpl.id)
  } catch { /* ignore */ }
  router.push({ path: '/admin/invitations/create', query: { templateId: String(tpl.id) } })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.page-header {
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.filter-bar {
  padding: 16px 20px;
  margin-bottom: 20px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.template-card {
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);

    .template-overlay { opacity: 1; }
  }
}

.template-cover-wrapper {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.template-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.template-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.template-info {
  padding: 12px 16px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    margin: 0 0 8px;
  }
}

.template-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.usage-count {
  font-size: 12px;
  color: #909399;
}

.load-more {
  text-align: center;
  margin-top: 24px;
}
</style>
