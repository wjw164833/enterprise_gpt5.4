<template>
  <div class="qrcode-display">
    <div v-if="qrcodeUrl" class="qrcode-wrapper">
      <img :src="qrcodeUrl" alt="小程序码" class="qrcode-img" />
      <p class="qrcode-tip">微信扫一扫查看邀请函</p>
    </div>
    <div v-else class="qrcode-placeholder">
      <el-icon :size="48" color="#909399"><Cellphone /></el-icon>
      <p>暂无小程序码</p>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ qrcodeUrl: string }>()
</script>

<style scoped lang="scss">
.qrcode-wrapper {
  text-align: center;
}

.qrcode-img {
  width: 180px;
  height: 180px;
  border-radius: 8px;
}

.qrcode-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.qrcode-placeholder {
  text-align: center;
  padding: 24px;
  color: #909399;
}
</style>
