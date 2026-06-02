<script setup lang="ts">
import { ref, h, computed } from 'vue';
import {
  Menu as MenuIcon,
  Dish as DishIcon,
  Box as BoxIcon,
  Calendar as CalendarIcon,
  Setting as SettingIcon,
  User as UserIcon,
  House as HomeIcon,
  SwitchButton as LogoutIcon,
  Document as DocumentIcon,
  List as ListIcon,
  Folder as FolderIcon,
  ShoppingCart as CartIcon,
  TrendCharts as ChartIcon,
  Cpu as CpuIcon,
  Notebook as NotebookIcon,
  Timer as TimerIcon,
  CopyDocument as CopyIcon,
  DataAnalysis as AnalysisIcon,
  Download as DownloadIcon,
  Warning as WarningIcon,
  QuestionFilled as QuestionIcon,
} from '@element-plus/icons-vue';
import type { Component } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox } from 'element-plus';

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
      <div class="logo">
        <span class="logo-icon">🍳</span>
        <span class="logo-text">宝贝早餐管理后台</span>
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
        <div class="header-right">
          <div class="user-info">
            <el-avatar :size="36" :icon="UserIcon" />
            <div class="user-details">
              <div class="user-name">{{ session?.user?.displayName || '管理员' }}</div>
              <div class="user-family">{{ session?.currentFamily?.name || '未选择家庭' }}</div>
            </div>
          </div>
          <el-divider direction="vertical" />
          <el-button :icon="LogoutIcon" circle @click="handleLogout" title="退出登录" />
        </div>
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
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.logo-icon {
  font-size: 32px;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
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
  background: #FFF8F0;
  border-bottom: 1px solid #FFE5D0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #5D4037;
}

.user-family {
  font-size: 12px;
  color: #9E9E9E;
}

.main-content {
  background: #FFF8F0;
  padding: 24px;
}
</style>
