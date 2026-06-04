<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface RecipeIngredient {
  name: string
  amount: string
  unit: string
}

interface Recipe {
  id?: number
  name: string
  dishId: number | null
  servings: number
  steps: string
  ingredients: RecipeIngredient[]
  tips: string
  imageUrl: string
}

interface DishOption {
  id: number
  name: string
}

const list = ref<Recipe[]>([])
const dishes = ref<DishOption[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)

const form = reactive<Recipe>({
  name: '',
  dishId: null,
  servings: 1,
  steps: '',
  ingredients: [{ name: '', amount: '', unit: 'g' }],
  tips: '',
  imageUrl: ''
})

const dishMap = computed(() => {
  const map: Record<number, DishOption> = {}
  dishes.value.forEach(d => { map[d.id] = d })
  return map
})

function parseStepsCount(steps: string | string[]): number {
  if (Array.isArray(steps)) return steps.filter(s => s && String(s).trim()).length
  if (!steps || !String(steps).trim()) return 0
  return String(steps).split('\n').filter(s => s.trim()).length
}

function parseIngredients(ingredientsStr: string | RecipeIngredient[]): RecipeIngredient[] {
  if (Array.isArray(ingredientsStr)) return ingredientsStr
  try {
    const parsed = JSON.parse(ingredientsStr)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

async function fetchDishes() {
  try {
    const res = await fetch('/api/dishes')
    if (res.ok) {
      const json = await res.json()
      dishes.value = json?.data && Array.isArray(json.data) ? json.data.map((d: { id: number; name: string }) => ({
        id: d.id ?? 0,
        name: d.name
      })) : []
    }
  } catch {}
}

async function fetchData() {
  loading.value = true
  try {
    const res = await fetch('/api/recipes')
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

function handleEdit(row: Recipe) {
  editingId.value = row.id ?? null
  Object.assign(form, {
    name: row.name,
    dishId: row.dishId,
    servings: row.servings,
    steps: Array.isArray(row.steps) ? row.steps.join('\n') : (row.steps || ''),
    ingredients: Array.isArray(row.ingredients) && row.ingredients.length > 0
      ? [...row.ingredients]
      : [{ name: '', amount: '', unit: 'g' }],
    tips: row.tips || '',
    imageUrl: row.imageUrl || ''
  })
  dialogVisible.value = true
}

async function handleDelete(row: Recipe) {
  try {
    await ElMessageBox.confirm(`确认删除菜谱「${row.name}」？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (row.id) {
      await fetch(`/api/recipes/${row.id}`, { method: 'DELETE' })
    }
    list.value = list.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch {}
}

async function handleSubmit() {
  if (!form.name) {
    ElMessage.warning('请填写菜谱名称')
    return
  }
  try {
    const payload = {
      ...form,
      steps: typeof form.steps === 'string' ? form.steps.split('\n').filter(s => s.trim()) : form.steps,
      ingredients: form.ingredients.filter(ing => ing.name && ing.amount)
    }
    if (editingId.value) {
      await fetch(`/api/recipes/${editingId.value}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      const idx = list.value.findIndex(item => item.id === editingId.value)
      if (idx !== -1) {
        list.value[idx] = { ...list.value[idx], ...payload }
      }
    } else {
      const res = await fetch('/api/recipes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
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
    dishId: null,
    servings: 1,
    steps: '',
    ingredients: [{ name: '', amount: '', unit: 'g' }],
    tips: '',
    imageUrl: ''
  })
}

function addIngredientRow() {
  form.ingredients.push({ name: '', amount: '', unit: 'g' })
}

function removeIngredientRow(index: number) {
  if (form.ingredients.length > 1) {
    form.ingredients.splice(index, 1)
  }
}

onMounted(async () => {
  await fetchDishes()
  fetchData()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>菜谱管理</h2>
      <el-button type="primary" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增菜谱
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column label="关联菜品" width="140">
        <template #default="{ row }">
          {{ row.dishId && dishMap[row.dishId] ? dishMap[row.dishId].name : '—' }}
        </template>
      </el-table-column>
      <el-table-column prop="servings" label="份数" width="80" align="center" />
      <el-table-column label="步骤数" width="90" align="center">
        <template #default="{ row }">
          {{ parseStepsCount(row.steps) }}
        </template>
      </el-table-column>
      <el-table-column label="食材数" width="90" align="center">
        <template #default="{ row }">
          {{ parseIngredients(row.ingredients).length }}
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
      :title="editingId ? '编辑菜谱' : '新增菜谱'"
      width="700px"
      destroy-on-close
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入菜谱名称" />
        </el-form-item>
        <el-form-item label="关联菜品">
          <el-select v-model="form.dishId" placeholder="选择关联菜品（可选）" clearable style="width: 100%">
            <el-option
              v-for="dish in dishes"
              :key="dish.id"
              :label="dish.name"
              :value="dish.id"
            />
          </el-select>
        </el-form-item>
        <div class="form-row">
          <el-form-item label="份数">
            <el-input-number v-model="form.servings" :min="1" :max="99" />
          </el-form-item>
          <el-form-item label="图片URL">
            <el-input v-model="form.imageUrl" placeholder="可选" />
          </el-form-item>
        </div>

        <el-divider content-position="left">烹饪步骤</el-divider>
        <el-form-item label="步骤说明" label-width="100px">
          <el-input
            v-model="form.steps"
            type="textarea"
            :rows="5"
            placeholder="每行一个步骤，例如：&#10;1. 将蔬菜洗净切好&#10;2. 热锅倒油..."
          />
        </el-form-item>

        <el-divider content-position="left">食材清单</el-divider>
        <div class="ingredient-list">
          <div
            v-for="(ing, index) in form.ingredients"
            :key="index"
            class="ingredient-row"
          >
            <el-input v-model="ing.name" placeholder="食材名" style="flex: 2" />
            <el-input v-model="ing.amount" placeholder="用量" style="flex: 1; margin: 0 8px" />
            <el-input v-model="ing.unit" placeholder="单位" style="flex: 1" />
            <el-button
              type="danger"
              :icon="'-'"
              circle
              size="small"
              :disabled="form.ingredients.length <= 1"
              @click="removeIngredientRow(index)"
              style="margin-left: 8px"
            />
          </div>
          <el-button type="primary" plain size="small" @click="addIngredientRow" style="margin-top: 8px">
            + 添加食材行
          </el-button>
        </div>

        <el-divider content-position="left">其他信息</el-divider>
        <el-form-item label="小贴士" label-width="100px">
          <el-input
            v-model="form.tips"
            type="textarea"
            :rows="2"
            placeholder="烹饪小贴士，如火候控制等"
          />
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

.form-row {
  display: flex;
  gap: 20px;
}

.ingredient-list {
  padding: 0 10px;
}

.ingredient-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
</style>
