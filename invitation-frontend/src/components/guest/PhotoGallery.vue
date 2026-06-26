<template>
  <div class="photo-gallery">
    <div class="gallery-header" v-if="photos.length > 0">
      <h3>相册</h3>
    </div>
    <div class="gallery-grid" v-if="photos.length > 0">
      <div
        v-for="photo in photos"
        :key="photo.id"
        class="gallery-item"
        @click="previewPhoto(photo)"
      >
        <img :src="photo.thumbnailUrl || photo.url" :alt="'照片'" class="gallery-img" />
      </div>
    </div>

    <el-image-viewer
      v-if="showViewer"
      :url-list="viewerUrls"
      :initial-index="viewerIndex"
      @close="showViewer = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAlbum } from '@/api/album'
import type { PhotoInfo } from '@/types'

const props = defineProps<{ invitationId: number }>()

const photos = ref<PhotoInfo[]>([])
const showViewer = ref(false)
const viewerIndex = ref(0)
const viewerUrls = ref<string[]>([])

async function fetchPhotos() {
  try {
    const res = await getAlbum(props.invitationId)
    photos.value = res.data
  } catch { /* ignore */ }
}

function previewPhoto(photo: PhotoInfo) {
  viewerUrls.value = photos.value.map(p => p.url)
  viewerIndex.value = photos.value.findIndex(p => p.id === photo.id)
  showViewer.value = true
}

onMounted(() => {
  fetchPhotos()
})
</script>

<style scoped lang="scss">
.gallery-header {
  margin-bottom: 12px;
  h3 { font-size: 16px; margin: 0; color: #fff; }
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
  margin-bottom: 24px;
}

.gallery-item {
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover { transform: scale(1.05); }
}

.gallery-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
