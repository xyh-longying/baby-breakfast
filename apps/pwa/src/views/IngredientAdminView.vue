<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import type { IngredientCategoryDTO, Ingredient } from '@baby-breakfast/shared';
import { getIngredientCategories, createIngredientCategory, updateIngredientCategory, deleteIngredientCategory } from '../api/ingredient-category';
import { getIngredients, createIngredient, updateIngredient, deleteIngredient } from '../api/ingredient';

const categories = ref<IngredientCategoryDTO[]>([]);
const ingredients = ref<Ingredient[]>([]);
const selectedCategory = ref<string | null>(null);

const showCategoryForm = ref(false);
const categoryForm = ref<any>({
  name: '',
  code: '',
  parentCode: null,
  sortOrder: 99,
  icon: '',
  color: '#27AE60',
  aliases: [],
});

const showIngredientForm = ref(false);
const ingredientForm = ref<any>({
  name: '',
  categoryCode: 'grain',
  unit: 'g',
  density: 1.0,
  imageUrl: null,
  aliases: [],
  tags: [],
});

const aliasInput = ref('');
const tagInput = ref('');

onMounted(async () => {
  await Promise.all([loadCategories(), loadIngredients()]);
});

async function loadCategories() {
  categories.value = await getIngredientCategories();
}

async function loadIngredients() {
  ingredients.value = await getIngredients(selectedCategory.value || undefined);
}

function startCreateCategory() {
  categoryForm.value = {
    name: '',
    code: '',
    parentCode: null,
    sortOrder: 99,
    icon: '',
    color: '#27AE60',
    aliases: [],
  };
  showCategoryForm.value = true;
}

function startEditCategory(cat: IngredientCategoryDTO) {
  categoryForm.value = {
    ...cat,
    code: cat.code,
  };
  showCategoryForm.value = true;
}

async function handleSaveCategory() {
  if (categoryForm.value.code && categoryForm.value.name) {
    if (categories.value.find(c => c.code === categoryForm.value.code) && !categoryForm.value.code) {
      alert('分类 CODE 已存在');
      return;
    }
    await updateIngredientCategory(categoryForm.value.code!, categoryForm.value);
  } else if (categoryForm.value.name && categoryForm.value.code) {
    await createIngredientCategory(categoryForm.value);
  }
  showCategoryForm.value = false;
  await loadCategories();
}

async function handleDeleteCategory(code: string) {
  if (confirm(`确定删除分类"${code}"吗？`)) {
    await deleteIngredientCategory(code);
    await loadCategories();
  }
}

function startCreateIngredient() {
  ingredientForm.value = {
    name: '',
    categoryCode: categories.value[0]?.code || 'grain',
    unit: 'g',
    density: 1.0,
    imageUrl: null,
    aliases: [],
    tags: [],
  };
  showIngredientForm.value = true;
}

function startEditIngredient(ing: Ingredient) {
  ingredientForm.value = {
    ...ing,
    id: ing.id,
  };
  showIngredientForm.value = true;
}

async function handleSaveIngredient() {
  if (ingredientForm.value.id) {
    await updateIngredient(ingredientForm.value.id, ingredientForm.value);
  } else {
    await createIngredient(ingredientForm.value);
  }
  showIngredientForm.value = false;
  await loadIngredients();
}

async function handleDeleteIngredient(id: number) {
  if (confirm(`确定删除食材"${ingredientForm.value.name}"吗？`)) {
    await deleteIngredient(id);
    await loadIngredients();
  }
}

function addAlias() {
  if (aliasInput.value.trim() && !ingredientForm.value.aliases?.includes(aliasInput.value.trim())) {
    ingredientForm.value.aliases = [...(ingredientForm.value.aliases ?? []), aliasInput.value.trim()];
    aliasInput.value = '';
  }
}

function addTag() {
  if (tagInput.value.trim() && !ingredientForm.value.tags?.includes(tagInput.value.trim())) {
    ingredientForm.value.tags = [...(ingredientForm.value.tags ?? []), tagInput.value.trim()];
    tagInput.value = '';
  }
}

function removeTag(list: string[], item: string) {
  const idx = list.indexOf(item);
  if (idx > -1) list.splice(idx, 1);
}

const filteredIngredients = computed(() => {
  if (!selectedCategory.value) return ingredients.value;
  return ingredients.value.filter(ing => ing.categoryCode === selectedCategory.value);
});
</script>

