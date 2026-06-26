<template>
  <div class="chat-room-page page-container">
    <div class="page-header">
      <h2>聊天室</h2>
      <el-select v-model="selectedInvitation" placeholder="选择邀请函" style="width: 240px" @change="switchInvitation">
        <el-option v-for="inv in invitations" :key="inv.id" :label="inv.title" :value="inv.id" />
      </el-select>
    </div>

    <div class="chat-container card-shadow" v-if="selectedInvitation">
      <div class="chat-messages" ref="messagesRef">
        <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ 'self': msg.userId === userId }">
          <div class="message-avatar">
            <el-avatar :size="32">{{ msg.guestName?.charAt(0) || '我' }}</el-avatar>
          </div>
          <div class="message-content">
            <div class="message-name">{{ msg.guestName || '我' }}</div>
            <div class="message-bubble">
              <span v-if="msg.msgType === 1">{{ msg.content }}</span>
              <img v-else-if="msg.msgType === 2" :src="msg.content" class="message-image" />
            </div>
            <div class="message-time">{{ formatRelativeTime(msg.createdAt) }}</div>
          </div>
        </div>
        <div v-if="messages.length === 0" class="empty-chat">
          <el-empty description="暂无消息，开始聊天吧" />
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputText"
          placeholder="输入消息..."
          @keyup.enter="handleSend"
          :disabled="!wsConnected"
        >
          <template #append>
            <el-button type="primary" @click="handleSend" :disabled="!inputText.trim()">发送</el-button>
          </template>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getMyInvitations } from '@/api/invitation'
import { getChatHistory } from '@/api/chat'
import { useWebSocket } from '@/composables/useWebSocket'
import { formatRelativeTime } from '@/utils/format'
import type { InvitationListItem } from '@/types'
import type { Ref } from 'vue'

const userStore = useUserStore()
const userId = userStore.userInfo?.id || 0
const invitations = ref<InvitationListItem[]>([])
const selectedInvitation = ref<number>(0)
const inputText = ref('')
const messagesRef = ref<HTMLElement | null>(null)

const invitationIdRef = ref(0) as Ref<number>
const { messages, connected: wsConnected, connect, sendMessage, disconnect } = useWebSocket(invitationIdRef)

async function fetchInvitations() {
  try {
    const res = await getMyInvitations({ page: 1, size: 100 })
    invitations.value = res.data.list
    if (invitations.value.length > 0) {
      selectedInvitation.value = invitations.value[0].id
      switchInvitation()
    }
  } catch { /* ignore */ }
}

async function switchInvitation() {
  if (!selectedInvitation.value) return
  disconnect()
  invitationIdRef.value = selectedInvitation.value
  try {
    const res = await getChatHistory(selectedInvitation.value, { page: 1, size: 50 })
    messages.value = res.data.list
  } catch { /* ignore */ }
  connect()
  await nextTick()
  scrollToBottom()
}

function handleSend() {
  if (!inputText.value.trim()) return
  sendMessage(inputText.value.trim())
  inputText.value = ''
  nextTick(() => scrollToBottom())
}

function scrollToBottom() {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

onMounted(() => {
  fetchInvitations()
})

onUnmounted(() => {
  disconnect()
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.chat-container {
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;

  &.self {
    flex-direction: row-reverse;
    .message-bubble {
      background: #409eff;
      color: #fff;
    }
    .message-name {
      text-align: right;
    }
  }
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 60%;
}

.message-name {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.message-bubble {
  background: #f4f4f5;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message-image {
  max-width: 200px;
  border-radius: 8px;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
}

.empty-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.chat-input {
  padding: 12px 16px;
  border-top: 1px solid var(--border-color-light);
}
</style>
