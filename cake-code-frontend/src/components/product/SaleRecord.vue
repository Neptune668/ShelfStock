<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { pageSale } from '@/api/product'
import { SALE_TYPE_TEXT, type SaleRecordVO, type SaleType } from '@/api/types'
import { buildOptions } from '@/utils/options'

const loading = ref(false)
const list = ref<SaleRecordVO[]>([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  saleType: '',
  dateRange: [] as string[],
})

const saleTypeOptions = buildOptions(SALE_TYPE_TEXT)

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const result = await pageSale({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      saleType: query.saleType || undefined,
      startDate: query.dateRange[0] || undefined,
      endDate: query.dateRange[1] || undefined,
    })
    list.value = result.data.list
    total.value = result.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  query.pageNum = 1
  loadList()
}

onMounted(loadList)
</script>

<template>
  <div>
    <el-form inline class="filter-bar" @submit.prevent>
      <el-form-item>
        <el-select v-model="query.saleType" placeholder="销售类型" clearable style="width: 150px">
          <el-option v-for="item in saleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="query.dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 260px"
        />
      </el-form-item>
      <el-form-item class="form-actions">
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="成品" />
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column label="销售类型" width="120">
        <template #default="{ row }">
          <el-tag :type="row.saleType === 'DISCOUNT_SALE' ? 'warning' : 'success'" effect="light">
            {{ SALE_TYPE_TEXT[row.saleType as SaleType] || row.saleType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="originalPrice" label="原价(元)" width="110" />
      <el-table-column prop="actualPrice" label="成交价(元)" width="110" />
      <el-table-column prop="createdAt" label="销售时间" />
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
  </div>
</template>


