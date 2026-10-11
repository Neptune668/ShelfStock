<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { pageOutbound } from '@/api/material'
import { OUTBOUND_REASON_TEXT, type OutboundVO } from '@/api/types'

const loading = ref(false)
const list = ref<OutboundVO[]>([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  reason: '',
  dateRange: [] as string[],
})

const reasonOptions = [
  { value: 'NORMAL_CONSUME', label: OUTBOUND_REASON_TEXT.NORMAL_CONSUME },
  { value: 'EXPIRED_DISCARD', label: OUTBOUND_REASON_TEXT.EXPIRED_DISCARD },
]

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const result = await pageOutbound({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      reason: query.reason || undefined,
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
        <el-select v-model="query.reason" placeholder="出库原因" clearable style="width: 160px">
          <el-option v-for="item in reasonOptions" :key="item.value" :label="item.label" :value="item.value" />
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
      <el-table-column prop="materialName" label="原材料" />
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column label="出库原因" width="150">
        <template #default="{ row }">
          <el-tag :type="row.reason === 'EXPIRED_DISCARD' ? 'danger' : 'info'" effect="light">
            {{ OUTBOUND_REASON_TEXT[row.reason as keyof typeof OUTBOUND_REASON_TEXT] || row.reason }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="createdAt" label="出库时间" />
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
