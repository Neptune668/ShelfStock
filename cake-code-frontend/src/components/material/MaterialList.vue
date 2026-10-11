<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Plus, Search } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { addMaterial, deleteMaterial, pageMaterial, updateMaterial } from '@/api/material'
import type { MaterialDTO, MaterialVO } from '@/api/types'

const loading = ref(false)
const list = ref<MaterialVO[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, name: '', category: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const form = reactive<MaterialDTO>({
  name: '',
  category: '',
  unit: '',
  shelfLifeDays: 1,
  storageCondition: '',
})

const rules: FormRules<MaterialDTO> = {
  name: [{ required: true, message: '请输入原材料名称', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入计量单位', trigger: 'blur' }],
  shelfLifeDays: [{ required: true, message: '请输入保质期天数', trigger: 'blur' }],
}

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const result = await pageMaterial({ ...query })
    list.value = result.data.list
    total.value = result.data.total
  } finally {
    loading.value = false
  }
}

function resetForm(): void {
  form.name = ''
  form.category = ''
  form.unit = ''
  form.shelfLifeDays = 1
  form.storageCondition = ''
  editingId.value = null
}

function openAdd(): void {
  resetForm()
  dialogTitle.value = '新增原材料'
  dialogVisible.value = true
}

function openEdit(row: MaterialVO): void {
  editingId.value = row.id
  dialogTitle.value = '编辑原材料'
  form.name = row.name
  form.category = row.category
  form.unit = row.unit
  form.shelfLifeDays = row.shelfLifeDays
  form.storageCondition = row.storageCondition
  dialogVisible.value = true
}

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (editingId.value == null) {
    await addMaterial(form)
    ElMessage.success('新增成功')
  } else {
    await updateMaterial(editingId.value, form)
    ElMessage.success('修改成功')
  }
  dialogVisible.value = false
  loadList()
}

async function handleDelete(row: MaterialVO): Promise<void> {
  await ElMessageBox.confirm(`确定删除原材料「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteMaterial(row.id)
  ElMessage.success('删除成功')
  loadList()
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
        <el-input v-model="query.name" placeholder="名称" clearable style="width: 180px" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="query.category" placeholder="分类" clearable style="width: 180px" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item class="form-actions">
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button type="success" :icon="Plus" @click="openAdd">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="shelfLifeDays" label="保质期(天)" width="110" />
      <el-table-column prop="storageCondition" label="存储条件" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Edit" @click="openEdit(row as MaterialVO)">编辑</el-button>
          <el-button link type="danger" :icon="Delete" @click="handleDelete(row as MaterialVO)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如 kg / g / 个" />
        </el-form-item>
        <el-form-item label="保质期天数" prop="shelfLifeDays">
          <el-input-number v-model="form.shelfLifeDays" :min="1" />
        </el-form-item>
        <el-form-item label="存储条件" prop="storageCondition">
          <el-input v-model="form.storageCondition" placeholder="如 常温 / 冷藏 / 冷冻" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>


