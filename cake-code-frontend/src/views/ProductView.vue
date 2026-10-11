<script setup lang="ts">
import ProductList from '@/components/product/ProductList.vue'
import ProductBatchList from '@/components/product/ProductBatchList.vue'
import SaleRecord from '@/components/product/SaleRecord.vue'
</script>

<template>
  <el-card>
    <el-tabs>
      <el-tab-pane label="成品" name="product">
        <ProductList />
      </el-tab-pane>
      <el-tab-pane label="货架批次" name="batch">
        <ProductBatchList />
      </el-tab-pane>
      <el-tab-pane label="销售记录" name="sale">
        <SaleRecord />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>
