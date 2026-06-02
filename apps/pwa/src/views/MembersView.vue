<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { FamilyMember } from '@baby-breakfast/shared';
import { getMembers, deleteMember, type CreateMemberRequest, uploadImage } from '../api/member';
import { authState } from '../auth';

const members = ref<FamilyMember[]>([]);
const showForm = ref(false);
const editingId = ref<number | null>(null);
const form = ref<CreateMemberRequest>({
  name: '',
  role: 'child',
  gender: 'female',
  birthday: null,
  avatarUrl: null,
  allergens: [],
  dietaryRestrictions: [],
  preferences: [],
});

const allergenInput = ref('');
const restrictionInput = ref('');
const preferenceInput = ref('');
const avatarFileInput = ref<HTMLInputElement | null>(null);
const avatarPreview = ref<string | null>(null);
const uploadProgress = ref(false);

onMounted(async () => {
  await loadMembers();
});

async function loadMembers() {
  members.value = await getMembers();
}

function startCreate() {
  editingId.value = null;
  form.value = {
    name: '',
    role: 'child',
    gender: 'female',
    birthday: null,
    avatarUrl: null,
    allergens: [],
    dietaryRestrictions: [],
    preferences: [],
  };
  avatarPreview.value = null;
  showForm.value = true;
}

function startEdit(member: FamilyMember) {
  editingId.value = member.id;
  form.value = {
    name: member.name,
    role: member.role,
    gender: member.gender,
    birthday: member.birthday,
    avatarUrl: member.avatarUrl,
    allergens: [...member.allergens],
    dietaryRestrictions: [...member.dietaryRestrictions],
    preferences: [...member.preferences],
  };
  avatarPreview.value = member.avatarUrl ?? null;
  showForm.value = true;
}

async function handleAvatarChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件');
    return;
  }

  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过 5MB');
    return;
  }

  uploadProgress.value = true;
  try {
    const imageUrl = await uploadImage(file);
    form.value.avatarUrl = imageUrl ?? null;
    avatarPreview.value = imageUrl ?? null;
  } catch (error) {
    alert('上传失败：' + (error instanceof Error ? error.message : '未知错误'));
  } finally {
    uploadProgress.value = false;
    input.value = '';
  }
}

function removeAvatar() {
  form.value.avatarUrl = null;
  avatarPreview.value = null;
  if (avatarFileInput.value) {
    avatarFileInput.value.value = '';
  }
}

async function handleSave() {
  if (editingId.value) {
    await import('../api/member').then(m => m.updateMember(editingId.value!, form.value));
  } else {
    await import('../api/member').then(m => m.createMember(form.value));
  }
  showForm.value = false;
  await loadMembers();
}

async function handleDelete(id: number) {
  if (confirm('确定要删除这个家庭成员吗？')) {
    await deleteMember(id);
    await loadMembers();
  }
}

function addAllergen() {
  if (allergenInput.value.trim() && !form.value.allergens?.includes(allergenInput.value.trim())) {
    form.value.allergens = [...(form.value.allergens ?? []), allergenInput.value.trim()];
    allergenInput.value = '';
  }
}

function addRestriction() {
  if (restrictionInput.value.trim() && !form.value.dietaryRestrictions?.includes(restrictionInput.value.trim())) {
    form.value.dietaryRestrictions = [...(form.value.dietaryRestrictions ?? []), restrictionInput.value.trim()];
    restrictionInput.value = '';
  }
}

function addPreference() {
  if (preferenceInput.value.trim() && !form.value.preferences?.includes(preferenceInput.value.trim())) {
    form.value.preferences = [...(form.value.preferences ?? []), preferenceInput.value.trim()];
    preferenceInput.value = '';
  }
}

function removeTag(list: string[], item: string) {
  const idx = list.indexOf(item);
  if (idx > -1) {
    list.splice(idx, 1);
  }
}

function calculateAge(birthday: string): number | null {
  const birthDate = new Date(birthday);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  return age;
}

const session = ref(authState.state.session);
</script>