<template>
  <div class="ingredient-admin-page">
    <header class="page-header">
      <h2>食材管理</h2>
    </header>

    <div class="admin-section">
      <div class="section-header">
        <h3>食材类别</h3>
        <button class="primary-button" @click="startCreateCategory">新增类别</button>
      </div>

      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.code" class="category-card" :style="{ borderLeftColor: cat.color }">
          <div class="category-header">
            <span class="category-icon">{{ cat.icon }}</span>
            <div class="category-info">
              <div class="category-name">{{ cat.name }}</div>
              <div class="category-code">{{ cat.code }}</div>
            </div>
          </div>
          <div class="category-actions">
            <button class="text-button" @click="startEditCategory(cat)">编辑</button>
            <button class="text-button danger" @click="handleDeleteCategory(cat.code)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <div class="admin-section">
      <div class="section-header">
        <h3>食材列表</h3>
        <div class="section-actions">
          <select v-model="selectedCategory" class="filter-select" @change="loadIngredients">
            <option value="">全部分类</option>
            <option v-for="cat in categories" :key="cat.code" :value="cat.code">{{ cat.name }}</option>
          </select>
          <button class="primary-button" @click="startCreateIngredient">新增食材</button>
        </div>
      </div>

      <div class="ingredient-list">
        <div v-for="ing in filteredIngredients" :key="ing.id" class="ingredient-card">
          <div class="ingredient-header">
            <div class="ingredient-name">{{ ing.name }}</div>
            <div class="ingredient-meta">{{ ing.unit }} · 密度：{{ ing.density }}</div>
          </div>
          <div class="ingredient-tags">
            <span class="tag">{{ ing.categoryCode }}</span>
            <span v-for="tag in ing.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="ingredient-actions">
            <button class="text-button" @click="startEditIngredient(ing)">编辑</button>
            <button class="text-button danger" @click="handleDeleteIngredient(ing.id)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showCategoryForm" class="modal-overlay" @click.self="showCategoryForm = false">
      <div class="modal">
        <h3>{{ categoryForm.code ? '编辑类别' : '新增类别' }}</h3>
        <div class="form-group">
          <label>名称</label>
          <input v-model="categoryForm.name" type="text" class="input" />
        </div>
        <div class="form-group">
          <label>CODE</label>
          <input v-model="categoryForm.code" type="text" class="input" :disabled="!!categoryForm.code" />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>图标</label>
            <input v-model="categoryForm.icon" type="text" class="input" placeholder="如：🥬" />
          </div>
          <div class="form-group">
            <label>颜色</label>
            <input v-model="categoryForm.color" type="color" class="input input-color" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>排序</label>
            <input v-model.number="categoryForm.sortOrder" type="number" class="input" />
          </div>
          <div class="form-group">
            <label>父分类 CODE</label>
            <input v-model="categoryForm.parentCode" type="text" class="input" placeholder="可选" />
          </div>
        </div>
        <div class="modal-actions">
          <button class="text-button" @click="showCategoryForm = false">取消</button>
          <button class="primary-button" @click="handleSaveCategory">保存</button>
        </div>
      </div>
    </div>

    <div v-if="showIngredientForm" class="modal-overlay" @click.self="showIngredientForm = false">
      <div class="modal">
        <h3>{{ ingredientForm.id ? '编辑食材' : '新增食材' }}</h3>
        <div class="form-row">
          <div class="form-group">
            <label>名称</label>
            <input v-model="ingredientForm.name" type="text" class="input" />
          </div>
          <div class="form-group">
            <label>分类</label>
            <select v-model="ingredientForm.categoryCode" class="input">
              <option v-for="cat in categories" :key="cat.code" :value="cat.code">{{ cat.name }}</option>
            </select>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>单位</label>
            <input v-model="ingredientForm.unit" type="text" class="input" placeholder="g, ml, 个" />
          </div>
          <div class="form-group">
            <label>密度</label>
            <input v-model.number="ingredientForm.density" type="number" step="0.01" class="input" />
          </div>
        </div>
        <div class="form-group">
          <label>别名</label>
          <div class="tag-input">
            <span v-for="item in ingredientForm.aliases" :key="item" class="tag removable">
              {{ item }}
              <button type="button" @click="removeTag(ingredientForm.aliases ?? [], item)">×</button>
            </span>
            <input v-model="aliasInput" type="text" class="input-sm" placeholder="输入别名" @keyup.enter="addAlias" />
            <button type="button" class="small-button" @click="addAlias">添加</button>
          </div>
        </div>
        <div class="form-group">
          <label>标签</label>
          <div class="tag-input">
            <span v-for="item in ingredientForm.tags" :key="item" class="tag info removable">
              {{ item }}
              <button type="button" @click="removeTag(ingredientForm.tags ?? [], item)">×</button>
            </span>
            <input v-model="tagInput" type="text" class="input-sm" placeholder="输入标签" @keyup.enter="addTag" />
            <button type="button" class="small-button" @click="addTag">添加</button>
          </div>
        </div>
        <div class="modal-actions">
          <button class="text-button" @click="showIngredientForm = false">取消</button>
          <button class="primary-button" @click="handleSaveIngredient">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ingredient-admin-page {
  padding: 16px;
}
.page-header {
  margin-bottom: 24px;
}
.admin-section {
  margin-bottom: 32px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.section-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}
.category-card {
  background: var(--card-soft);
  border-radius: 12px;
  padding: 16px;
  border-left: 4px solid var(--accent-primary);
}
.category-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.category-icon {
  font-size: 32px;
}
.category-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}
.category-code {
  font-size: 12px;
  color: var(--text-secondary);
}
.category-actions {
  display: flex;
  gap: 8px;
}
.ingredient-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
}
.ingredient-card {
  background: var(--card-soft);
  border-radius: 12px;
  padding: 16px;
}
.ingredient-header {
  margin-bottom: 12px;
}
.ingredient-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}
.ingredient-meta {
  font-size: 13px;
  color: var(--text-secondary);
}
.ingredient-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}
.ingredient-actions {
  display: flex;
  gap: 8px;
}
.filter-select {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid var(--text-secondary);
  background: var(--bg-primary);
  color: var(--text-primary);
  font-size: 14px;
}
input.input-color {
  height: 40px;
  padding: 4px;
}
</style>
