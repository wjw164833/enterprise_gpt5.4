<template>
  <div class="reply-form">
    <div class="form-group">
      <label>您的姓名</label>
      <input v-model="form.guestName" placeholder="请输入姓名" />
    </div>

    <div class="form-group">
      <label>手机号（选填）</label>
      <input v-model="form.guestPhone" type="tel" placeholder="选填" maxlength="11" />
    </div>

    <div class="form-group">
      <label>是否出席</label>
      <div class="reply-options">
        <div
          v-for="opt in replyOptions"
          :key="opt.value"
          class="reply-option"
          :class="{ active: form.replyStatus === opt.value, [opt.class]: form.replyStatus === opt.value }"
          @click="form.replyStatus = opt.value"
        >
          <span class="option-icon">{{ opt.icon }}</span>
          <span class="option-label">{{ opt.label }}</span>
        </div>
      </div>
    </div>

    <div class="form-group" v-if="form.replyStatus === 1">
      <label>出席人数</label>
      <div class="number-input">
        <button @click="form.guestCount = Math.max(1, form.guestCount - 1)">-</button>
        <span>{{ form.guestCount }}</span>
        <button @click="form.guestCount = Math.min(20, form.guestCount + 1)">+</button>
      </div>
    </div>

    <div class="form-group">
      <label>留言备注</label>
      <textarea v-model="form.remark" placeholder="有什么想说的..." rows="3"></textarea>
    </div>

    <button class="submit-btn" :disabled="!canSubmit" @click="handleSubmit">
      提交回复
    </button>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import type { GuestReplyDTO, ReplyStatus } from '@/types'

const emit = defineEmits<{ (e: 'submit', data: GuestReplyDTO): void }>()
defineProps<{ loading?: boolean }>()

const replyOptions = [
  { label: '出席', value: 1, icon: '✅', class: 'attend' },
  { label: '不确定', value: 2, icon: '🤔', class: 'uncertain' },
  { label: '不出席', value: 3, icon: '❌', class: 'decline' }
]

const form = reactive<GuestReplyDTO>({
  guestName: '',
  guestPhone: '',
  replyStatus: 1,
  guestCount: 1,
  remark: ''
})

const canSubmit = computed(() => form.guestName.trim() && form.replyStatus)

function handleSubmit() {
  if (!canSubmit.value) return
  emit('submit', { ...form })
}
</script>

<style scoped lang="scss">
.reply-form {
  padding: 16px;
}

.form-group {
  margin-bottom: 20px;

  label {
    display: block;
    font-size: 14px;
    margin-bottom: 8px;
    color: rgba(255,255,255,0.7);
  }

  input, textarea {
    width: 100%;
    padding: 12px 16px;
    background: rgba(255,255,255,0.08);
    border: 1px solid rgba(255,255,255,0.15);
    border-radius: 10px;
    color: #fff;
    font-size: 14px;
    outline: none;
    resize: none;

    &:focus { border-color: var(--color-primary); }
    &::placeholder { color: rgba(255,255,255,0.3); }
  }
}

.reply-options {
  display: flex;
  gap: 10px;
}

.reply-option {
  flex: 1;
  padding: 14px 8px;
  text-align: center;
  background: rgba(255,255,255,0.08);
  border: 2px solid transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;

  &.attend { border-color: #67c23a; background: rgba(103, 194, 58, 0.15); }
  &.uncertain { border-color: #e6a23c; background: rgba(230, 162, 60, 0.15); }
  &.decline { border-color: #f56c6c; background: rgba(245, 108, 108, 0.15); }
}

.option-icon {
  display: block;
  font-size: 24px;
  margin-bottom: 4px;
}

.option-label {
  font-size: 13px;
  color: #fff;
}

.number-input {
  display: flex;
  align-items: center;
  gap: 16px;

  button {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: rgba(255,255,255,0.1);
    border: none;
    color: #fff;
    font-size: 18px;
    cursor: pointer;
  }

  span {
    font-size: 20px;
    font-weight: 600;
    min-width: 30px;
    text-align: center;
  }
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover { opacity: 0.9; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}
</style>
