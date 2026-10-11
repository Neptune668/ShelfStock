<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import type { Component } from 'vue'
import type { EChartsOption } from 'echarts'
import { Box, Collection, Download, Goods, Money, Refresh, Sort, TakeawayBox, Tickets, Warning } from '@element-plus/icons-vue'
import { getExpiryDistribution, getOutboundReason, getOverview, getSaleTrend } from '@/api/dashboard'
import { pageOutbound } from '@/api/material'
import { pageSale } from '@/api/product'
import BaseChart from '@/components/dashboard/BaseChart.vue'
import { exportCsv } from '@/utils/export'
import { formatNumber } from '@/utils/format'
import {
  OUTBOUND_REASON_TEXT,
  SALE_TYPE_TEXT,
  type ExpiryDistributionVO,
  type OutboundReason,
  type OutboundReasonVO,
  type OutboundVO,
  type OverviewVO,
  type SaleRecordVO,
  type SaleTrendVO,
  type SaleType,
} from '@/api/types'

const AUTO_REFRESH_INTERVAL = 30 * 1000

// ==================== 数据状态 ====================
const loading = ref(false)
const errorMsg = ref('')

const overview = ref<OverviewVO | null>(null)
const trendList = ref<SaleTrendVO[]>([])
const reasonList = ref<OutboundReasonVO[]>([])
const expiryList = ref<ExpiryDistributionVO[]>([])

// 筛选
const days = ref(7)

// 自动刷新
const autoRefresh = ref(false)
let timer: number | null = null

async function loadDashboard(): Promise<void> {
  loading.value = true
  errorMsg.value = ''
  try {
    const [overviewRes, trendRes, reasonRes, expiryRes] = await Promise.all([
      getOverview(),
      getSaleTrend(days.value),
      getOutboundReason(),
      getExpiryDistribution(),
    ])
    overview.value = overviewRes.data
    trendList.value = trendRes.data.list
    reasonList.value = reasonRes.data.list
    expiryList.value = expiryRes.data.list
  } catch {
    errorMsg.value = '看板数据加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function startAutoRefresh(): void {
  stopAutoRefresh()
  timer = window.setInterval(loadDashboard, AUTO_REFRESH_INTERVAL)
}

function stopAutoRefresh(): void {
  if (timer != null) {
    window.clearInterval(timer)
    timer = null
  }
}

function toggleAutoRefresh(): void {
  if (autoRefresh.value) {
    startAutoRefresh()
    ElMessage.success('已开启自动刷新')
  } else {
    stopAutoRefresh()
  }
}

watch(days, () => loadDashboard())

// ==================== KPI 卡片 ====================
interface KpiItem {
  title: string
  value: number
  /** 数值与图标的主色 */
  color: string
  /** 图标底色 */
  tint: string
  icon: Component
  isDecimal?: boolean
}

const kpiItems = computed<KpiItem[]>(() => {
  const o = overview.value
  if (!o) return []
  return [
    {
      title: '原材料总库存',
      value: o.materialTotalQuantity,
      color: '#d97706',
      tint: '#fdf1e0',
      icon: Box,
      isDecimal: true,
    },
    { title: '原材料种类', value: o.materialTypeCount, color: '#2563eb', tint: '#e8f0fe', icon: Collection },
    { title: '在售成品数量', value: o.onSaleQuantity, color: '#16a34a', tint: '#e7f7ee', icon: Goods, isDecimal: true },
    { title: '成品种类', value: o.productTypeCount, color: '#7c3aed', tint: '#f1ebfe', icon: TakeawayBox },
    {
      title: '今日销售金额(元)',
      value: o.todaySaleAmount,
      color: '#e11d48',
      tint: '#fdeaee',
      icon: Money,
      isDecimal: true,
    },
    { title: '今日销售笔数', value: o.todaySaleCount, color: '#0891b2', tint: '#e4f6fa', icon: Tickets },
    {
      title: '今日入库/出库',
      value: o.todayInboundQuantity - o.todayOutboundQuantity,
      color: '#64748b',
      tint: '#eef1f5',
      icon: Sort,
      isDecimal: true,
    },
    {
      title: '临期/过期待处理',
      value: o.nearExpiryMaterialBatch + o.nearExpiryProduct + o.expiredPending,
      color: '#ea580c',
      tint: '#fdeee3',
      icon: Warning,
    },
  ]
})

// ==================== 图表 option ====================
const trendOption = computed<EChartsOption>(() => ({
  color: ['#d97706', '#2563eb'],
  tooltip: { trigger: 'axis' },
  legend: { data: ['销售笔数', '销售金额'], left: 'center', top: 0, icon: 'circle' },
  grid: { left: 10, right: 16, bottom: 6, top: 44, containLabel: true },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: trendList.value.map((item) => item.date),
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#e6ded4' } },
  },
  yAxis: [
    { type: 'value', name: '笔数', nameGap: 12, splitLine: { lineStyle: { color: '#f2ece4' } } },
    { type: 'value', name: '金额(元)', nameGap: 12, splitLine: { show: false } },
  ],
  series: [
    {
      name: '销售笔数',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 7,
      lineStyle: { width: 3 },
      areaStyle: { opacity: 0.12 },
      data: trendList.value.map((item) => item.saleCount),
    },
    {
      name: '销售金额',
      type: 'line',
      yAxisIndex: 1,
      smooth: true,
      symbol: 'circle',
      symbolSize: 7,
      lineStyle: { width: 3 },
      // 两条面积叠加会发灰，故金额线只保留线条
      data: trendList.value.map((item) => item.saleAmount),
    },
  ],
}))

