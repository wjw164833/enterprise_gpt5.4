<template>
  <div class="gift-pay-page">
    <div class="page-header-guest">
      <el-icon @click="$router.back()" :size="20" color="#fff"><ArrowLeft /></el-icon>
      <h2>送礼金</h2>
    </div>

    <div class="gift-content">
      <div class="amount-grid">
        <div
          v-for="amt in presetAmounts"
          :key="amt"
          class="amount-item"
          :class="{ active: form.amount === amt }"
          @click="form.amount = amt"
        >
          ¥{{ amt }}
        </div>
      </div>

      <div class="custom-amount">
        <label>自定义金额</label>
        <input v-model.number="customAmount" type="number" placeholder="输入金额" @input="form.amount = customAmount" />
      </div>

      <div class="gift-form">
        <div class="form-item">
          <label>您的姓名</label>
          <input v-model="form.guestName" placeholder="请输入姓名" />
        </div>
        <div class="form-item">
          <label>祝福语</label>
          <textarea v-model="form.message" placeholder="写下您的祝福..." rows="3"></textarea>
        </div>
      </div>

      <button class="pay-btn" :disabled="!canPay" @click="handlePay">
        🧧 送出 ¥{{ form.amount || 0 }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRoute } from 'vue-router'
import { createGiftPayment } from '@/api/payment'
import { ElMessage } from 'element-plus'

const route = useRoute()
const invitationId = Number(route.query.invitationId) || 0

const presetAmounts = [66, 88, 168, 288, 520, 666, 888, 1314]
const customAmount = ref<number | undefined>(undefined)

const form = reactive({
  amount: 0,
  guestName: '',
  message: ''
})

const canPay = computed(() => form.amount > 0 && form.guestName.trim())

async function handlePay() {
  if (!canPay.value) return
  try {
    const res = await createGiftPayment({
      invitationId,
      guestName: form.guestName,
      amount: form.amount,
      message: form.message
    })
    if (res.data?.payUrl) {
      window.location.href = res.data.payUrl
    } else {
      ElMessage.success('礼金已送出！')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '支付失败')
  }
}
</script>

<style scoped lang="scss">
.gift-pay-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #0a0a0a, #1a1a2e);
  color: #fff;
}

.page-header-guest {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  h2 { font-size: 18px; margin: 0; }
}

.gift-content {
  padding: 16px;
  max-width: 500px;
  margin: 0 auto;
}

.amount-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.amount-item {
  text-align: center;
  padding: 14px 8px;
  border-radius: 10px;
  background: rgba(255,255,255,0.08);
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.2s;

  &:hover { background: rgba(255,255,255,0.12); }
  &.active {
    background: linear-gradient(135deg, #f5af19, #f12711);
    color: #fff;
  }
}

.custom-amount {
  margin-bottom: 20px;

  label {
    display: block;
    font-size: 14px;
    margin-bottom: 8px;
    color: rgba(255,255,255,0.7);
  }

  input {
    width: 100%;
    padding: 12px 16px;
    background: rgba(255,255,255,0.08);
    border: 1px solid rgba(255,255,255,0.15);
    border-radius: 10px;
    color: #fff;
    font-size: 16px;
    outline: none;

    &:focus { border-color: #f5af19; }
  }
}

.gift-form {
  margin-bottom: 24px;
}

.form-item {
  margin-bottom: 16px;

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

    &:focus { border-color: #f5af19; }
    &::placeholder { color: rgba(255,255,255,0.3); }
  }
}

.pay-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #f5af19, #f12711);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover { opacity: 0.9; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}
</style>
