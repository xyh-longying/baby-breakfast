<script setup lang="ts">
import { RouterLink } from 'vue-router';

const sections = [
  { path: '/admin/ingredients', name: '食材管理', icon: '🥬', desc: '食材类别、基础食材维护' },
  { path: '/admin/dishes', name: '菜品中心', icon: '🍳', desc: '菜谱、菜品管理' },
  { path: '/admin/inventory', name: '库存管理', icon: '📦', desc: '库存维护、自动扣减' },
  { path: '/admin/meal-plan', name: '早餐方案', icon: '📅', desc: '周计划、AI 推荐' },
];
</script>

<template>
  <div class="admin-page">
    <header class="page-header">
      <h2>管理后台</h2>
      <p class="page-desc">食材、菜品、库存、早餐方案管理</p>
    </header>

    <div class="admin-grid">
      <RouterLink v-for="section in sections" :key="section.path" :to="section.path" class="admin-card">
        <div class="admin-icon">{{ section.icon }}</div>
        <h3>{{ section.name }}</h3>
        <p class="admin-desc">{{ section.desc }}</p>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  padding: 16px;
}
.page-header {
  margin-bottom: 24px;
}
.page-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 8px;
}
.admin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.admin-card {
  background: var(--card-soft);
  border-radius: 16px;
  padding: 24px;
  text-decoration: none;
  color: var(--text-primary);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}
.admin-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.admin-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.admin-card h3 {
  font-size: 18px;
  margin-bottom: 8px;
}
.admin-desc {
  font-size: 14px;
  color: var(--text-secondary);
}
</style>
