<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps, UploadFile } from 'element-plus'

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
  value: string
  label: string
  children?: TreeNode[]
}

const list = ref<Category[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = ref(12)

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

const defaultCategories = [
  { name: '主食杂粮', code: 'staple', parentCode: '', sortOrder: 1, icon: '🍚', color: '#F59E0B', aliases: '谷物,米面' },
  { name: '蛋奶豆类', code: 'egg_dairy', parentCode: '', sortOrder: 2, icon: '🥚', color: '#FEF3C7', aliases: '鸡蛋,牛奶,豆腐' },
  { name: '肉类', code: 'meat', parentCode: '', sortOrder: 3, icon: '🥩', color: '#EF4444', aliases: '猪肉,牛肉,鸡肉' },
  { name: '水产', code: 'seafood', parentCode: '', sortOrder: 4, icon: '🐟', color: '#3B82F6', aliases: '鱼,虾,蟹' },
  { name: '蔬菜', code: 'vegetable', parentCode: '', sortOrder: 5, icon: '🥬', color: '#22C55E', aliases: '青菜,萝卜' },
  { name: '水果', code: 'fruit', parentCode: '', sortOrder: 6, icon: '🍎', color: '#EC4899', aliases: '苹果,香蕉' },
  { name: '坚果', code: 'nut', parentCode: '', sortOrder: 7, icon: '🥜', color: '#D97706', aliases: '花生,核桃' },
  { name: '调味品', code: 'seasoning', parentCode: '', sortOrder: 8, icon: '🧂', color: '#A78BFA', aliases: '盐,酱油,醋' },
  { name: '其他', code: 'other', parentCode: '', sortOrder: 9, icon: '📦', color: '#6B7280', aliases: '' }
]

const emojiOptions = [
  '🍚', '🥚', '🥩', '🐟', '🥬', '🍎', '🥜', '🧂', '📦',
  '🍞', '🧀', '🥛', '🍗', '🦐', '🥕', '🍌', '🫒',
  '🌾', '🥔', '🍠', '🌽', '🥑', '🍇', '🍊', '🍋', '🫐',
  '🥣', '🍜', '🥗', '🧈', '🫙'
]

const showEmojiPicker = ref(false)

// 判断是否为图片URL（支持 http/https/blob/data）
function isImageUrl(val: string): boolean {
  if (!val) return false
  return val.startsWith('http') || val.startsWith('blob:') || val.startsWith('data:')
}

const treeData = computed<TreeNode[]>(() => {
  const topLevel = list.value.filter(item => !item.parentCode || item.parentCode === '')
  return topLevel.map(item => buildTreeNode(item))
})

function buildTreeNode(item: Category): TreeNode {
  const children = list.value.filter(child => child.parentCode === item.code)
  const node: TreeNode = {
    value: item.code,
    label: item.name
  }
  if (children.length > 0) {
    node.children = children.map(child => buildTreeNode(child))
  }
  return node
}

async function fetchData() {
  loading.value = true
  try {
    const res = await fetch('/api/ingredient-categories')
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
    parentCode: row.parentCode,
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
      await fetch(`/api/ingredient-categories/${row.id}`, { method: 'DELETE' })
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
      await fetch(`/api/ingredient-categories/${editingId.value}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      })
      const idx = list.value.findIndex(item => item.id === editingId.value)
      if (idx !== -1) {
        list.value[idx] = { ...list.value[idx], ...form }
      }
    } else {
      const res = await fetch('/api/ingredient-categories', {
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
    parentCode: '',
    sortOrder: 0,
    icon: '',
    color: '#FF9F43',
    aliases: ''
  })
  showEmojiPicker.value = false
}

function selectEmoji(emoji: string) {
  form.icon = emoji
  showEmojiPicker.value = false
}

// 处理图片上传选择（本地预览，不上传服务器）
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
  // 创建本地预览 URL (blob:)
  form.icon = URL.createObjectURL(rawFile)
}

onMounted(fetchData)

// 分页
const total = computed(() => list.value.length)
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return list.value.slice(start, start + pageSize.value)
})
function handlePageChange(page: number) { currentPage.value = page }
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>食材分类管理</h2>
      <el-button class="btn-orange" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增分类
      </el-button>
    </div>

    <el-table :data="pagedData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column label="图标" width="80" align="center">
        <template #default="{ row }">
          <img v-if="isImageUrl(row.icon)" :src="row.icon" class="icon-img" />
          <span v-else class="icon-cell">{{ row.icon || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="code" label="编码" width="120" />
      <el-table-column label="上级分类" width="120">
        <template #default="{ row }">
          {{ row.parentCode ? list.find(c => c.code === row.parentCode)?.name : '—' }}
        </template>
      </el-table-column>
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

    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑分类' : '新增分类'"
      width="600px"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="编码" required>
          <el-input v-model="form.code" placeholder="请输入英文编码" />
        </el-form-item>
        <el-form-item label="上级分类">
          <el-tree-select
            v-model="form.parentCode"
            :data="treeData"
            placeholder="留空表示顶级分类"
            clearable
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="图标">
          <div class="icon-selector">
            <div class="icon-preview-area">
              <img v-if="isImageUrl(form.icon)" :src="form.icon" class="preview-img" />
              <span v-else class="preview-icon">{{ form.icon || '暂无图标' }}</span>
            </div>
            <div class="icon-actions">
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :auto-upload="false"
                :on-change="handleImageChange"
                accept="image/*"
              >
                <el-button size="small">上传图片</el-button>
              </el-upload>
              <el-button size="small" @click="showEmojiPicker = !showEmojiPicker">
                选择 Emoji
              </el-button>
              <el-input
                v-model="form.icon"
                placeholder="或输入URL"
                size="small"
                style="width: 120px; margin-left: 8px"
              />
            </div>
            <div v-if="showEmojiPicker" class="emoji-picker">
              <div
                v-for="emoji in emojiOptions"
                :key="emoji"
                class="emoji-item"
                @click="selectEmoji(emoji)"
              >
                {{ emoji }}
              </div>
            </div>
          </div>
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

.icon-cell {
  font-size: 22px;
}

.icon-img {
  width: 32px;
  height: 32px;
  object-fit: cover;
  border-radius: 4px;
  vertical-align: middle;
}

.color-block {
  display: inline-block;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  vertical-align: middle;
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
  font-size: 28px;
  color: #909399;
}

.icon-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.emoji-picker {
  display: grid;
  grid-template-columns: repeat(9, 1fr);
  gap: 8px;
  margin-top: 12px;
  padding: 12px;
  background: #fafafa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.emoji-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  font-size: 22px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.emoji-item:hover {
  background: #e6f7ff;
  transform: scale(1.15);
}

/* 分页 - 橙色主题 */
.pagination-wrap { display: flex; justify-content: flex-end; padding-top: 16px; }
:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: #E67E22 !important;
  border-color: #E67E22 !important;
}
:deep(.el-pagination.is-background .el-pager li:hover:not(.is-active)) { color: #E67E22; }
:deep(.el-pagination .btn-prev:hover),
:deep(.el-pagination .btn-next:hover) { color: #E67E22; }
</style>
