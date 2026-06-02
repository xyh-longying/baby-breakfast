<script setup lang="ts">
import { ref, h, computed } from 'vue';
import {
  Menu as MenuIcon,
  Dish as DishIcon,
  Box as BoxIcon,
  Setting as SettingIcon,
  User as UserIcon,
  House as HomeIcon,
  SwitchButton as LogoutIcon,
  Document as DocumentIcon,
  TrendCharts as ChartIcon,
  Cpu as CpuIcon,
  DataAnalysis as AnalysisIcon,
} from '@element-plus/icons-vue';
import type { Component } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox, ElDropdownMenu, ElDropdownItem, ElDropdown } from 'element-plus';

interface SubMenuItem {
  path: string;
  label: string;
}

interface MenuItem {
  index: string;
  label: string;
  icon: Component;
  children?: SubMenuItem[];
}

const router = useRouter();
const route = useRoute();

const menuItems: MenuItem[] = [
  {
    index: 'dashboard',
    label: '仪表盘',
    icon: h(HomeIcon),
    children: [
      { path: '/dashboard', label: '数据概览' },
      { path: '/dashboard/today', label: '今日食谱' },
      { path: '/dashboard/quick', label: '快速操作' },
    ],
  },
  {
    index: 'recipe',
    label: '食谱管理',
    icon: h(DocumentIcon),
    children: [
      { path: '/recipe/weekly', label: '一周食谱' },
      { path: '/recipe/generate', label: '食谱生成' },
      { path: '/recipe/history', label: '食谱记录' },
    ],
  },
  {
    index: 'dish',
    label: '菜品管理',
    icon: h(DishIcon),
    children: [
      { path: '/dish/list', label: '菜品列表' },
      { path: '/dish/category', label: '菜品分类' },
      { path: '/dish/recipe', label: '菜谱管理' },
    ],
  },
  {
    index: 'ingredient',
    label: '食材管理',
    icon: h(BoxIcon),
    children: [
      { path: '/ingredient/list', label: '食材列表' },
      { path: '/ingredient/category', label: '食材分类' },
      { path: '/ingredient/inventory', label: '库存管理' },
    ],
  },
  {
    index: 'member',
    label: '家庭成员',
    icon: h(UserIcon),
    children: [
      { path: '/member/list', label: '成员管理' },
      { path: '/member/dietary', label: '过敏忌口' },
      { path: '/member/preference', label: '口味偏好' },
    ],
  },
  {
    index: 'ai',
    label: 'AI 配置',
    icon: h(CpuIcon),
    children: [
      { path: '/ai/model', label: '模型配置' },
      { path: '/ai/prompt', label: 'Prompt 模板' },
      { path: '/ai/rules', label: '推荐规则' },
    ],
  },
  {
    index: 'report',
    label: '数据报表',
    icon: h(ChartIcon),
    children: [
      { path: '/report/nutrition', label: '营养分析' },
      { path: '/report/consumption', label: '消耗统计' },
      { path: '/report/preference', label: '偏好分析' },
    ],
  },
  {
    index: 'system',
    label: '系统设置',
    icon: h(SettingIcon),
    children: [
      { path: '/system/holiday', label: '节假日配置' },
      { path: '/system/params', label: '系统参数' },
      { path: '/system/backup', label: '数据备份' },
    ],
  },
];

const activeMenu = computed(() => {
  const path = route.path;
  for (const item of menuItems) {
    if (path.startsWith(`/${item.index}`) || path === `/${item.index}`) {
      return item.index;
    }
  }
  return 'dashboard';
});

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('session');
    router.push('/login');
  });
}

function handleCommand(command: string) {
  if (command === 'logout') {
    handleLogout();
  }
}

const session = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('session') || '{}');
  } catch {
    return null;
  }
});
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="sidebar">
      <div class="logo-area">
        <!-- Logo 预留位置 -->
      </div>

      <el-menu
        :default-active="activeMenu"
        background-color="transparent"
        text-color="rgba(255, 255, 255, 0.85)"
        active-text-color="#fff"
        mode="vertical"
        router
      >
        <el-sub-menu
          v-for="item in menuItems"
          :key="item.index"
          :index="item.index"
        >
          <template #title>
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </template>
          <el-menu-item
            v-for="child in item.children"
            :key="child.path"
            :index="child.path"
          >
            {{ child.label }}
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title || route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <el-dropdown trigger="click" class="user-dropdown" @command="handleCommand">
          <div class="user-trigger">
            <el-avatar :size="32" :icon="UserIcon" />
            <span class="user-name">{{ session?.user?.displayName || '管理员' }}</span>
            <span class="family-name">{{ session?.currentFamily?.name || '' }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled class="user-profile-item">
                <div class="profile-info">
                  <div class="profile-name">{{ session?.user?.displayName || '管理员' }}</div>
                  <div class="profile-family">{{ session?.currentFamily?.name || '未选择家庭' }}</div>
                </div>
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon :size="14"><SwitchButton /></el-icon>
                退出系统
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background: linear-gradient(180deg, #FF9F43 0%, #FF8E2E 100%);
  overflow-y: auto;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.08);
}

.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.el-menu {
  border-right: none;
  background-color: transparent;
}

:deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.85);
  padding-left: 20px;
}

:deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.15) !important;
}

:deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.75);
  padding-left: 48px !important;
  font-size: 14px;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.25) !important;
  color: #fff !important;
}

.header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 56px;
}

.header-left {
  flex: 1;
}

.user-dropdown {
  cursor: pointer;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-trigger:hover {
  background: #f5f5f5;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.family-name {
  font-size: 12px;
  color: #999;
}

.profile-info {
  text-align: center;
  padding: 4px 0;
}

.profile-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.profile-family {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.user-profile-item {
  cursor: default;
}

.main-content {
  background: #f5f7fa;
  padding: 24px;
}
</style>