const reasonOption = computed<EChartsOption>(() => ({
  color: ['#d97706', '#e11d48'],
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, icon: 'circle' },
  series: [
    {
      name: '出库数量',
      type: 'pie',
      radius: ['46%', '68%'],
      center: ['50%', '45%'],
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { formatter: '{b}\n{d}%' },
      data: reasonList.value.map((item) => ({ name: item.reasonName, value: item.quantity })),
    },
  ],
}))

const expiryOption = computed<EChartsOption>(() => ({
  color: ['#d97706'],
  tooltip: { trigger: 'axis' },
  grid: { left: 10, right: 16, bottom: 6, top: 24, containLabel: true },
  xAxis: {
    type: 'category',
    data: expiryList.value.map((item) => item.typeName),
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#e6ded4' } },
  },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f2ece4' } } },
  series: [
    {
      name: '批次数量',
      type: 'bar',
      barWidth: '46%',
      itemStyle: { borderRadius: [6, 6, 0, 0] },
      data: expiryList.value.map((item) => item.count),
    },
  ],
}))

// ==================== 明细表格 ====================
const activeTab = ref('sale')

const saleList = ref<SaleRecordVO[]>([])
const saleTotal = ref(0)
const saleQuery = reactive({ pageNum: 1, pageSize: 10, saleType: '', dateRange: [] as string[] })

const outboundList = ref<OutboundVO[]>([])
const outboundTotal = ref(0)
const outboundQuery = reactive({ pageNum: 1, pageSize: 10, reason: '', dateRange: [] as string[] })

async function loadSale(): Promise<void> {
  const result = await pageSale({
    pageNum: saleQuery.pageNum,
    pageSize: saleQuery.pageSize,
    saleType: saleQuery.saleType || undefined,
    startDate: saleQuery.dateRange[0] || undefined,
    endDate: saleQuery.dateRange[1] || undefined,
  })
  saleList.value = result.data.list
  saleTotal.value = result.data.total
}

async function loadOutbound(): Promise<void> {
  const result = await pageOutbound({
    pageNum: outboundQuery.pageNum,
    pageSize: outboundQuery.pageSize,
    reason: outboundQuery.reason || undefined,
    startDate: outboundQuery.dateRange[0] || undefined,
    endDate: outboundQuery.dateRange[1] || undefined,
  })
  outboundList.value = result.data.list
  outboundTotal.value = result.data.total
}

function handleExportSale(): void {
  exportCsv(
    '销售记录',
    ['ID', '成品', '数量', '销售类型', '原价(元)', '成交价(元)', '销售时间'],
    saleList.value.map((item) => [
      item.id,
      item.productName,
      item.quantity,
      SALE_TYPE_TEXT[item.saleType] || item.saleType,
      item.originalPrice,
      item.actualPrice,
      item.createdAt,
    ]),
  )
  ElMessage.success('导出成功')
}

function handleExportOutbound(): void {
  exportCsv(
    '出库流水',
    ['ID', '原材料', '数量', '出库原因', '操作人', '出库时间'],
    outboundList.value.map((item) => [
      item.id,
      item.materialName,
      item.quantity,
      OUTBOUND_REASON_TEXT[item.reason] || item.reason,
      item.operator,
      item.createdAt,
    ]),
  )
  ElMessage.success('导出成功')
}

watch(activeTab, (tab) => {
  if (tab === 'sale') loadSale()
  else loadOutbound()
})

onMounted(() => {
  loadDashboard()
  loadSale()
})

onBeforeUnmount(() => stopAutoRefresh())
</script>

