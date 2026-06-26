<template>
  <div class="reply-chart">
    <v-chart :option="chartOption" autoresize style="height: 300px" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'
import type { ReplyStats } from '@/types'

use([CanvasRenderer, PieChart, TooltipComponent, LegendComponent])

const props = defineProps<{ data: ReplyStats }>()

const chartOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, left: 'center' },
  series: [
    {
      type: 'pie',
      radius: ['35%', '65%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: [
        { value: props.data.attendCount, name: '出席', itemStyle: { color: '#67c23a' } },
        { value: props.data.uncertainCount, name: '不确定', itemStyle: { color: '#e6a23c' } },
        { value: props.data.declineCount, name: '不出席', itemStyle: { color: '#f56c6c' } }
      ]
    }
  ]
}))
</script>
