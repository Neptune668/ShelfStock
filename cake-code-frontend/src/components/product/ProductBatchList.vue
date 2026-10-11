<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { Download, Search, Sell, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { offShelf, onShelf, pageProduct, pageProductBatch, sale } from '@/api/product'
import { formatNumber } from '@/utils/format'
import {
  SALE_TYPE_TEXT,
  SHELF_STATUS_TAG,
  SHELF_STATUS_TEXT,
  type ProductBatchVO,
  type ProductVO,
  type SaleType,
  type ShelfStatus,
} from '@/api/types'

// ==================== 列表 ====================
const loading = ref(false)
const list = ref<ProductBatchVO[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, productId: undefined as number | undefined, status: '' })

const statusOptions = (Object.keys(SHELF_STATUS_TEXT) as ShelfStatus[]).map((key) => ({
  value: key,
  label: SHELF_STATUS_TEXT[key],
}))

const saleTypeOptions = (Object.keys(SALE_TYPE_TEXT) as SaleType[]).map((key) => ({
  value: key,
  label: SALE_TYPE_TEXT[key],
}))

const productOptions = ref<ProductVO[]>([])

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const result = await pageProductBatch({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      productId: query.productId,
      status: query.status || undefined,
    })
    list.value = result.data.list
    total.value = result.data.total
  } finally {
    loading.value = false
  }
}

async function loadProductOptions(): Promise<void> {
  const result = await pageProduct({ pageNum: 1, pageSize: 1000 })
  productOptions.value = result.data.list
}

function handleSearch(): void {
  query.pageNum = 1
  loadList()
}

// ==================== 上架 ====================
const shelfVisible = ref(false)
const shelfFormRef = ref<FormInstance>()
const shelfForm = reactive({
  productId: undefined as number | undefined,
  quantity: 1,
  productionDate: dayjs().format('YYYY-MM-DD'),
  nearExpiryDays: 1,
})
const shelfRules: FormRules = {
  productId: [{ required: true, message: '请选择成品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入上架数量', trigger: 'blur' }],
  productionDate: [{ required: true, message: '请选择生产日期', trigger: 'change' }],
  nearExpiryDays: [{ required: true, message: '请输入临期阈值', trigger: 'blur' }],
}

function openShelf(): void {
  shelfForm.productId = undefined
  shelfForm.quantity = 1
  shelfForm.productionDate = dayjs().format('YYYY-MM-DD')
  shelfForm.nearExpiryDays = 1
  shelfVisible.value = true
}

async function handleShelf(): Promise<void> {
  if (!shelfFormRef.value) return
  const valid = await shelfFormRef.value.validate().catch(() => false)
  if (!valid) return
  await onShelf({
    productId: shelfForm.productId as number,
    quantity: shelfForm.quantity,
    productionDate: shelfForm.productionDate,
    nearExpiryDays: shelfForm.nearExpiryDays,
  })
  ElMessage.success('上架成功')
  shelfVisible.value = false
  loadList()
}

// ==================== 销售 ====================
const saleVisible = ref(false)
const saleFormRef = ref<FormInstance>()
const saleBatchId = ref<number | null>(null)
const saleForm = reactive({
  quantity: 1,
  saleType: 'NORMAL_SALE' as SaleType,
  actualPrice: undefined as number | undefined,
})
const saleRules: FormRules = {
  quantity: [{ required: true, message: '请输入销售数量', trigger: 'blur' }],
  saleType: [{ required: true, message: '请选择销售类型', trigger: 'change' }],
}

function openSale(row: ProductBatchVO): void {
  saleBatchId.value = row.id
  saleForm.quantity = 1
  saleForm.saleType = 'NORMAL_SALE'
  saleForm.actualPrice = undefined
  saleVisible.value = true
}

async function handleSale(): Promise<void> {
  if (!saleFormRef.value) return
  const valid = await saleFormRef.value.validate().catch(() => false)
  if (!valid) return
  // 临期降价必须填写成交单价
  if (saleForm.saleType === 'DISCOUNT_SALE' && !saleForm.actualPrice) {
    ElMessage.warning('临期降价必须填写实际成交单价')
    return
  }
  await sale({
    batchId: saleBatchId.value as number,
    quantity: saleForm.quantity,
    saleType: saleForm.saleType,
    actualPrice: saleForm.actualPrice,
  })
  ElMessage.success('销售成功')
  saleVisible.value = false
  loadList()
}

