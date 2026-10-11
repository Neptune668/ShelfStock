<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { Download, Search, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { inbound, outbound, pageMaterial, pageMaterialBatch } from '@/api/material'
import {
  MATERIAL_STATUS_TAG,
  MATERIAL_STATUS_TEXT,
  OUTBOUND_REASON_TEXT,
  type MaterialBatchVO,
  type MaterialStatus,
  type MaterialVO,
  type OutboundReason,
} from '@/api/types'

const loading = ref(false)
const list = ref<MaterialBatchVO[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', status: '' })

const statusOptions = (Object.keys(MATERIAL_STATUS_TEXT) as MaterialStatus[]).map((key) => ({
  value: key,
  label: MATERIAL_STATUS_TEXT[key],
}))

const reasonOptions = (Object.keys(OUTBOUND_REASON_TEXT) as OutboundReason[]).map((key) => ({
  value: key,
  label: OUTBOUND_REASON_TEXT[key],
}))

// 原材料下拉选项
const materialOptions = ref<MaterialVO[]>([])

// 入库弹窗
const inboundVisible = ref(false)
const inboundFormRef = ref<FormInstance>()
const inboundForm = reactive({
  materialId: undefined as number | undefined,
  quantity: 1,
  productionDate: dayjs().format('YYYY-MM-DD'),
  nearExpiryDays: 1,
  supplier: '',
})
const inboundRules: FormRules = {
  materialId: [{ required: true, message: '请选择原材料', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  productionDate: [{ required: true, message: '请选择生产日期', trigger: 'change' }],
  nearExpiryDays: [{ required: true, message: '请输入临期阈值', trigger: 'blur' }],
}

// 出库弹窗
const outboundVisible = ref(false)
const outboundFormRef = ref<FormInstance>()
const outboundBatchId = ref<number | null>(null)
const outboundForm = reactive({
  quantity: 1,
  reason: 'NORMAL_CONSUME' as OutboundReason,
})
const outboundRules: FormRules = {
  quantity: [{ required: true, message: '请输入出库数量', trigger: 'blur' }],
  reason: [{ required: true, message: '请选择出库原因', trigger: 'change' }],
}

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const result = await pageMaterialBatch({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      keyword: query.keyword || undefined,
      status: query.status || undefined,
    })
    list.value = result.data.list
    total.value = result.data.total
  } finally {
    loading.value = false
  }
}

async function loadMaterialOptions(): Promise<void> {
  const result = await pageMaterial({ pageNum: 1, pageSize: 1000 })
  materialOptions.value = result.data.list
}

function handleSearch(): void {
  query.pageNum = 1
  loadList()
}

function openInbound(): void {
  inboundForm.materialId = undefined
  inboundForm.quantity = 1
  inboundForm.productionDate = dayjs().format('YYYY-MM-DD')
  inboundForm.nearExpiryDays = 1
  inboundForm.supplier = ''
  inboundVisible.value = true
}

async function handleInbound(): Promise<void> {
  if (!inboundFormRef.value) return
  const valid = await inboundFormRef.value.validate().catch(() => false)
  if (!valid) return
  await inbound({
    materialId: inboundForm.materialId as number,
    quantity: inboundForm.quantity,
    productionDate: inboundForm.productionDate,
    nearExpiryDays: inboundForm.nearExpiryDays,
    supplier: inboundForm.supplier || undefined,
  })
  ElMessage.success('入库成功')
  inboundVisible.value = false
  loadList()
}

function openOutbound(row: MaterialBatchVO): void {
  outboundBatchId.value = row.id
  outboundForm.quantity = 1
  outboundForm.reason = 'NORMAL_CONSUME'
  outboundVisible.value = true
}

async function handleOutbound(): Promise<void> {
  if (!outboundFormRef.value) return
  const valid = await outboundFormRef.value.validate().catch(() => false)
  if (!valid) return
  await outbound({
    batchId: outboundBatchId.value as number,
    quantity: outboundForm.quantity,
    reason: outboundForm.reason,
  })
  ElMessage.success('出库成功')
  outboundVisible.value = false
  loadList()
}

onMounted(() => {
  loadList()
  loadMaterialOptions()
})
</script>

<template>
  <div>
    <el-form inline class="filter-bar" @submit.prevent>
      <el-form-item>
        <el-input v-model="query.keyword" placeholder="原材料名称" clearable style="width: 180px" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item class="form-actions">
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button type="success" :icon="Upload" @click="openInbound">入库</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column prop="id" label="批次" width="80" />
      <el-table-column prop="materialName" label="原材料" />
      <el-table-column prop="quantity" label="剩余数量" width="100" />
      <el-table-column prop="productionDate" label="生产日期" width="120" />
      <el-table-column prop="expiryDate" label="过期日期" width="120" />
      <el-table-column prop="remainingDays" label="剩余天数" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="MATERIAL_STATUS_TAG[row.status as MaterialStatus]" effect="light">
            {{ MATERIAL_STATUS_TEXT[row.status as MaterialStatus] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Download" @click="openOutbound(row as MaterialBatchVO)">出库</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, prev, pager, next, sizes"
      class="pagination"
      @current-change="loadList"
      @size-change="handleSearch"
    />

    <!-- 入库弹窗 -->
    <el-dialog v-model="inboundVisible" title="原材料入库" width="480px">
      <el-form ref="inboundFormRef" :model="inboundForm" :rules="inboundRules" label-width="90px">
        <el-form-item label="原材料" prop="materialId">
          <el-select v-model="inboundForm.materialId" placeholder="请选择原材料" style="width: 100%">
            <el-option v-for="item in materialOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库数量" prop="quantity">
          <el-input-number v-model="inboundForm.quantity" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生产日期" prop="productionDate">
          <el-date-picker v-model="inboundForm.productionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="临期阈值" prop="nearExpiryDays">
          <el-input-number v-model="inboundForm.nearExpiryDays" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="inboundForm.supplier" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inboundVisible = false">取消</el-button>
        <el-button type="primary" @click="handleInbound">确定</el-button>
      </template>
    </el-dialog>

    <!-- 出库弹窗 -->
    <el-dialog v-model="outboundVisible" title="原材料出库" width="420px">
      <el-form ref="outboundFormRef" :model="outboundForm" :rules="outboundRules" label-width="90px">
        <el-form-item label="出库数量" prop="quantity">
          <el-input-number v-model="outboundForm.quantity" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="出库原因" prop="reason">
          <el-select v-model="outboundForm.reason" style="width: 100%">
            <el-option v-for="item in reasonOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="outboundVisible = false">取消</el-button>
        <el-button type="primary" @click="handleOutbound">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 16px;
  border-radius: 12px;
  background-color: var(--app-panel-bg);
  border: 1px solid var(--app-border);
}

.filter-bar :deep(.el-form-item) {
  margin: 0;
}

.filter-bar :deep(.form-actions) {
  margin-left: auto;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
