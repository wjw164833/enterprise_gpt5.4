<template>
  <div class="image-uploader">
    <el-upload
      :http-request="handleUpload"
      :show-file-list="false"
      accept="image/*"
      :before-upload="beforeUpload"
      list-type="picture-card"
      :multiple="multiple"
    >
      <div class="upload-trigger">
        <el-icon :size="20"><Plus /></el-icon>
        <span>上传</span>
      </div>
    </el-upload>
    <div v-if="modelValue" class="preview-list">
      <div class="preview-item">
        <img :src="modelValue" alt="" />
        <div class="preview-actions">
          <el-icon @click="handleRemove"><Delete /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { uploadImage } from '@/api/upload'
import { ElMessage } from 'element-plus'

defineProps<{
  modelValue: string
  multiple?: boolean
  maxSize?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const maxSize = 5 * 1024 * 1024
  if (!isImage) {
    ElMessage.error('只能上传图片')
    return false
  }
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

async function handleUpload(options: any) {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const res = await uploadImage(formData)
    emit('update:modelValue', res.data.url)
  } catch {
    ElMessage.error('上传失败')
  }
}

function handleRemove() {
  emit('update:modelValue', '')
}
</script>

<style scoped lang="scss">
.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
}

.preview-list {
  margin-top: 8px;
}

.preview-item {
  position: relative;
  display: inline-block;

  img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: var(--radius-sm);
  }
}

.preview-actions {
  position: absolute;
  top: 0;
  right: 0;
  background: rgba(0,0,0,0.5);
  color: #fff;
  border-radius: 0 0 0 4px;
  cursor: pointer;
  padding: 4px;
}
</style>