// ==================== 下架 ====================
const offVisible = ref(false)
const offFormRef = ref<FormInstance>()
const offBatchId = ref<number | null>(null)
const offForm = reactive({ quantity: 1 })
const offRules: FormRules = {
  quantity: [{ required: true, message: '请输入下架数量', trigger: 'blur' }],
}

function openOff(row: ProductBatchVO): void {
  offBatchId.value = row.id
  offForm.quantity = 1
  offVisible.value = true
}

async function handleOff(): Promise<void> {
  if (!offFormRef.value) return
  const valid = await offFormRef.value.validate().catch(() => false)
  if (!valid) return
  await offShelf({
    batchId: offBatchId.value as number,
    quantity: offForm.quantity,
  })
  ElMessage.success('下架成功')
  offVisible.value = false
  loadList()
}

onMounted(() => {
  loadList()
  loadProductOptions()
})
</script>

<template>
  <div>
    <el-form inline class="filter-bar" @submit.prevent>
      <el-form-item>
        <el-select v-model="query.productId" placeholder="成品" clearable style="width: 160px">
          <el-option v-for="item in productOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item class="form-actions">
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button type="success" :icon="Upload" @click="openShelf">上架</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column prop="id" label="批次" width="80" />
      <el-table-column prop="productName" label="成品" />
      <el-table-column prop="price" label="售价(元)" width="100">
        <template #default="{ row }">{{ formatNumber(row.price) }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="剩余数量" width="100" />
      <el-table-column prop="productionDate" label="生产日期" width="120" />
      <el-table-column prop="expiryDate" label="过期日期" width="120" />
      <el-table-column prop="remainingDays" label="剩余天数" width="100" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="SHELF_STATUS_TAG[row.status as ShelfStatus]" effect="light">
            {{ SHELF_STATUS_TEXT[row.status as ShelfStatus] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Sell" @click="openSale(row as ProductBatchVO)">销售</el-button>
          <el-button link type="warning" :icon="Download" @click="openOff(row as ProductBatchVO)">下架</el-button>
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

    <!-- 上架弹窗 -->
    <el-dialog v-model="shelfVisible" title="成品上架" width="480px">
      <el-form ref="shelfFormRef" :model="shelfForm" :rules="shelfRules" label-width="90px">
        <el-form-item label="成品" prop="productId">
          <el-select v-model="shelfForm.productId" placeholder="请选择成品" style="width: 100%">
            <el-option v-for="item in productOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上架数量" prop="quantity">
          <el-input-number v-model="shelfForm.quantity" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生产日期" prop="productionDate">
          <el-date-picker v-model="shelfForm.productionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="临期阈值" prop="nearExpiryDays">
          <el-input-number v-model="shelfForm.nearExpiryDays" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shelfVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShelf">确定</el-button>
      </template>
    </el-dialog>

    <!-- 销售弹窗 -->
    <el-dialog v-model="saleVisible" title="成品销售" width="420px">
      <el-form ref="saleFormRef" :model="saleForm" :rules="saleRules" label-width="90px">
        <el-form-item label="销售数量" prop="quantity">
          <el-input-number v-model="saleForm.quantity" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="销售类型" prop="saleType">
          <el-select v-model="saleForm.saleType" style="width: 100%">
            <el-option v-for="item in saleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="saleForm.saleType === 'DISCOUNT_SALE'" label="成交单价" prop="actualPrice">
          <el-input-number v-model="saleForm.actualPrice" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saleVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSale">确定</el-button>
      </template>
    </el-dialog>

    <!-- 下架弹窗 -->
    <el-dialog v-model="offVisible" title="成品下架" width="420px">
      <el-form ref="offFormRef" :model="offForm" :rules="offRules" label-width="90px">
        <el-form-item label="下架数量" prop="quantity">
          <el-input-number v-model="offForm.quantity" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="offVisible = false">取消</el-button>
        <el-button type="primary" @click="handleOff">确定</el-button>
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
