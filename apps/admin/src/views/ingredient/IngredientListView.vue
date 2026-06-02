<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Ingredient {
  id?: number
  name: string
  categoryCode: string
  unit: string
  density: number
  imageUrl: string
  aliases: string
  tags: string
}

interface CategoryOption {
  code: string
  name: string
  color: string
}

const list = ref<Ingredient[]>([])
const categories = ref<CategoryOption[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const filterCategory = ref('')

const form = reactive<Ingredient>({
  name: '',
  categoryCode: '',
  unit: 'g',
  density: 1,
  imageUrl: '',
  aliases: '',
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
    const res = await fetch('/api/ingredient-categories')
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
    const url = filterCategory.value ? `/api/ingredients?category=${filterCategory.value}` : '/api/ingredients'
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

function handleEdit(row: Ingredient) {
  editingId.value = row.id ?? null
  Object.assign(form, {
    name: row.name,
    categoryCode: row.categoryCode,
    unit: row.unit,
    density: row.density,
    imageUrl: row.imageUrl,
    aliases: row.aliases,
    tags: row.tags
  })
  dialogVisible.value = true
}

async function handleDelete(row: Ingredient) {
  try {
    await ElMessageBox.confirm(`确认删除食材「${row.name}」？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (row.id) {
      await fetch(`/api/ingredients/${row.id}`, { method: 'DELETE' })
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
      await fetch(`/api/ingredients/${editingId.value}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const idx = list.value.findIndex(item => item.id === editingId.value)
      if (idx !== -1) {
        list.value[idx] = { ...list.value[idx], ...form }
      }
    } else {
      const res = await fetch('/api/ingredients', {
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
    unit: 'g',
    density: 1,
    imageUrl: '',
    aliases: '',
    tags: ''
  })
}

function parseTags(tagsStr: string): string[] {
  return tagsStr ? tagsStr.split(',').map(s => s.trim()).filter(Boolean) : []
}

onMounted(async () => {
  await fetchCategories()
  fetchData()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>食材列表</h2>
      <el-button type="primary" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增食材
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
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column label="分类" width="120" align="center">
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
      <el-table-column prop="unit" label="单位" width="80" align="center" />
      <el-table-column prop="density" label="密度(g/ml)" width="100" align="center" />
      <el-table-column label="别名" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">
          {{ Array.isArray(row.aliases) ? row.aliases.join(', ') : (row.aliases || '—') }}
        </template>
      </el-table-column>
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
      :title="editingId ? '编辑食材' : '新增食材'"
      width="560px"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入食材名称" />
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
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如 g、ml、个" style="width: 160px" />
        </el-form-item>
        <el-form-item label="密度">
          <el-input-number v-model="form.density" :min="0.01" :max="9999" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="请输入图片地址" />
        </el-form-item>
        <el-form-item label="别名">
          <el-input v-model="form.aliases" placeholder="多个别名用逗号分隔" />
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
</style>
