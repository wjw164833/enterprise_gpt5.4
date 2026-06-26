<template>
  <div class="guest-chat-page">
    <div class="page-header-guest">
      <el-icon @click="$router.back()" :size="20" color="#fff"><ArrowLeft /></el-icon>
      <h2>聊天室</h2>
      <span class="online-badge" :class="{ active: wsConnected }">{{ wsConnected ? '在线' : '离线' }}</span>
    </div>

    <div class="chat-messages">
      <div v-for="msg in messages" :key="msg.id" class="message-item">
        <div class="msg-avatar">{{ (msg.guestName || '我').charAt(0) }}</div>
        <div class="msg-body">
          <span class="msg-name">{{ msg.guestName || '我' }}</span>
          <div class="msg-bubble">{{ msg.content }}</div>
        </div>
      </div>
    </div>

    <div class="chat-input-bar">
      <input v-model="inputText" placeholder="输入消息..." @keyup.enter="handleSend" />
      <button @click="handleSend" :disabled="!inputText.trim()">发送</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getChatHistory } from '@/api/chat'
import { useWebSocket } from '@/composables/useWebSocket'
import type { Ref } from 'vue'

const route = useRoute()
const invitationId = Number(route.query.invitationId) || 0
const inputText = ref('')

const invitationIdRef = ref(invitationId) as Ref<number>
const { messages, connected: wsConnected, connect, sendMessage, disconnect } = useWebSocket(invitationIdRef)

async function loadHistory() {
  if (!invitationId) return
  try {
    const res = await getChatHistory(invitationId, { page: 1, size: 50 })
    messages.value = res.data.list
  } catch { /* ignore */ }
}

function handleSend() {
  if (!inputText.value.trim()) return
  sendMessage(inputText.value.trim())
  inputText.value = ''
}

onMounted(() => {
  loadHistory()
  connect()
})

onUnmounted(() => {
  disconnect()
})
</script>

<style scoped lang="scss">
.guest-chat-page {
  min-height: 100vh;
  background: #0a0a0a;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.page-header-guest {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  h2 { font-size: 18px; margin: 0; flex: 1; }
}

.online-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 8px;
  background: rgba(255,255,255,0.1);
  color: rgba(255,255,255,0.5);
  &.active { background: rgba(103, 194, 58, 0.2); color: #67c23a; }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message-item {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255,255,255,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.msg-name {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  display: block;
  margin-bottom: 2px;
}

.msg-bubble {
  background: rgba(255,255,255,0.1);
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 14px;
  max-width: 70%;
  word-break: break-word;
}

.chat-input-bar {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(255,255,255,0.05);
  border-top: 1px solid rgba(255,255,255,0.1);

  input {
    flex: 1;
    padding: 10px 16px;
    background: rgba(255,255,255,0.08);
    border: none;
    border-radius: 20px;
    color: #fff;
    font-size: 14px;
    outline: none;

    &::placeholder { color: rgba(255,255,255,0.3); }
  }

  button {
    padding: 10px 20px;
    background: #409eff;
    color: #fff;
    border: none;
    border-radius: 20px;
    font-size: 14px;
    cursor: pointer;

    &:disabled { opacity: 0.5; }
  }
}
</style>
