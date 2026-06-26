<template>
  <div class="map-navigation">
    <div class="map-btn" @click="openMap">
      <el-icon><Location /></el-icon>
      <span>导航到活动地点</span>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  latitude: number
  longitude: number
  address: string
}>()

function openMap() {
  // Try WeChat mini-program map first, then fall back to web map
  const url = `https://uri.amap.com/navigation?to=${props.longitude},${props.latitude},${encodeURIComponent(props.address)}&mode=bus`
  window.open(url, '_blank')
}
</script>

<style scoped lang="scss">
.map-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: rgba(64, 158, 255, 0.2);
  border: 1px solid rgba(64, 158, 255, 0.4);
  border-radius: 10px;
  color: #409eff;
  cursor: pointer;
  font-size: 14px;
  margin-top: 12px;
  transition: background 0.2s;

  &:hover {
    background: rgba(64, 158, 255, 0.3);
  }
}
</style>
