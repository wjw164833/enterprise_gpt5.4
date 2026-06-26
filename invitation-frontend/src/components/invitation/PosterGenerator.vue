<template>
  <div class="poster-generator">
    <el-button type="primary" @click="handleGenerate" :loading="generating">
      <el-icon><PictureFilled /></el-icon> 生成海报
    </el-button>
    <div v-if="posterUrl" class="poster-preview">
      <img :src="posterUrl" alt="海报" class="poster-img" />
      <el-button link type="primary" @click="handleDownload">
        <el-icon><Download /></el-icon> 下载海报
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { usePoster } from '@/composables/usePoster'

const props = defineProps<{
  coverImage: string
  title: string
  date: string
  address: string
  qrcodeUrl?: string
}>()

const { generating, posterUrl, generatePoster, downloadPoster } = usePoster()

async function handleGenerate() {
  await generatePoster({
    width: 375,
    height: 667,
    backgroundColor: '#1a1a2e',
    coverImage: props.coverImage,
    title: props.title,
    date: props.date,
    address: props.address,
    qrcodeUrl: props.qrcodeUrl
  })
}

function handleDownload() {
  downloadPoster(`${props.title}-poster.png`)
}
</script>

<style scoped lang="scss">
.poster-preview {
  margin-top: 12px;
  text-align: center;
}

.poster-img {
  max-width: 100%;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
}
</style>
