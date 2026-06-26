<template>
  <div class="bless-wall">
    <div class="wall-header">
      <h3>留言墙</h3>
      <span class="count">{{ blessings.length }}条祝福</span>
    </div>

    <div class="waterfall-container" ref="containerRef">
      <div class="waterfall-column" v-for="(col, colIdx) in columns" :key="colIdx">
        <BlessCard
          v-for="bless in col"
          :key="bless.id"
          :bless="bless"
        />
      </div>
    </div>

    <div class="load-more" v-if="hasMore">
      <el-button @click="loadMore" :loading="loadingMore" size="small">加载更多</el-button>
    </div>

    <div class="bless-input">
      <input v-model="newBless.content" placeholder="写下你的祝福..." />
      <input v-model="newBless.guestName" placeholder="你的名字" class="name-input" />
      <button @click="handleSend" :disabled="!canSend">送出祝福</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getAlbum } from '@/api/album'
import BlessCard from './BlessCard.vue'
import type { BlessMessage, BlessCreateDTO } from '@/types'

// We use a placeholder API since bless API is not in the defined list yet
// In production this would call the actual bless API

const props = defineProps<{ invitationId: number }>()
const emit = defineEmits<{ (e: 'send', data: BlessCreateDTO): void }>()

const blessings = ref<BlessMessage[]>([])
const loadingMore = ref(false)
const hasMore = ref(true)
const page = ref(1)
const columnCount = 2

const newBless = reactive({
  content: '',
  guestName: '',
  guestAvatar: '',
  theme: ''
})

const canSend = computed(() => newBless.content.trim() && newBless.guestName.trim())

const columns = computed(() => {
  const cols: BlessMessage[][] = Array.from({ length: columnCount }, () => [])
  blessings.value.forEach((bless, idx) => {
    cols[idx % columnCount].push(bless)
  })
  return cols
})

async function loadMore() {
  loadingMore.value = true
  page.value++
  // Would call actual bless API
  setTimeout(() => {
    loadingMore.value = false
  }, 500)
}

function handleSend() {
  if (!canSend.value) return
  const data: BlessCreateDTO = {
    content: newBless.content,
    guestName: newBless.guestName,
    guestAvatar: newBless.guestAvatar,
    theme: newBless.theme
  }
  emit('send', data)

  // Optimistically add to local list
  blessings.value.unshift({
    id: Date.now(),
    invitationId: props.invitationId,
    ...data,
    status: 1,
    createdAt: new Date().toISOString()
  })

  newBless.content = ''
}

onMounted(() => {
  // Would fetch initial blessings
})
</script>

<style scoped lang="scss">
.bless-wall {
  padding: 16px;
}

.wall-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 { margin: 0; font-size: 16px; }
}

.count {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.waterfall-container {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.waterfall-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.load-more {
  text-align: center;
  margin-bottom: 16px;
}

.bless-input {
  display: flex;
  gap: 8px;
  padding: 12px;
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border-radius: 12px;

  input {
    flex: 1;
    padding: 8px 12px;
    background: rgba(255,255,255,0.1);
    border: none;
    border-radius: 8px;
    color: #fff;
    font-size: 13px;
    outline: none;

    &::placeholder { color: rgba(255,255,255,0.3); }
  }

  .name-input {
    max-width: 80px;
  }

  button {
    padding: 8px 16px;
    background: linear-gradient(135deg, #f5af19, #f12711);
    color: #fff;
    border: none;
    border-radius: 8px;
    font-size: 13px;
    cursor: pointer;

    &:disabled { opacity: 0.5; }
  }
}
</style>
