<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Member {
  id?: number
  name: string
  role: string
  gender: string
  birthday: string
  avatarUrl: string
  allergens: string[]
  dietaryRestrictions: string[]
  preferences: string[]
}

const list = ref<Member[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  role: 'child',
  gender: '',
  birthday: '',
  avatarUrl: '',
  allergensStr: '',
  dietaryStr: '',
  preferencesStr: ''
})

const roleOptions = [
  { label: '家长', value: 'parent' },
  { label: '孩子', value: 'child' }
]

const genderOptions = [
  { label: '男', value: 'male' },
  { label: '女', value: 'female' }
]

function computeAge(birthday: string): number | null {
  if (!birthday) return null
  try {
    const birthDate = new Date(birthday)
    const today = new Date()
    let age = today.getFullYear() - birthDate.getFullYear()
    const m = today.getMonth() - birthDate.getMonth()
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) age--
    return age
  } catch {
    return null
  }
}

function parseTags(val: string): string[] {
  return val ? val.split(/[,，]/).map(s => s.trim()).filter(Boolean) : []
}

async function fetchData() {
  loading.value = true
  try {
    const res = await fetch('/api/members')
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

function handleEdit(row: Member) {
  editingId.value = row.id ?? null
  Object.assign(form, {
    name: row.name,
    role: row.role,
    gender: row.gender,
    birthday: row.birthday,
    avatarUrl: row.avatarUrl || '',
    allergensStr: row.allergens ? row.allergens.join('，') : '',
    dietaryStr: row.dietaryRestrictions ? row.dietaryRestrictions.join('，') : '',
    preferencesStr: row.preferences ? row.preferences.join('，') : ''
  })
  dialogVisible.value = true
}

async function handleDelete(row: Member) {
  try {
    await ElMessageBox.confirm(`确认删除成员「${row.name}」？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (row.id) {
      await fetch(`/api/members/${row.id}`, { method: 'DELETE' })
    }
    list.value = list.value.filter(item => item.id !== row.id)
    ElMessage.success('删除成功')
  } catch {}
}

async function handleSubmit() {
  if (!form.name) {
    ElMessage.warning('请填写姓名')
    return
  }
  try {
    const payload = {
      name: form.name,
      role: form.role,
      gender: form.gender,
      birthday: form.birthday,
      avatarUrl: form.avatarUrl || null,
      allergens: parseTags(form.allergensStr),
      dietaryRestrictions: parseTags(form.dietaryStr),
      preferences: parseTags(form.preferencesStr)
    }
    if (editingId.value) {
      await fetch(`/api/members/${editingId.value}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      const idx = list.value.findIndex(item => item.id === editingId.value)
      if (idx !== -1) {
        list.value[idx] = { ...list.value[idx], ...payload }
      }
    } else {
      const res = await fetch('/api/members', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      const data = await res.json()
      list.value.push({ ...payload, id: data.id ?? Date.now() })
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
    role: 'child',
    gender: '',
    birthday: '',
    avatarUrl: '',
    allergensStr: '',
    dietaryStr: '',
    preferencesStr: ''
  })
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>成员管理</h2>
      <el-button class="btn-orange" @click="handleAdd">
        <span style="margin-right: 4px">+</span> 新增成员
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
      <el-table-column label="头像" width="80" align="center">
        <template #default="{ row }">
          <el-avatar :size="36" :src="row.avatarUrl" v-if="row.avatarUrl">
            {{ row.name?.charAt(0) }}
          </el-avatar>
          <el-avatar :size="36" v-else>
            {{ row.name?.charAt(0) }}
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="姓名" min-width="100" />
      <el-table-column label="角色" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.role === 'parent' ? '' : 'success'" size="small">
            {{ row.role === 'parent' ? '家长' : '孩子' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="性别" width="70" align="center">
        <template #default="{ row }">
          {{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '—' }}
        </template>
      </el-table-column>
      <el-table-column label="年龄" width="70" align="center">
        <template #default="{ row }">
          {{ computeAge(row.birthday) ?? '—' }}
        </template>
      </el-table-column>
      <el-table-column prop="birthday" label="生日" width="120" align="center" />
      <el-table-column label="过敏源" min-width="160">
        <template #default="{ row }">
          <template v-if="row.allergens && row.allergens.length">
            <el-tag
              v-for="tag in row.allergens"
              :key="tag"
              type="danger"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px"
            >
              {{ tag }}
            </el-tag>
          </template>
          <span v-else style="color: #c0c4cc">—</span>
        </template>
      </el-table-column>
      <el-table-column label="饮食限制" min-width="160">
        <template #default="{ row }">
          <template v-if="row.dietaryRestrictions && row.dietaryRestrictions.length">
            <el-tag
              v-for="tag in row.dietaryRestrictions"
              :key="tag"
              type="warning"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px"
            >
              {{ tag }}
            </el-tag>
          </template>
          <span v-else style="color: #c0c4cc">—</span>
        </template>
      </el-table-column>
      <el-table-column label="口味偏好" min-width="180">
        <template #default="{ row }">
          <template v-if="row.preferences && row.preferences.length">
            <el-tag
              v-for="tag in row.preferences"
              :key="tag"
              type="success"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px"
            >
              {{ tag }}
            </el-tag>
          </template>
          <span v-else style="color: #c0c4cc">—</span>
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
      :title="editingId ? '编辑成员' : '新增成员'"
      width="620px"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px">
        <div class="form-row">
          <el-form-item label="姓名" required>
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
              <el-option
                v-for="opt in roleOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="性别">
            <el-select v-model="form.gender" placeholder="请选择" style="width: 100%" clearable>
              <el-option
                v-for="opt in genderOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="生日">
            <el-date-picker
              v-model="form.birthday"
              type="date"
              placeholder="选择生日"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-form-item label="头像URL">
          <el-input v-model="form.avatarUrl" placeholder="可选，输入图片地址" />
        </el-form-item>

        <el-divider content-position="left">过敏源</el-divider>
        <el-form-item label="过敏源列表">
          <el-input
            v-model="form.allergensStr"
            placeholder="多个过敏源用逗号分隔，如：海鲜、花生、芒果"
          />
        </el-form-item>

        <el-divider content-position="left">饮食限制</el-divider>
        <el-form-item label="饮食限制">
          <el-input
            v-model="form.dietaryStr"
            placeholder="多个限制用逗号分隔，如：低盐、低糖、无麸质"
          />
        </el-form-item>

        <el-divider content-position="left">口味偏好</el-divider>
        <el-form-item label="口味偏好">
          <el-input
            v-model="form.preferencesStr"
            placeholder="多个偏好用逗号分隔，如：喜欢面食、不喜欢胡萝卜"
          />
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

.form-row {
  display: flex;
  gap: 20px;
}

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
</style>
