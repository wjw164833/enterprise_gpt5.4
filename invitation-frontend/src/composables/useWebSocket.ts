import { ref, onUnmounted } from 'vue'
import type { ChatMessage, WsMessage } from '@/types'
import { getAccessToken } from '@/api/request'

export function useWebSocket(invitationId: Ref<number | string>) {
  const messages = ref<ChatMessage[]>([])
  const connected = ref(false)
  const ws = ref<WebSocket | null>(null)
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null
  let reconnectCount = 0
  const maxReconnect = 5

  function connect() {
    const token = getAccessToken()
    const wsUrl = `${import.meta.env.VITE_WS_URL}/chat?token=${token}&invitationId=${invitationId.value}`
    ws.value = new WebSocket(wsUrl)

    ws.value.onopen = () => {
      connected.value = true
      reconnectCount = 0
    }

    ws.value.onmessage = (event: MessageEvent) => {
      try {
        const msg: WsMessage = JSON.parse(event.data)
        if (msg.type === 'chat') {
          messages.value.push(msg.data as ChatMessage)
        } else if (msg.type === 'history') {
          messages.value = msg.data as ChatMessage[]
        }
      } catch {
        // ignore parse error
      }
    }

    ws.value.onclose = () => {
      connected.value = false
      attemptReconnect()
    }

    ws.value.onerror = () => {
      connected.value = false
    }
  }

  function attemptReconnect() {
    if (reconnectCount >= maxReconnect) return
    reconnectCount++
    const delay = Math.min(1000 * Math.pow(2, reconnectCount), 30000)
    reconnectTimer = setTimeout(() => {
      connect()
    }, delay)
  }

  function sendMessage(content: string, msgType: number = 1) {
    if (!ws.value || ws.value.readyState !== WebSocket.OPEN) return
    ws.value.send(JSON.stringify({
      type: 'chat',
      data: { content, msgType }
    }))
  }

  function disconnect() {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    if (ws.value) {
      ws.value.close()
      ws.value = null
    }
    connected.value = false
  }

  onUnmounted(() => {
    disconnect()
  })

  return {
    messages,
    connected,
    connect,
    sendMessage,
    disconnect
  }
}

import type { Ref } from 'vue'
