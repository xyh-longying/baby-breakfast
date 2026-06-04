<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'

interface Category {
  id?: number
  name: string
  code: string
  parentCode: string
  sortOrder: number
  icon: string
  color: string
  aliases: string
}

interface TreeNode {
  id?: number
  name: string
  code: string
  parentCode: string
  sortOrder: number
  icon: string
  color: string
  aliases: string
  children?: TreeNode[]
}

const list = ref<Category[]>([])
const treeData = ref<TreeNode[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
// 新增时的默认父级分类code（用于"添加子分类"）
const defaultParentCode = ref('')

const form = reactive<Category>({
  name: '',
  code: '',
  parentCode: '',
  sortOrder: 0,
  icon: '',
  color: '#FF9F43',
  aliases: ''
})

// 系统主题橙色
const themeOrange = '#E67E22'

const emojiOptions = [
  '🍚', '🥚', '🥩', '🐟', '🥬', '🍎', '🥜', '🧂', '📦',
  '🍞', '🧀', '🥛', '🍗', '🦐', '🥕', '🍌', '🫒',
  '🌾', '🥔', '🍠', '🌽', '🥑', '🍇', '🍊', '🍋', '🫐',
  '🥣', '🍜', '🥗', '🧈', '🫙'
]
const showEmojiPicker = ref(false)

// 判断是否为图片URL
function isImageUrl(val: string): boolean {
  if (!val) return false
  return val.startsWith('http') || val.startsWith('blob:') || val.startsWith('data:')
}

// 构建树形数据（用于弹窗中的父级选择）
const parentTreeData = computed(() => {
  const topLevel = list.value.filter(item => !item.parentCode || item.parentCode === '')
    .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  return buildParentTreeNodes(topLevel)
})

function buildParentTreeNodes(items: Category[]): any[] {
  return items.map(item => ({
    value: item.code,
    label: item.name,
    children: buildParentTreeNodes(
      list.value.filter(c => c.parentCode === item.code).sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    )
  }))
}

// 将扁平列表转为树形结构（用于表格展示）
function buildTableTree(): TreeNode[] {
  const map = new Map<string, TreeNode>()
  const roots: TreeNode[] = []

  // 先创建所有节点
  list.value.forEach(item => {
    map.set(item.code, {
      id: item.id,
      name: item.name,
      code: item.code,
      parentCode: item.parentCode,
      sortOrder: item.sortOrder,
      icon: item.icon,
      color: item.color,
      aliases: item.aliases,
      children: []
    })
  })

  // 建立父子关系
  list.value.forEach(item => {
    const node = map.get(item.code)!
    if (item.parentCode && map.has(item.parentCode)) {
      map.get(item.parentCode)!.children!.push(node)
    } else {
      roots.push(node)
    }
  })

  // 排序：子节点按sortOrder排序
  function sortChildren(nodes: TreeNode[]) {
    nodes.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    nodes.forEach(n => { if (n.children && n.children.length) sortChildren(n.children!) })
  }
  sortChildren(roots)
  return roots
}

async function fetchData() {
  loading.value = true
  try {
    const res = await fetch('/api/ingredient-categories')
    if (res.ok) {
      const json = await res.json()
      list.value = json?.data && Array.isArray(json.data) ? json.data : []
    }
    // 构建树形数据
    treeData.value = buildTableTree()
  } catch {} finally {
    loading.value = false
  }
}

/** 添加顶级分类 */
function handleAdd() {
  resetForm('')
  dialogVisible.value = true
}

/** 添加子分类（在指定分类下） */
function handleAddChild(row: Category | TreeNode) {
  resetForm(row.code)
  dialogVisible.value = true
}

function handleEdit(row: Category | TreeNode) {
  editingId.value = row.id ?? null
  Object.assign(form, {
    name: row.name,
    code: row.code,
    parentCode: row.parentCode,
    sortOrder: row.sortOrder,
    icon: row.icon,
    color: row.color,
    aliases: row.aliases
  })
  dialogVisible.value = true
}

async function handleDelete(row: Category | TreeNode) {
  // 检查是否有子分类
  const hasChildren = list.value.some(c => c.parentCode === row.code)
  if (hasChildren) {
    ElMessage.warning(`「${row.name}」下还有子分类，请先删除子分类`)
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除分类「${row.name}」？`, '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    if (row.id) await fetch(`/api/ingredient-categories/${row.id}`, { method: 'DELETE' })
    list.value = list.value.filter(item => item.id !== row.id)
    treeData.value = buildTableTree()
    ElMessage.success('删除成功')
  } catch {}
}

async function handleSubmit() {
  if (!form.name || !form.code) { ElMessage.warning('请填写名称和编码'); return }
  try {
    if (editingId.value) {
      await fetch(`/api/ingredient-categories/${editingId.value}`, {
        method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form)
      })
      const idx = list.value.findIndex(item => item.id === editingId.value)
      if (idx !== -1) list.value[idx] = { ...list.value[idx], ...form }
    } else {
      const res = await fetch('/api/ingredient-categories', {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form)
      })
      const data = await res.json()
      list.value.push({ ...form, id: data.id ?? Date.now() })
    }
    treeData.value = buildTableTree()
    ElMessage.success(editingId.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
  } catch { ElMessage.error('操作失败') }
}

function resetForm(parentCode: string = '') {
  editingId.value = null
  defaultParentCode.value = parentCode
  Object.assign(form, {
    name: '', code: '', parentCode: parentCode,
    sortOrder: 0, icon: '', color: '#FF9F43', aliases: ''
  })
  showEmojiPicker.value = false
}

function selectEmoji(emoji: string) { form.icon = emoji; showEmojiPicker.value = false }

function handleImageChange(uploadFile: UploadFile) {
  const rawFile = uploadFile.raw
  if (!rawFile) return
  if (!rawFile.type.startsWith('image/')) { ElMessage.error('只能上传图片!'); return }
  if (rawFile.size / 1024 / 1024 > 2) { ElMessage.error('图片不超过2MB!'); return }
  form.icon = URL.createObjectURL(rawFile)
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>食材分类管理</h2>
      <el-button class="btn-orange" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增分类
      </el-button>
    </div>

    <!-- 树状表格 -->
    <el-table
      :data="treeData"
      v-loading="loading"
      border
      row-key="code"
      :tree-props="{ children: 'children' }"
      :default-expand-all="true"
      style="width: 100%"
    >
      <!-- 名称+图标列（合并，避免树形展开图标遮挡） -->
      <el-table-column label="分类名称" min-width="260">
        <template #default="{ row }">
          <span class="name-with-icon">
            <span v-if="row.icon" class="row-icon">{{ row.icon }}</span>
            <span class="row-name">{{ row.name }}</span>
          </span>
        </template>
      </el-table-column>

      <!-- 编码列 -->
      <el-table-column prop="code" label="编码" width="130" />

      <!-- 上级分类 -->
      <el-table-column label="上级分类" width="120">
        <template #default="{ row }">
          {{ row.parentCode ? list.find(c => c.code === row.parentCode)?.name : '—' }}
        </template>
      </el-table-column>

      <!-- 排序 -->
      <el-table-column prop="sortOrder" label="排序" width="70" align="center" />

      <!-- 颜色 -->
      <el-table-column label="颜色" width="70" align="center">
        <template #default="{ row }">
          <span class="color-block" :style="{ backgroundColor: row.color }"></span>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleAddChild(row)">+ 子分类</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : (defaultParentCode ? `新增子分类` : '新增分类')" width="600px" destroy-on-close>
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required><el-input v-model="form.name" placeholder="请输入分类名称" /></el-form-item>
        <el-form-item label="编码" required><el-input v-model="form.code" placeholder="请输入英文编码" /></el-form-item>
        <el-form-item label="上级分类">
          <el-tree-select
            v-model="form.parentCode"
            :data="parentTreeData"
            placeholder="留空表示顶级分类"
            clearable check-strictly filterable
            :render-after-expand="false"
            style="width: 100%"
          />
          <div v-if="defaultParentCode && !editingId" class="parent-hint">
            默认父级：{{ list.find(c => c.code === defaultParentCode)?.name }}（可修改）
          </div>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" :max="999" /></el-form-item>
        <el-form-item label="图标">
          <div class="icon-selector">
            <div class="icon-preview-area">
              <img v-if="isImageUrl(form.icon)" :src="form.icon" class="preview-img" />
              <span v-else class="preview-icon">{{ form.icon || '暂无图标' }}</span>
            </div>
            <div class="icon-actions">
              <el-upload action="#" :show-file-list="false" :auto-upload="false" :on-change="handleImageChange" accept="image/*">
                <el-button size="small">上传图片</el-button>
              </el-upload>
              <el-button size="small" @click="showEmojiPicker = !showEmojiPicker">选择 Emoji</el-button>
              <el-input v-model="form.icon" placeholder="或输入URL" size="small" style="width: 120px; margin-left: 8px" />
            </div>
            <div v-if="showEmojiPicker" class="emoji-picker">
              <div v-for="emoji in emojiOptions" :key="emoji" class="emoji-item" @click="selectEmoji(emoji)">
                {{ emoji }}
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="颜色"><el-color-picker v-model="form.color" /></el-form-item>
        <el-form-item label="别名"><el-input v-model="form.aliases" placeholder="多个别名用逗号分隔" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button class="btn-orange" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container { padding: 24px; background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.04); }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 600; color: #1f2937; }

.btn-orange { background-color: #E67E22 !important; border-color: #E67E22 !important; color: #fff !important; }
.btn-orange:hover, .btn-orange:focus { background-color: #D35400 !important; border-color: #D35400 !important; }

/* 图标+名称 */
.name-with-icon { display: inline-flex; align-items: center; gap: 6px; }
.row-icon { font-size: 20px; line-height: 1; flex-shrink: 0; }
.row-name { font-size: 14px; }

.color-block { display: inline-block; width: 24px; height: 24px; border-radius: 4px; border: 1px solid #e5e7eb; vertical-align: middle; }

.parent-hint { margin-top: 4px; font-size: 12px; color: #909399; }

/* 图标选择器 */
.icon-selector { width: 100%; }
.icon-preview-area { display: flex; align-items: center; justify-content: center; height: 60px; background: #f5f7fa; border: 1px dashed #dcdfe6; border-radius: 6px; margin-bottom: 10px; }
.preview-img { max-width: 50px; max-height: 50px; object-fit: contain; border-radius: 4px; }
.preview-icon { font-size: 28px; color: #909399; }
.icon-actions { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.emoji-picker { display: grid; grid-template-columns: repeat(9, 1fr); gap: 8px; margin-top: 12px; padding: 12px; background: #fafafa; border: 1px solid #e4e7ed; border-radius: 6px; }
.emoji-item { display: flex; align-items: center; justify-content: center; width: 36px; height: 36px; font-size: 22px; cursor: pointer; border-radius: 4px; transition: all 0.2s; }
.emoji-item:hover { background: #e6f7ff; transform: scale(1.15); }

/* 树表格展开行样式 */
:deep(.el-table__expand-icon) { color: #E67E22; }
:deep(.el-table__indent) { padding-left: 8px !important; }
</style>
