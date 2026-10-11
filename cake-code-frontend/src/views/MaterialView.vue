<script setup lang="ts">
import { ref } from 'vue'
import { Box, Collection, Upload } from '@element-plus/icons-vue'
import MaterialList from '@/components/material/MaterialList.vue'
import MaterialBatchList from '@/components/material/MaterialBatchList.vue'
import OutboundRecord from '@/components/material/OutboundRecord.vue'

// 显式指定默认标签，避免无 v-model 时没有激活项导致内容隐藏
const activeTab = ref('material')
</script>

<template>
  <el-card class="tabs-card" shadow="never">
    <el-tabs v-model="activeTab">
      <el-tab-pane name="material">
        <template #label>
          <span class="tab-label"><el-icon><Box /></el-icon>原材料</span>
        </template>
        <MaterialList />
      </el-tab-pane>
      <el-tab-pane name="batch">
        <template #label>
          <span class="tab-label"><el-icon><Collection /></el-icon>批次库存</span>
        </template>
        <MaterialBatchList />
      </el-tab-pane>
      <el-tab-pane name="outbound">
        <template #label>
          <span class="tab-label"><el-icon><Upload /></el-icon>出库流水</span>
        </template>
        <OutboundRecord />
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
