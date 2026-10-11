<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

const props = defineProps<{ option: EChartsOption }>()

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

function render(): void {
  if (!chartRef.value) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption(props.option)
}

function handleResize(): void {
  chart?.resize()
}

onMounted(() => {
  render()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  // 组件销毁时释放图表实例
  chart?.dispose()
  chart = null
})

watch(() => props.option, render, { deep: true })
</script>

<template>
  <div ref="chartRef" class="chart" />
</template>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>