<template>
  <div class="dashboard">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <span class="label">销售趋势范围</span>
        <el-radio-group v-model="days">
          <el-radio-button :value="7">近 7 天</el-radio-button>
          <el-radio-button :value="30">近 30 天</el-radio-button>
        </el-radio-group>
      </div>
      <div class="toolbar-right">
        <span class="label">自动刷新</span>
        <el-switch v-model="autoRefresh" @change="toggleAutoRefresh" />
        <el-button :icon="Refresh" :loading="loading" @click="loadDashboard">刷新</el-button>
      </div>
    </div>

    <!-- 异常提示 -->
    <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon :closable="false" />

    <!-- KPI 概览 -->
    <el-row v-loading="loading" :gutter="14" class="kpi-row">
      <el-col v-for="item in kpiItems" :key="item.title" :xs="12" :sm="8" :md="6">
        <el-card class="kpi-card" shadow="never">
          <div class="kpi-head">
            <span class="kpi-title">{{ item.title }}</span>
            <span class="kpi-icon" :style="{ backgroundColor: item.tint, color: item.color }">
              <el-icon><component :is="item.icon" /></el-icon>
            </span>
          </div>
          <div class="kpi-value" :style="{ color: item.color }">
            {{ item.isDecimal ? formatNumber(item.value) : item.value }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-card shadow="never" class="chart-card">
      <template #header>
        <div class="card-head">
          <span class="card-title">销售趋势</span>
          <span class="card-hint">近 {{ days }} 天</span>
        </div>
      </template>
      <BaseChart :option="trendOption" class="chart-large" />
    </el-card>

    <el-row :gutter="14">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-head">
              <span class="card-title">出库原因占比</span>
            </div>
          </template>
          <BaseChart :option="reasonOption" class="chart-medium" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-head">
              <span class="card-title">临期分布</span>
            </div>
          </template>
          <BaseChart :option="expiryOption" class="chart-medium" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 明细表格 -->
    <el-card shadow="never" class="table-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="销售记录" name="sale">
          <div class="table-toolbar">
            <el-select v-model="saleQuery.saleType" placeholder="销售类型" clearable style="width: 150px" @change="loadSale">
              <el-option v-for="(label, value) in SALE_TYPE_TEXT" :key="value" :label="label" :value="value" />
            </el-select>
            <el-date-picker
              v-model="saleQuery.dateRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 260px"
              @change="loadSale"
            />
            <el-button :icon="Download" @click="handleExportSale">导出</el-button>
          </div>
          <el-table :data="saleList" stripe>
            <el-table-column prop="id" label="ID" width="80" sortable />
            <el-table-column prop="productName" label="成品" sortable />
            <el-table-column prop="quantity" label="数量" width="100" sortable />
            <el-table-column label="销售类型" width="120">
              <template #default="{ row }">{{ SALE_TYPE_TEXT[row.saleType as SaleType] || row.saleType }}</template>
            </el-table-column>
            <el-table-column prop="originalPrice" label="原价(元)" width="110" sortable>
              <template #default="{ row }">{{ formatNumber(row.originalPrice) }}</template>
            </el-table-column>
            <el-table-column prop="actualPrice" label="成交价(元)" width="110" sortable>
              <template #default="{ row }">{{ formatNumber(row.actualPrice) }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="销售时间" sortable />
          </el-table>
          <el-pagination
            v-model:current-page="saleQuery.pageNum"
            v-model:page-size="saleQuery.pageSize"
            :total="saleTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, prev, pager, next, sizes"
            class="pagination"
            @current-change="loadSale"
            @size-change="loadSale"
          />
        </el-tab-pane>

        <el-tab-pane label="出库流水" name="outbound">
          <div class="table-toolbar">
            <el-select v-model="outboundQuery.reason" placeholder="出库原因" clearable style="width: 150px" @change="loadOutbound">
              <el-option v-for="(label, value) in OUTBOUND_REASON_TEXT" :key="value" :label="label" :value="value" />
            </el-select>
            <el-date-picker
              v-model="outboundQuery.dateRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 260px"
              @change="loadOutbound"
            />
            <el-button :icon="Download" @click="handleExportOutbound">导出</el-button>
          </div>
          <el-table :data="outboundList" stripe>
            <el-table-column prop="id" label="ID" width="80" sortable />
            <el-table-column prop="materialName" label="原材料" sortable />
            <el-table-column prop="quantity" label="数量" width="100" sortable />
            <el-table-column label="出库原因" width="130">
              <template #default="{ row }">{{ OUTBOUND_REASON_TEXT[row.reason as OutboundReason] || row.reason }}</template>
            </el-table-column>
            <el-table-column prop="operator" label="操作人" width="120" sortable />
            <el-table-column prop="createdAt" label="出库时间" sortable />
          </el-table>
          <el-pagination
            v-model:current-page="outboundQuery.pageNum"
            v-model:page-size="outboundQuery.pageSize"
            :total="outboundTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, prev, pager, next, sizes"
            class="pagination"
            @current-change="loadOutbound"
            @size-change="loadOutbound"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* ---------- 工具栏 ---------- */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  padding: 12px 18px;
  border-radius: var(--app-radius);
  background-color: #fff;
  border: 1px solid var(--app-border);
  box-shadow: var(--app-shadow);
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  color: var(--text-muted);
  font-size: 13px;
}

/* ---------- KPI ---------- */
.kpi-card {
  margin-bottom: 14px;
}

.kpi-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.kpi-title {
  color: var(--text-muted);
  font-size: 13px;
}

.kpi-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  flex: none;
  border-radius: 10px;
  font-size: 17px;
}

.kpi-value {
  margin-top: 10px;
  font-size: 26px;
  font-weight: 600;
  line-height: 1.1;
}

/* ---------- 图表卡片 ---------- */
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-strong);
}

.card-hint {
  font-size: 12px;
  color: var(--text-muted);
}

.chart-card {
  margin-bottom: 14px;
}

.chart-large {
  height: 320px;
}

.chart-medium {
  height: 280px;
}

/* ---------- 表格 ---------- */
.table-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
