<template>
  <div class="template-selector">
    <div class="template-grid">
      <div
        v-for="tpl in templates"
        :key="tpl.id"
        class="template-card"
        :class="{ selected: modelValue === tpl.id }"
        @click="$emit('update:modelValue', tpl.id)"
      >
        <img :src="tpl.coverImage" :alt="tpl.name" class="tpl-cover" />
        <div class="tpl-name">{{ tpl.name }}</div>
        <el-tag v-if="tpl.isFree" type="success" size="small" class="tpl-tag">免费</el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTemplateList } from '@/api/template'
import type { TemplateInfo } from '@/types'

defineProps<{ modelValue: number }>()
defineEmits<{ (e: 'update:modelValue', value: number): void }>()

const templates = ref<TemplateInfo[]>([])

onMounted(async () => {
  try {
    const res = await getTemplateList({ page: 1, size: 20 })
    templates.value = res.data.list
  } catch { /* ignore */ }
})
</script>

<style scoped lang="scss">
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

.template-card {
  border: 2px solid var(--border-color-light);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;

  &:hover { border-color: var(--color-primary); }
  &.selected {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3);
  }
}

.tpl-cover {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.tpl-name {
  padding: 8px;
  font-size: 13px;
  text-align: center;
}

.tpl-tag {
  position: absolute;
  top: 4px;
  right: 4px;
}
</style>
