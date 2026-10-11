<script setup lang="ts">
import { ref } from 'vue'
import { Goods, Sell, TakeawayBox } from '@element-plus/icons-vue'
import ProductList from '@/components/product/ProductList.vue'
import ProductBatchList from '@/components/product/ProductBatchList.vue'
import SaleRecord from '@/components/product/SaleRecord.vue'

// 显式指定默认标签，避免无 v-model 时没有激活项导致内容隐藏
const activeTab = ref('product')
</script>

<template>
  <el-card class="tabs-card" shadow="never">
    <el-tabs v-model="activeTab">
      <el-tab-pane name="product">
        <template #label>
          <span class="tab-label"><el-icon><Goods /></el-icon>成品</span>
        </template>
        <ProductList />
      </el-tab-pane>
      <el-tab-pane name="batch">
        <template #label>
          <span class="tab-label"><el-icon><TakeawayBox /></el-icon>货架批次</span>
        </template>
        <ProductBatchList />
      </el-tab-pane>
      <el-tab-pane name="sale">
        <template #label>
          <span class="tab-label"><el-icon><Sell /></el-icon>销售记录</span>
        </template>
        <SaleRecord />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped>
.tabs-card :deep(.el-tabs__header) {
  margin-bottom: 18px;
}

.tabs-card :deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 500;
}

.tabs-card :deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
</style>
