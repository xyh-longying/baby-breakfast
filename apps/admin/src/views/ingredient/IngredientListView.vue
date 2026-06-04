<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'

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

interface CategoryItem {
  code: string
  name: string
  parentCode: string | null
  color: string
}

interface TreeNode {
  value: string
  label: string
  children?: TreeNode[]
}

const list = ref<Ingredient[]>([])
const allCategories = ref<CategoryItem[]>([])
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

// 分类名称映射
const categoryNameMap = computed(() => {
  const map: Record<string, string> = {}
  allCategories.value.forEach(c => { map[c.code] = c.name })
  return map
})

// 分类颜色映射（用于标签显示）
const categoryColorMap = computed(() => {
  const map: Record<string, string> = {}
  allCategories.value.forEach(c => { map[c.code] = c.color || '#E67E22' })
  return map
})

// 判断分类颜色是否为浅色背景，需要深色文字
function isLightColor(hex: string): boolean {
  if (!hex || hex === '#FFFFFF' || hex === '#ffffff' || hex === '#FFF8DC' || hex === '#FEF3C7' || hex === '#F5DEB3' || hex === '#FFDAB9') return true
  if (!hex) return true
  // 计算亮度
  const h = hex.replace('#', '')
  const r = parseInt(h.substring(0, 2), 16)
  const g = parseInt(h.substring(2, 4), 16)
  const b = parseInt(h.substring(4, 6), 16)
  return (r * 299 + g * 587 + b * 114) / 1000 > 155
}

// 构建树形数据
const treeData = computed<TreeNode[]>(() => {
  const topLevel = allCategories.value.filter(c => !c.parentCode)
  return topLevel.map(item => buildTreeNode(item))
})

function buildTreeNode(item: CategoryItem): TreeNode {
  const children = allCategories.value.filter(c => c.parentCode === item.code)
  const node: TreeNode = {
    value: item.code,
    label: item.name
  }
  if (children.length > 0) {
    node.children = children.map(child => buildTreeNode(child))
  }
  return node
}

const filteredList = computed(() => {
  if (!filterCategory.value) return list.value
  return list.value.filter(item => item.categoryCode === filterCategory.value)
})

async function fetchCategories() {
  try {
    const res = await fetch('/api/ingredient-categories')
    if (res.ok) {
      const json = await res.json()
      allCategories.value = json?.data && Array.isArray(json.data) ? json.data : []
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

// 判断是否为图片URL
function isImageUrl(val: string): boolean {
  if (!val) return false
  return val.startsWith('http') || val.startsWith('blob:') || val.startsWith('data:')
}

// 处理图片上传
function handleImageChange(uploadFile: UploadFile) {
  const rawFile = uploadFile.raw
  if (!rawFile) return
  const isImage = rawFile.type.startsWith('image/')
  const isLt2M = rawFile.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return
  }
  form.imageUrl = URL.createObjectURL(rawFile)
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
      <el-button class="btn-orange" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增食材
      </el-button>
    </div>

    <div class="filter-bar">
      <el-tree-select
        v-model="filterCategory"
        :data="treeData"
        placeholder="按分类筛选"
        clearable
        check-strictly
        :render-after-expand="false"
        style="width: 240px"
        @change="fetchData"
      />
    </div>

    <el-table :data="filteredList" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column label="分类" width="130" align="center">
        <template #default="{ row }">
          <span
            v-if="categoryNameMap[row.categoryCode]"
            class="category-tag"
            :style="{
              backgroundColor: categoryColorMap[row.categoryCode] || '#E67E22',
              color: isLightColor(categoryColorMap[row.categoryCode]) ? '#333' : '#fff'
            }"
          >
            {{ categoryNameMap[row.categoryCode] }}
          </span>
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
          <el-tree-select
            v-model="form.categoryCode"
            :data="treeData"
            placeholder="请选择分类"
            style="width: 100%"
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如 g、ml、个" style="width: 160px" />
        </el-form-item>
        <el-form-item label="密度">
          <el-input-number v-model="form.density" :min="0.01" :max="9999" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="图片">
          <div class="icon-selector">
            <div class="icon-preview-area">
              <img v-if="isImageUrl(form.imageUrl)" :src="form.imageUrl" class="preview-img" />
              <span v-else class="preview-icon">{{ form.imageUrl || '暂无图片' }}</span>
            </div>
            <div class="icon-actions">
              <el-upload
                action="#"
                :show-file-list="false"
                :auto-upload="false"
                :on-change="handleImageChange"
                accept="image/*"
              >
                <el-button size="small">上传图片</el-button>
              </el-upload>
              <el-input
                v-model="form.imageUrl"
                placeholder="或输入图片URL"
                size="small"
                style="width: 160px; margin-left: 8px"
              />
            </div>
          </div>
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
        <el-button class="btn-orange" @click="handleSubmit">确定</el-button>
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

/* 系统主题橙色按钮 */
.btn-orange {
  background-color: #E67E22 !important;
  border-color: #E67E22 !important;
  color: #fff !important;
}

.btn-orange:hover,
.btn-orange:focus {
  background-color: #D35400 !important;
  border-color: #D35400 !important;
}

.filter-bar {
  margin-bottom: 16px;
}

/* 分类标签 - 根据背景自动调整字体颜色 */
.category-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 20px;
  white-space: nowrap;
}

.icon-selector {
  width: 100%;
}

.icon-preview-area {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  margin-bottom: 10px;
}

.preview-img {
  max-width: 50px;
  max-height: 50px;
  object-fit: contain;
  border-radius: 4px;
}

.preview-icon {
  font-size: 14px;
  color: #909399;
}

.icon-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
