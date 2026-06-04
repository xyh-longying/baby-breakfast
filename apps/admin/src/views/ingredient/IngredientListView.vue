<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
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
  id: string
  label: string
  code: string
  color: string
  children?: TreeNode[]
}

const list = ref<Ingredient[]>([])
const allCategories = ref<CategoryItem[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)

// 当前选中的分类code（默认全部）
const selectedCategoryCode = ref<string>('')
// 搜索关键词
const searchKeyword = ref('')
// 分页
const currentPage = ref(1)
const pageSize = ref(10)

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

// 分类颜色映射
const categoryColorMap = computed(() => {
  const map: Record<string, string> = {}
  allCategories.value.forEach(c => { map[c.code] = c.color || '#E67E22' })
  return map
})

// 判断颜色是否为浅色背景
function isLightColor(hex: string): boolean {
  if (!hex) return true
  const whiteList = ['#FFFFFF', '#ffffff', '#FFF8DC', '#FEF3C7', '#F5DEB3', '#FFDAB9', '#FAEBD7']
  if (whiteList.includes(hex)) return true
  const h = hex.replace('#', '')
  if (h.length !== 6) return true
  const r = parseInt(h.substring(0, 2), 16)
  const g = parseInt(h.substring(2, 4), 16)
  const b = parseInt(h.substring(4, 6), 16)
  return (r * 299 + g * 587 + b * 114) / 1000 > 155
}

// 构建树形数据（用于左侧树形导航）
const treeData = computed<TreeNode[]>(() => {
  // 添加"全部分类"根节点
  const allNode: TreeNode = { id: '__all__', label: '📋 全部分类', code: '', color: '#E67E22' }
  const topLevel = allCategories.value.filter(c => !c.parentCode).sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  allNode.children = topLevel.map(item => buildTreeNode(item))
  return [allNode]
})

function buildTreeNode(item: CategoryItem): TreeNode {
  const children = allCategories.value.filter(c => c.parentCode === item.code).sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  // 根据图标或名称添加emoji前缀
  let prefix = ''
  if (!item.parentCode) {
    const iconMap: Record<string, string> = {
      grain: '🌾', dairy: '🥛', meat: '🥩', seafood: '🐟',
      vegetable: '🥬', fruit: '🍎', nut: '🌰', condiment: '🧂', other: '📦'
    }
    prefix = iconMap[item.code] ? iconMap[item.code] + ' ' : ''
  }
  const node: TreeNode = {
    id: item.code,
    label: prefix + item.name,
    code: item.code,
    color: item.color || '#E67E22'
  }
  if (children.length > 0) {
    node.children = children.map(child => buildTreeNode(child))
  }
  return node
}

// 当前树选中的key（支持"全部分类"节点）
const selectedTreeKey = computed({
  get: () => selectedCategory.value ? selectedCategory.value : '__all__',
  set: (val: string) => {
    selectedCategoryCode.value = val === '__all__' ? '' : val
    currentPage.value = 1
    fetchData()
  }
})

// 当前选中分类对象
const selectedCategory = computed(() => {
  return selectedTreeKey.value
})

// 过滤后的数据（搜索 + 分类筛选）
const filteredData = computed(() => {
  let data = list.value
  // 分类筛选
  if (selectedCategoryCode.value) {
    data = data.filter(item => item.categoryCode === selectedCategoryCode.value)
  }
  // 关键词搜索
  if (searchKeyword.value.trim()) {
    const kw = searchKeyword.value.trim().toLowerCase()
    data = data.filter(item =>
      item.name.toLowerCase().includes(kw) ||
      (item.aliases && String(item.aliases).toLowerCase().includes(kw))
    )
  }
  return data
})

// 分页数据
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const total = computed(() => filteredData.value.length)

// 监听分类变化重新获取数据
watch(selectedCategoryCode, () => {
  currentPage.value = 1
  fetchData()
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
    const url = selectedCategoryCode.value
      ? `/api/ingredients?category=${selectedCategoryCode.value}`
      : '/api/ingredients'
    const res = await fetch(url)
    if (res.ok) {
      const json = await res.json()
      list.value = json?.data && Array.isArray(json.data) ? json.data : []
    }
  } catch {} finally {
    loading.value = false
  }
}

function handleTreeNodeClick(data: TreeNode) {
  selectedCategoryCode.value = data.code === '' ? '' : data.code
  currentPage.value = 1
  fetchData()
}

