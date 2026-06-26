<template>
  <div class="subscription-page page-container">
    <div class="page-header">
      <h2>订阅管理</h2>
    </div>

    <div class="current-plan card-shadow" v-if="currentSub">
      <div class="plan-info">
        <h3>当前方案：{{ formatSubscriptionPlan(currentSub.plan) }}</h3>
        <p>有效期至：{{ formatDate(currentSub.endDate, 'YYYY-MM-DD') }}</p>
        <el-progress :percentage="(currentSub.invitationUsed / currentSub.invitationQuota) * 100" :format="() => `${currentSub.invitationUsed}/${currentSub.invitationQuota}`" />
        <p class="quota-info">邀请函配额：已使用 {{ currentSub.invitationUsed }} / {{ currentSub.invitationQuota }}</p>
        <p class="quota-info">AI配额：已使用 {{ currentSub.aiUsed }} / {{ currentSub.aiQuota }}</p>
      </div>
    </div>

    <h3 class="section-title">升级方案</h3>
    <div class="plan-grid">
      <div v-for="plan in plans" :key="plan.plan" class="plan-card card-shadow" :class="{ active: currentSub?.plan === plan.plan }">
        <div class="plan-badge" v-if="plan.plan === 2">推荐</div>
        <h4 class="plan-name">{{ plan.name }}</h4>
        <div class="plan-price">
          <span class="price-amount">¥{{ plan.price }}</span>
          <span class="price-period">/月</span>
        </div>
        <ul class="plan-features">
          <li v-for="(feat, idx) in plan.features" :key="idx">
            <el-icon color="#67c23a"><Check /></el-icon> {{ feat }}
          </li>
        </ul>
        <el-button
          :type="currentSub?.plan === plan.plan ? 'info' : 'primary'"
          :disabled="currentSub?.plan === plan.plan"
          @click="handleSubscribe(plan.plan)"
          class="plan-btn"
        >
          {{ currentSub?.plan === plan.plan ? '当前方案' : '升级' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCurrentSubscription, getSubscriptionPlans, subscribe } from '@/api/subscription'
import { formatSubscriptionPlan, formatDate } from '@/utils/format'
import { ElMessage } from 'element-plus'
import type { SubscriptionInfo, PlanInfo, SubscriptionPlan } from '@/types'

const currentSub = ref<SubscriptionInfo | null>(null)
const plans = ref<PlanInfo[]>([])

async function fetchData() {
  try {
    const [subRes, planRes] = await Promise.all([
      getCurrentSubscription(),
      getSubscriptionPlans()
    ])
    currentSub.value = subRes.data
    plans.value = planRes.data
  } catch { /* ignore */ }
}

async function handleSubscribe(plan: SubscriptionPlan) {
  try {
    const res = await subscribe({ plan, period: 1 })
    if (res.data?.payUrl) {
      window.open(res.data.payUrl, '_blank')
    }
    ElMessage.success('订单已创建，请完成支付')
  } catch (error: any) {
    ElMessage.error(error.message || '订阅失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.page-header {
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.current-plan {
  padding: 24px;
  margin-bottom: 32px;
}

.plan-info {
  h3 { margin: 0 0 8px; }
  p { color: #606266; margin-bottom: 8px; }
}

.quota-info {
  font-size: 13px;
  color: #909399;
}

.section-title {
  font-size: 18px;
  margin-bottom: 16px;
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.plan-card {
  padding: 32px 24px;
  text-align: center;
  border: 2px solid transparent;
  position: relative;
  transition: all 0.2s;

  &.active {
    border-color: var(--color-primary);
  }
  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }
}

.plan-badge {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #f5af19, #f12711);
  color: #fff;
  padding: 4px 16px;
  border-radius: 12px;
  font-size: 12px;
}

.plan-name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
}

.plan-price {
  margin-bottom: 20px;
}

.price-amount {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-color-primary);
}

.price-period {
  font-size: 14px;
  color: #909399;
}

.plan-features {
  list-style: none;
  padding: 0;
  margin-bottom: 24px;
  text-align: left;

  li {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 6px 0;
    font-size: 14px;
    color: #606266;
  }
}

.plan-btn {
  width: 100%;
}
</style>
