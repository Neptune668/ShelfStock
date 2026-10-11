<script setup lang="ts">
import MaterialList from '@/components/material/MaterialList.vue'
import MaterialBatchList from '@/components/material/MaterialBatchList.vue'
import OutboundRecord from '@/components/material/OutboundRecord.vue'
</script>

<template>
  <el-card>
    <el-tabs>
      <el-tab-pane label="原材料" name="material">
        <MaterialList />
      </el-tab-pane>
      <el-tab-pane label="批次库存" name="batch">
        <MaterialBatchList />
      </el-tab-pane>
      <el-tab-pane label="出库流水" name="outbound">
        <OutboundRecord />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>