function handleAdd() {
  resetForm()
  // 默认选中当前分类
  if (selectedCategoryCode.value) {
    form.categoryCode = selectedCategoryCode.value
  }
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

function isImageUrl(val: string): boolean {
  if (!val) return false
  return val.startsWith('http') || val.startsWith('blob:') || val.startsWith('data:')
}

function handleImageChange(uploadFile: UploadFile) {
  const rawFile = uploadFile.raw
  if (!rawFile) return
  if (!rawFile.type.startsWith('image/')) { ElMessage.error('只能上传图片文件!'); return }
  if (rawFile.size / 1024 / 1024 > 2) { ElMessage.error('图片不能超过2MB!'); return }
  form.imageUrl = URL.createObjectURL(rawFile)
}

function handlePageChange(page: number) {
  currentPage.value = page
}

onMounted(async () => {
  await fetchCategories()
  fetchData()
})
</script>

<template>
  <div class="page-layout">
    <!-- 左侧：分类树 -->
    <div class="left-panel">
      <div class="tree-header">食材分类</div>
      <el-tree
        :data="treeData"
        :props="{ label: 'label', children: 'children' }"
        node-key="id"
        highlight-current
        :default-expanded-keys="['__all__']"
        :current-node-key="selectedTreeKey"
        @node-click="handleTreeNodeClick"
        class="category-tree"
      >
        <template #default="{ data }">
          <span class="tree-node-label" :class="{ 'is-active': selectedTreeKey === data.id }">
            {{ data.label }}
          </span>
        </template>
      </el-tree>
    </div>

    <!-- 右侧：食材列表 -->
    <div class="right-panel">
      <div class="panel-header">
        <h2>食材列表</h2>
        <div class="header-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索食材名称..."
            prefix-icon="Search"
            clearable
            style="width: 220px; margin-right: 12px"
          />
          <el-button class="btn-orange" @click="handleAdd">
            <span style="margin-right: 4px">+</span> 新增食材
          </el-button>
        </div>
      </div>

      <!-- 当前分类提示 -->
      <div class="category-hint" v-if="selectedCategoryCode">
        <span class="hint-tag" :style="{ backgroundColor: categoryColorMap[selectedCategoryCode] || '#E67E22', color: isLightColor(categoryColorMap[selectedCategoryCode]) ? '#333' : '#fff' }">
          {{ categoryNameMap[selectedCategoryCode] || selectedCategoryCode }}
        </span>
        <span class="hint-text">共 {{ total }} 条食材</span>
      </div>
      <div class="category-hint" v-else>
        <span class="hint-tag" style="background-color: #E67E22; color: #fff;">📋 全部分类</span>
        <span class="hint-text">共 {{ total }} 条食材</span>
      </div>

      <el-table :data="pagedData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column label="分类" width="120" align="center">
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
        <el-table-column prop="unit" label="单位" width="70" align="center" />
        <el-table-column prop="density" label="密度(g/ml)" width="100" align="center" />
        <el-table-column label="别名" min-width="130" show-overflow-tooltip>
          <template #default="{ row }">
            {{ Array.isArray(row.aliases) ? row.aliases.join(', ') : (row.aliases || '—') }}
          </template>
        </el-table-column>
        <el-table-column label="标签" min-width="160">
          <template #default="{ row }">
            <template v-if="Array.isArray(row.tags)">
              <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 4px; margin-bottom: 2px">{{ tag }}</el-tag>
            </template>
            <template v-else-if="row.tags">
              <el-tag v-for="tag in parseTags(row.tags)" :key="tag" size="small" style="margin-right: 4px; margin-bottom: 2px">{{ tag }}</el-tag>
            </template>
            <span v-if="!row.tags || (!Array.isArray(row.tags) && !row.tags.length)">—</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          :pager-count="5"
          background
          @current-change="handlePageChange"
        />
      </div>

      <!-- 弹窗 -->
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
                <el-upload action="#" :show-file-list="false" :auto-upload="false" :on-change="handleImageChange" accept="image/*">
                  <el-button size="small">上传图片</el-button>
                </el-upload>
                <el-input v-model="form.imageUrl" placeholder="或输入URL" size="small" style="width: 140px; margin-left: 8px" />
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
  </div>
</template>

<style scoped>
.page-layout {
  display: flex;
  gap: 20px;
  height: calc(100vh - 120px);
  min-height: 500px;
}

/* 左侧分类树 */
.left-panel {
  width: 240px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tree-header {
  padding: 16px 16px 12px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  border-bottom: 1px solid #f0f0f0;
}

.category-tree {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

/* 自定义树节点样式 */
:deep(.el-tree-node__content) {
  height: 36px;
  border-radius: 6px;
  margin: 1px 4px;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa !important;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #fef3e6 !important;
  color: #E67E22;
}

.tree-node-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.tree-icon-all {
  font-size: 16px;
}

.tree-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #ccc;
  flex-shrink: 0;
}

.tree-leaf-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 3px;
  flex-shrink: 0;
}

/* 右侧内容区 */
.right-panel {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.panel-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.header-actions {
  display: flex;
  align-items: center;
}

/* 橙色按钮 */
.btn-orange {
  background-color: #E67E22 !important;
  border-color: #E67E22 !important;
  color: #fff !important;
}
.btn-orange:hover, .btn-orange:focus {
  background-color: #D35400 !important;
  border-color: #D35400 !important;
}

/* 分类提示条 */
.category-hint {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  padding: 8px 0;
  flex-shrink: 0;
}

.hint-tag {
  display: inline-block;
  padding: 2px 12px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 22px;
}

.hint-text {
  font-size: 13px;
  color: #909399;
}

/* 分类标签 */
.category-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 20px;
  white-space: nowrap;
}

/* 表格区域 */
:deep(.el-table) {
  flex: 1;
  overflow: auto;
}

/* 分页 */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  flex-shrink: 0;
}

/* 图片上传 */
.icon-selector { width: 100%; }
.icon-preview-area {
  display: flex; align-items: center; justify-content: center;
  height: 60px; background: #f5f7fa; border: 1px dashed #dcdfe6;
  border-radius: 6px; margin-bottom: 10px;
}
.preview-img { max-width: 50px; max-height: 50px; object-fit: contain; border-radius: 4px; }
.preview-icon { font-size: 14px; color: #909399; }
.icon-actions { display: flex; align-items: center; gap: 8px; }
</style>
