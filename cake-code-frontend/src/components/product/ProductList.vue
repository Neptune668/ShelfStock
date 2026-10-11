<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Plus, Search } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { addProduct, deleteProduct, pageProduct, updateProduct } from '@/api/product'
import { formatNumber } from '@/utils/format'
import type { ProductDTO, ProductVO } from '@/api/types'

const loading = ref(false)
const list = ref<ProductVO[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, name: '', category: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const form = reactive<ProductDTO>({
  name: '',
  category: '',
  price: 1,
  shelfLifeDays: 1,
})

const rules: FormRules<ProductDTO> = {
  name: [{ required: true, message: '请输入成品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  shelfLifeDays: [{ required: true, message: '请输入保质期天数', trigger: 'blur' }],
}

async function loadList(): Promise<void> {
  loading.value = true
  try {
    const result = await pageProduct({ ...query })
    list.value = result.data.list
    total.value = result.data.total
  } finally {
    loading.value = false
  }
}

function resetForm(): void {
  form.name = ''
  form.category = ''
  form.price = 1
  form.shelfLifeDays = 1
  editingId.value = null
}

function openAdd(): void {
  resetForm()
  dialogTitle.value = '新增成品'
  dialogVisible.value = true
}

function openEdit(row: ProductVO): void {
  editingId.value = row.id
  dialogTitle.value = '编辑成品'
  form.name = row.name
  form.category = row.category
  form.price = row.price
  form.shelfLifeDays = row.shelfLifeDays
  dialogVisible.value = true
}

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (editingId.value == null) {
    await addProduct(form)
    ElMessage.success('新增成功')
  } else {
    await updateProduct(editingId.value, form)
    ElMessage.success('修改成功')
  }
  dialogVisible.value = false
  loadList()
}

async function handleDelete(row: ProductVO): Promise<void> {
  await ElMessageBox.confirm(`确定删除成品「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteProduct(row.id)
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
      <el-table-column prop="price" label="售价(元)" width="110">
        <template #default="{ row }">
          <span class="price">{{ formatNumber(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="shelfLifeDays" label="保质期(天)" width="110" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Edit" @click="openEdit(row as ProductVO)">编辑</el-button>
          <el-button link type="danger" :icon="Delete" @click="handleDelete(row as ProductVO)">删除</el-button>
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
        <el-form-item label="售价(元)" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="保质期天数" prop="shelfLifeDays">
          <el-input-number v-model="form.shelfLifeDays" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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

.price {
  font-weight: 600;
  color: var(--el-color-primary);
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
