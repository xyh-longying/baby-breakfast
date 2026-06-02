<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Category {
  id?: number
  name: string
  code: string
  sortOrder: number
  icon: string
  color: string
  aliases: string
}

const list = ref<Category[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)

const form = reactive<Category>({
  name: '',
  code: '',
  sortOrder: 0,
  icon: '',
  color: '#FF9F43',
  aliases: ''
})

const defaultCategories = [
  { name: '早餐主食', code: 'breakfast', sortOrder: 1, icon: '🍳', color: '#F59E0B', aliases: '包子,馒头,粥' },
  { name: '粥品', code: 'porridge', sortOrder: 2, icon: '🥣', color: '#D4A574', aliases: '白粥,小米粥,八宝粥' },
  { name: '面点', code: 'noodle', sortOrder: 3, icon: '🍜', color: '#F97316', aliases: '面条,饺子,馄饨' },
  { name: '小食', code: 'snack', sortOrder: 4, icon: '🥟', color: '#EAB308', aliases: '点心,小吃' },
  { name: '汤羹', code: 'soup', sortOrder: 5, icon: '🍲', color: '#EF4444', aliases: '汤品,羹类' },
  { name: '饮品', code: 'drink', sortOrder: 6, icon: '🧋', color: '#8B5CF6', aliases: '果汁,茶饮,豆浆' },
  { name: '其他', code: 'other', sortOrder: 7, icon: '📦', color: '#6B7280', aliases: '' }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await fetch('/api/dish-categories')
    if (res.ok) {
      const json = await res.json()
      list.value = json?.data && Array.isArray(json.data) ? json.data : []
      if (list.value.length === 0) {
        list.value = [...defaultCategories]
      }
    } else {
      list.value = [...defaultCategories]
    }
  } catch {
    list.value = [...defaultCategories]
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Category) {
  editingId.value = row.id ?? null
  Object.assign(form, {
    name: row.name,
    code: row.code,
    sortOrder: row.sortOrder,
    icon: row.icon,
    color: row.color,
    aliases: row.aliases
  })
  dialogVisible.value = true
}

async function handleDelete(row: Category) {
  try {
    await ElMessageBox.confirm(`确认删除分类「${row.name}」？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (row.id) {
      await fetch(`/api/dish-categories/${row.id}`, { method: 'DELETE' })
    }
    list.value = list.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch {}
}

async function handleSubmit() {
  if (!form.name || !form.code) {
    ElMessage.warning('请填写名称和编码')
    return
  }
  try {
    if (editingId.value) {
      await fetch(`/api/dish-categories/${editingId.value}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const idx = list.value.findIndex(item => item.id === editingId.value)
      if (idx !== -1) {
        list.value[idx] = { ...list.value[idx], ...form }
      }
    } else {
      const res = await fetch('/api/dish-categories', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const data = await res.json()
      list.value.push({ ...form, id: data.id ?? Date.now() })
    }
    ElMessage.success(editingId.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
  } catch {
    ElMessage.error('操作失败')
  }
}

function resetForm() {
  editingId.value = null
  Object.assign(form, {
    name: '',
    code: '',
    sortOrder: 0,
    icon: '',
    color: '#FF9F43',
    aliases: ''
  })
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>菜品分类管理</h2>
      <el-button type="primary" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增分类
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
      <el-table-column label="图标" width="80" align="center">
        <template #default="{ row }">
          <span class="icon-cell">{{ row.icon || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="code" label="编码" width="120" />
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column label="颜色" width="100" align="center">
        <template #default="{ row }">
          <span class="color-block" :style="{ backgroundColor: row.color }"></span>
        </template>
      </el-table-column>
      <el-table-column prop="aliases" label="别名" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑分类' : '新增分类'"
      width="520px"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="编码" required>
          <el-input v-model="form.code" placeholder="请输入英文编码" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="输入emoji或图标名" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-color-picker v-model="form.color" />
        </el-form-item>
        <el-form-item label="别名">
          <el-input v-model="form.aliases" placeholder="多个别名用逗号分隔" />
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
.page-container {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.icon-cell {
  font-size: 22px;
}

.color-block {
  display: inline-block;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  vertical-align: middle;
}
</style>
