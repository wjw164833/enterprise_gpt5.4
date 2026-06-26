<template>
  <div class="cover-uploader">
    <el-upload
      class="cover-upload-area"
      :http-request="handleUpload"
      :show-file-list="false"
      accept="image/*"
      :before-upload="beforeUpload"
    >
      <img v-if="modelValue" :src="modelValue" class="cover-preview" />
      <div v-else class="upload-placeholder">
        <el-icon :size="32"><Plus /></el-icon>
        <span>上传封面</span>
      </div>
    </el-upload>
    <el-button v-if="modelValue" link type="primary" size="small" @click="openCropper" class="crop-btn">
      裁剪图片
    </el-button>

    <el-dialog v-model="showCropper" title="裁剪封面" width="600px" :close-on-click-modal="false">
      <div class="cropper-container">
        <VueCropper
          ref="cropperRef"
          :img="cropperImg"
          :output-size="1"
          output-type="png"
          :auto-crop="true"
          :auto-crop-width="375"
          :auto-crop-height="250"
          :fixed="true"
          :fixed-number="[3, 2]"
        />
      </div>
      <template #footer>
        <el-button @click="showCropper = false">取消</el-button>
        <el-button type="primary" @click="handleCrop">确认裁剪</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { uploadImage } from '@/api/upload'
import { ElMessage } from 'element-plus'
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'

defineProps<{ modelValue: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>()

const showCropper = ref(false)
const cropperImg = ref('')
const cropperRef = ref<any>(null)

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
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
    ElMessage.success('上传成功')
  } catch {
    ElMessage.error('上传失败')
  }
}

function openCropper() {
  cropperImg.value = '' // Would set from current cover
  showCropper.value = true
}

function handleCrop() {
  if (!cropperRef.value) return
  cropperRef.value.getCropBlob((blob: Blob) => {
    const formData = new FormData()
    formData.append('file', blob, 'cover.png')
    uploadImage(formData).then((res) => {
      emit('update:modelValue', res.data.url)
      showCropper.value = false
      ElMessage.success('裁剪成功')
    }).catch(() => {
      ElMessage.error('上传失败')
    })
  })
}
</script>

<style scoped lang="scss">
.cover-uploader {
  text-align: center;
}

.cover-upload-area {
  :deep(.el-upload) {
    width: 100%;
    border: 2px dashed var(--border-color);
    border-radius: var(--radius-md);
    cursor: pointer;
    overflow: hidden;
    transition: border-color 0.2s;

    &:hover { border-color: var(--color-primary); }
  }
}

.cover-preview {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 32px;
  color: #909399;
}

.cropper-container {
  height: 400px;
}

.crop-btn {
  margin-top: 8px;
}
</style>