<template>
  <div class="members-page">
    <header class="page-header">
      <h2>家庭成员</h2>
      <button class="primary-button" @click="startCreate">新增成员</button>
    </header>

    <div class="members-list">
      <div v-for="member in members" :key="member.id" class="member-card">
        <div class="member-info">
          <div class="member-header">
            <img v-if="member.avatarUrl" :src="member.avatarUrl" :alt="member.name" class="member-avatar" />
            <div v-else class="member-avatar-placeholder">
              {{ member.gender === 'male' ? '👨' : member.gender === 'female' ? '👩' : '👤' }}
            </div>
            <div class="member-details">
              <div class="member-name">{{ member.name }}</div>
              <div class="member-meta">
                {{ member.role === 'parent' ? '家长' : member.role === 'child' ? '孩子' : '其他' }}
                · {{ member.gender === 'male' ? '男' : member.gender === 'female' ? '女' : '其他' }}
                <span v-if="member.birthday">· {{ calculateAge(member.birthday) }}岁</span>
                <span v-if="member.birthday">· 生日：{{ member.birthday }}</span>
              </div>
            </div>
          </div>
          <div v-if="member.allergens.length > 0" class="member-tags">
            <span class="tag danger">过敏：{{ member.allergens.join(', ') }}</span>
          </div>
          <div v-if="member.dietaryRestrictions.length > 0" class="member-tags">
            <span class="tag warning">忌口：{{ member.dietaryRestrictions.join(', ') }}</span>
          </div>
          <div v-if="member.preferences.length > 0" class="member-tags">
            <span class="tag info">偏好：{{ member.preferences.join(', ') }}</span>
          </div>
        </div>
        <div class="member-actions">
          <button class="text-button" @click="startEdit(member)">编辑</button>
          <button class="text-button danger" @click="handleDelete(member.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal">
        <h3>{{ editingId ? '编辑成员' : '新增成员' }}</h3>
        <div class="form-group">
          <label>姓名</label>
          <input v-model="form.name" type="text" class="input" />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>角色</label>
            <select v-model="form.role" class="input">
              <option value="parent">家长</option>
              <option value="child">孩子</option>
              <option value="other">其他</option>
            </select>
          </div>
          <div class="form-group">
            <label>性别</label>
            <select v-model="form.gender" class="input">
              <option value="male">男</option>
              <option value="female">女</option>
              <option value="other">其他</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label>生日</label>
          <input v-model="form.birthday" type="date" class="input" />
        </div>
        <div class="form-group">
          <label>头像</label>
          <div class="avatar-upload">
            <div v-if="avatarPreview" class="avatar-preview">
              <img :src="avatarPreview" alt="头像预览" class="avatar-image" />
              <button type="button" class="remove-avatar" @click="removeAvatar">×</button>
            </div>
            <div v-else class="avatar-placeholder">
              <span>{{ form.gender === 'male' ? '👨' : form.gender === 'female' ? '👩' : '👤' }}</span>
            </div>
            <div class="avatar-actions">
              <input
                ref="avatarFileInput"
                type="file"
                accept="image/*"
                class="file-input"
                @change="handleAvatarChange"
              />
              <button type="button" class="upload-button" @click="() => avatarFileInput?.click()">
                {{ avatarPreview ? '更换头像' : '上传头像' }}
              </button>
              <span v-if="uploadProgress" class="uploading">上传中...</span>
            </div>
          </div>
        </div>
        <div class="form-group">
          <label>过敏原</label>
          <div class="tag-input">
            <span v-for="item in form.allergens" :key="item" class="tag danger removable">
              {{ item }}
              <button type="button" @click="removeTag(form.allergens ?? [], item)">×</button>
            </span>
            <input v-model="allergenInput" type="text" class="input-sm" placeholder="输入后按添加" />
            <button type="button" class="small-button" @click="addAllergen">添加</button>
          </div>
        </div>
        <div class="form-group">
          <label>饮食限制</label>
          <div class="tag-input">
            <span v-for="item in form.dietaryRestrictions" :key="item" class="tag warning removable">
              {{ item }}
              <button type="button" @click="removeTag(form.dietaryRestrictions ?? [], item)">×</button>
            </span>
            <input v-model="restrictionInput" type="text" class="input-sm" placeholder="输入后按添加" />
            <button type="button" class="small-button" @click="addRestriction">添加</button>
          </div>
        </div>
        <div class="form-group">
          <label>偏好</label>
          <div class="tag-input">
            <span v-for="item in form.preferences" :key="item" class="tag info removable">
              {{ item }}
              <button type="button" @click="removeTag(form.preferences ?? [], item)">×</button>
            </span>
            <input v-model="preferenceInput" type="text" class="input-sm" placeholder="输入后按添加" />
            <button type="button" class="small-button" @click="addPreference">添加</button>
          </div>
        </div>
        <div class="modal-actions">
          <button class="text-button" @click="showForm = false">取消</button>
          <button class="primary-button" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.members-page {
  padding: 16px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.members-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.member-card {
  background: var(--card-soft);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}
.member-header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.member-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}
.member-avatar-placeholder {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.member-details {
  flex: 1;
}
.member-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}
.member-meta {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
.member-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
}
.tag.danger {
  background: #ffe5e5;
  color: #c62828;
}
.tag.warning {
  background: #fff8e1;
  color: #f57c00;
}
.tag.info {
  background: #e3f2fd;
  color: #1976d2;
}
.tag.removable {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.tag.removable button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: inherit;
  font-size: 14px;
}
.member-actions {
  display: flex;
  gap: 8px;
}
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}
.modal {
  background: var(--bg-primary);
  border-radius: 16px;
  padding: 24px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}
.form-group {
  margin-bottom: 16px;
}
.form-group label {
  display: block;
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--text-secondary);
  border-radius: 8px;
  font-size: 14px;
  background: var(--bg-primary);
  color: var(--text-primary);
}
.input-sm {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid var(--text-secondary);
  border-radius: 6px;
  font-size: 13px;
  background: var(--bg-primary);
  color: var(--text-primary);
}
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}
.avatar-preview {
  position: relative;
  width: 96px;
  height: 96px;
}
.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--accent-primary);
}
.remove-avatar {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #ff4444;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-placeholder {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  border: 2px dashed var(--text-secondary);
}
.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
}
.file-input {
  display: none;
}
.upload-button {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid var(--text-secondary);
  background: var(--bg-secondary);
  color: var(--text-primary);
  cursor: pointer;
  font-size: 14px;
}
.upload-button:hover {
  background: var(--accent-primary);
  color: white;
}
.uploading {
  font-size: 12px;
  color: var(--text-secondary);
}
.tag-input {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}
.small-button {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid var(--text-secondary);
  background: var(--bg-secondary);
  color: var(--text-primary);
  cursor: pointer;
  font-size: 13px;
}
.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
.primary-button {
  background: var(--accent-primary);
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}
.text-button {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
}
.text-button.danger {
  color: #c62828;
}
</style>
