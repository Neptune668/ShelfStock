<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import type { EChartsOption } from 'echarts'
import { ElMessage } from 'element-plus'
import { getExpiryDistribution, getOutboundReason, getOverview, getSaleTrend } from '@/api/dashboard'
import { pageOutbound } from '@/api/material'
import { pageSale } from '@/api/product'
import BaseChart from '@/components/dashboard/BaseChart.vue'
import { exportCsv } from '@/utils/export'
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
  color: string
}

const kpiItems = computed<KpiItem[]>(() => {
  const o = overview.value
  if (!o) return []
  return [
    { title: '原材料总库存', value: o.materialTotalQuantity, color: '#409eff' },
    { title: '原材料种类', value: o.materialTypeCount, color: '#409eff' },
    { title: '在售成品数量', value: o.onSaleQuantity, color: '#67c23a' },
    { title: '成品种类', value: o.productTypeCount, color: '#67c23a' },
    { title: '今日销售金额(元)', value: o.todaySaleAmount, color: '#e6a23c' },
    { title: '今日销售笔数', value: o.todaySaleCount, color: '#e6a23c' },
    { title: '今日入库/出库', value: o.todayInboundQuantity - o.todayOutboundQuantity, color: '#909399' },
    { title: '临期/过期待处理', value: o.nearExpiryMaterialBatch + o.nearExpiryProduct + o.expiredPending, color: '#f56c6c' },
  ]
})

// ==================== 图表 option ====================
const trendOption = computed<EChartsOption>(() => ({
  title: { text: '销售趋势', left: 'center' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['销售笔数', '销售金额'] },
  grid: { left: 60, right: 60, bottom: 30, top: 60 },
  xAxis: { type: 'category', data: trendList.value.map((item) => item.date) },
  yAxis: [
    { type: 'value', name: '笔数' },
    { type: 'value', name: '金额(元)' },
  ],
  series: [
    { name: '销售笔数', type: 'line', data: trendList.value.map((item) => item.saleCount) },
    { name: '销售金额', type: 'line', yAxisIndex: 1, data: trendList.value.map((item) => item.saleAmount) },
  ],
}))

const reasonOption = computed<EChartsOption>(() => ({
  title: { text: '出库原因占比', left: 'center' },
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [
    {
      name: '出库数量',
      type: 'pie',
      radius: '55%',
      data: reasonList.value.map((item) => ({ name: item.reasonName, value: item.quantity })),
    },
  ],
}))

const expiryOption = computed<EChartsOption>(() => ({
  title: { text: '临期分布', left: 'center' },
  tooltip: {},
  grid: { left: 60, right: 20, bottom: 30, top: 60 },
  xAxis: { type: 'category', data: expiryList.value.map((item) => item.typeName) },
  yAxis: { type: 'value' },
  series: [{ name: '批次数量', type: 'bar', data: expiryList.value.map((item) => item.count) }],
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
        <el-button :loading="loading" @click="loadDashboard">刷新</el-button>
      </div>
    </div>

    <!-- 异常提示 -->
    <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon class="error-alert" />

    <!-- KPI 概览 -->
    <el-row v-loading="loading" :gutter="12">
      <el-col v-for="item in kpiItems" :key="item.title" :xs="12" :sm="8" :md="6">
        <el-card class="kpi-card" shadow="hover">
          <div class="kpi-title">{{ item.title }}</div>
          <div class="kpi-value" :style="{ color: item.color }">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="12" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <BaseChart :option="trendOption" class="chart-large" />
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="12" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <BaseChart :option="reasonOption" class="chart-medium" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
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
            <el-button @click="handleExportSale">导出</el-button>
          </div>
          <el-table :data="saleList" border>
            <el-table-column prop="id" label="ID" width="80" sortable />
            <el-table-column prop="productName" label="成品" sortable />
            <el-table-column prop="quantity" label="数量" width="100" sortable />
            <el-table-column label="销售类型" width="120">
              <template #default="{ row }">{{ SALE_TYPE_TEXT[row.saleType as SaleType] || row.saleType }}</template>
            </el-table-column>
            <el-table-column prop="originalPrice" label="原价(元)" width="110" sortable />
            <el-table-column prop="actualPrice" label="成交价(元)" width="110" sortable />
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
            <el-button @click="handleExportOutbound">导出</el-button>
          </div>
          <el-table :data="outboundList" border>
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
  gap: 12px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  color: #606266;
  font-size: 14px;
}

.error-alert {
  margin-bottom: 4px;
}

.kpi-card {
  margin-bottom: 12px;
  text-align: center;
}

.kpi-title {
  color: #909399;
  font-size: 13px;
}

.kpi-value {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 600;
}

.chart-row {
  margin-bottom: 12px;
}

.chart-card {
  margin-bottom: 12px;
}

.chart-large {
  height: 320px;
}

.chart-medium {
  height: 280px;
}

.table-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
