<template>
  <div class="pvuv-chart">
    <v-chart :option="chartOption" autoresize style="height: 300px" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import type { TrendItem } from '@/types'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent])

const props = defineProps<{ data: TrendItem[] }>()

const chartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['PV', 'UV'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: props.data.map(d => d.date)
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: 'PV',
      type: 'line',
      smooth: true,
      data: props.data.map(d => d.pv),
      areaStyle: { opacity: 0.1 },
      itemStyle: { color: '#409eff' }
    },
    {
      name: 'UV',
      type: 'line',
      smooth: true,
      data: props.data.map(d => d.uv),
      areaStyle: { opacity: 0.1 },
      itemStyle: { color: '#67c23a' }
    }
  ]
}))
</script>
