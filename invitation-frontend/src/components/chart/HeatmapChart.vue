<template>
  <div class="heatmap-chart">
    <v-chart :option="chartOption" autoresize style="height: 300px" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { HeatmapChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, VisualMapComponent } from 'echarts/components'

use([CanvasRenderer, HeatmapChart, GridComponent, TooltipComponent, VisualMapComponent])

const props = defineProps<{ invitationId: number }>()

// Generate sample heatmap data (hours x days)
const hours = Array.from({ length: 24 }, (_, i) => `${i}:00`)
const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const data: [number, number, number][] = []
for (let i = 0; i < 7; i++) {
  for (let j = 0; j < 24; j++) {
    data.push([j, i, Math.floor(Math.random() * 50)])
  }
}

const chartOption = computed(() => ({
  tooltip: {
    position: 'top',
    formatter: (params: any) => `${days[params.value[1]]} ${hours[params.value[0]]}<br/>访问量: ${params.value[2]}`
  },
  grid: { left: 60, right: 40, top: 10, bottom: 60 },
  xAxis: { type: 'category', data: hours, splitArea: { show: true } },
  yAxis: { type: 'category', data: days, splitArea: { show: true } },
  visualMap: {
    min: 0,
    max: 50,
    calculable: true,
    orient: 'horizontal',
    left: 'center',
    bottom: 0,
    inRange: { color: ['#ebedf0', '#409eff', '#2563eb'] }
  },
  series: [{
    type: 'heatmap',
    data: data,
    label: { show: false },
    emphasis: {
      itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.5)' }
    }
  }]
}))
</script>
