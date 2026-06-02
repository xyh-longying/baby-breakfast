<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Dish {
  id?: number
  name: string
  categoryCode: string
  description: string
  difficulty: number
  prepTimeMin: number
  cookTimeMin: number
  imageUrl: string
  tags: string
}

interface CategoryOption {
  code: string
  name: string
  color: string
}

const list = ref<Dish[]>([])
const categories = ref<CategoryOption[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const filterCategory = ref('')

const form = reactive<Dish>({
  name: '',
  categoryCode: '',
  description: '',
  difficulty: 1,
  prepTimeMin: 0,
  cookTimeMin: 0,
  imageUrl: '',
  tags: ''
})

const categoryMap = computed(() => {
  const map: Record<string, CategoryOption> = {}
  categories.value.forEach(c => { map[c.code] = c })
  return map
})

const filteredList = computed(() => {
  if (!filterCategory.value) return list.value
  return list.value.filter(item => item.categoryCode === filterCategory.value)
})

async function fetchCategories() {
  try {
    const res = await fetch('/api/dish-categories')
    if (res.ok) {
      const json = await res.json()
      categories.value = json?.data && Array.isArray(json.data) ? json.data.map((c: { code: string; name: string; color: string }) => ({
        code: c.code,
        name: c.name,
        color: c.color || '#FF9F43'
      })) : []
    }
  } catch {}
}

async function fetchData() {
  loading.value = true
  try {
    const url = filterCategory.value ? `/api/dishes?category=${filterCategory.value}` : '/api/dishes'
    const res = await fetch(url)
    if (res.ok) {
      const json = await res.json()
      list.value = json?.data && Array.isArray(json.data) ? json.data : []
    }
  } catch {} finally {
    loading.value = false
  }
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Dish) {
  editingId.value = row.id ?? null
  Object.assign(form, {
    name: row.name,
    categoryCode: row.categoryCode,
    description: row.description,
    difficulty: row.difficulty,
    prepTimeMin: row.prepTimeMin,
    cookTimeMin: row.cookTimeMin,
    imageUrl: row.imageUrl,
    tags: row.tags
  })
  dialogVisible.value = true
}

async function handleDelete(row: Dish) {
  try {
    await ElMessageBox.confirm(`确认删除菜品「${row.name}」？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (row.id) {
      await fetch(`/api/dishes/${row.id}`, { method: 'DELETE' })
    }
    list.value = list.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch {}
}

async function handleSubmit() {
  if (!form.name || !form.categoryCode) {
    ElMessage.warning('请填写名称和选择分类')
    return
  }
  try {
    if (editingId.value) {
      await fetch(`/api/dishes/${editingId.value}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const idx = list.value.findIndex(item => item.id === editingId.value)
      if (idx !== -1) {
        list.value[idx] = { ...list.value[idx], ...form }
      }
    } else {
      const res = await fetch('/api/dishes', {
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
    categoryCode: '',
    description: '',
    difficulty: 1,
    prepTimeMin: 0,
    cookTimeMin: 0,
    imageUrl: '',
    tags: ''
  })
}

function parseTags(tagsStr: string): string[] {
  return tagsStr ? tagsStr.split(',').map(s => s.trim()).filter(Boolean) : []
}

function renderStars(difficulty: number): string {
  return '★'.repeat(difficulty) + '☆'.repeat(3 - difficulty)
}

onMounted(async () => {
  await fetchCategories()
  fetchData()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>菜品列表</h2>
      <el-button type="primary" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增菜品
      </el-button>
    </div>

    <div class="filter-bar">
      <el-select v-model="filterCategory" placeholder="按分类筛选" clearable style="width: 200px" @change="fetchData">
        <el-option
          v-for="cat in categories"
          :key="cat.code"
          :label="cat.name"
          :value="cat.code"
        />
      </el-select>
    </div>

    <el-table :data="filteredList" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="name" label="名称" min-width="130" />
      <el-table-column label="分类" width="110" align="center">
        <template #default="{ row }">
          <el-tag
            v-if="categoryMap[row.categoryCode]"
            :color="categoryMap[row.categoryCode].color"
            style="color: #fff; border: none"
          >
            {{ categoryMap[row.categoryCode].name }}
          </el-tag>
          <span v-else>{{ row.categoryCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="难度" width="120" align="center">
        <template #default="{ row }">
          <span class="stars">{{ renderStars(row.difficulty) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="prepTimeMin" label="准备时间(分)" width="120" align="center" />
      <el-table-column prop="cookTimeMin" label="烹饪时间(分)" width="120" align="center" />
      <el-table-column label="标签" min-width="180">
        <template #default="{ row }">
          <template v-if="Array.isArray(row.tags)">
            <el-tag
              v-for="tag in row.tags"
              :key="tag"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px"
            >
              {{ tag }}
            </el-tag>
          </template>
          <template v-else-if="row.tags">
            <el-tag
              v-for="tag in parseTags(row.tags)"
              :key="tag"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px"
            >
              {{ tag }}
            </el-tag>
          </template>
          <span v-if="!row.tags || (!Array.isArray(row.tags) && !row.tags.length)">—</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑菜品' : '新增菜品'"
      width="580px"
      destroy-on-close
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.categoryCode" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.code"
              :label="cat.name"
              :value="cat.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入菜品描述" />
        </el-form-item>
        <el-form-item label="难度">
          <el-rate v-model="form.difficulty" :max="3" show-text :texts="['简单', '中等', '困难']" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="准备时间(分)">
            <el-input-number v-model="form.prepTimeMin" :min="0" :max="999" />
          </el-form-item>
          <el-form-item label="烹饪时间(分)">
            <el-input-number v-model="form.cookTimeMin" :min="0" :max="999" />
          </el-form-item>
        </div>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="请输入图片地址" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
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

.filter-bar {
  margin-bottom: 16px;
}

.stars {
  color: #FF9F43;
  font-size: 18px;
  letter-spacing: 2px;
}

.form-row {
  display: flex;
  gap: 20px;
}
</style>
