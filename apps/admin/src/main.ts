import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import App from './App.vue';
import AdminLayout from './views/AdminLayout.vue';
import LoginView from './views/LoginView.vue';
import DashboardView from './views/DashboardView.vue';
import TodayRecipeView from './views/recipe/TodayRecipeView.vue';
import QuickActionView from './views/dashboard/QuickActionView.vue';
import WeeklyRecipeView from './views/recipe/WeeklyRecipeView.vue';
import RecipeGenerateView from './views/recipe/RecipeGenerateView.vue';
import RecipeHistoryView from './views/recipe/RecipeHistoryView.vue';
import DishListView from './views/dish/DishListView.vue';
import DishCategoryView from './views/dish/DishCategoryView.vue';
import RecipeManageView from './views/dish/RecipeManageView.vue';
import IngredientListView from './views/ingredient/IngredientListView.vue';
import IngredientCategoryView from './views/ingredient/IngredientCategoryView.vue';
import InventoryView from './views/ingredient/InventoryView.vue';
import MemberListView from './views/member/MemberListView.vue';
import MemberDietaryView from './views/member/MemberDietaryView.vue';
import MemberPreferenceView from './views/member/MemberPreferenceView.vue';
import AIModelView from './views/ai/AIModelView.vue';
import AIPromptView from './views/ai/AIPromptView.vue';
import AIRulesView from './views/ai/AIRulesView.vue';
import ReportNutritionView from './views/report/ReportNutritionView.vue';
import ReportConsumptionView from './views/report/ReportConsumptionView.vue';
import ReportPreferenceView from './views/report/ReportPreferenceView.vue';
import SystemHolidayView from './views/system/SystemHolidayView.vue';
import SystemParamsView from './views/system/SystemParamsView.vue';
import SystemBackupView from './views/system/SystemBackupView.vue';

const routes = [
  {
    path: '/login',
    component: LoginView,
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: { name: 'Dashboard' },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: DashboardView, meta: { title: '数据概览' } },
      { path: 'dashboard/today', name: 'TodayRecipe', component: TodayRecipeView, meta: { title: '今日食谱' } },
      { path: 'dashboard/quick', name: 'QuickAction', component: QuickActionView, meta: { title: '快速操作' } },
      { path: 'recipe/weekly', name: 'WeeklyRecipe', component: WeeklyRecipeView, meta: { title: '一周食谱' } },
      { path: 'recipe/generate', name: 'RecipeGenerate', component: RecipeGenerateView, meta: { title: '食谱生成' } },
      { path: 'recipe/history', name: 'RecipeHistory', component: RecipeHistoryView, meta: { title: '食谱记录' } },
      { path: 'dish/list', name: 'DishList', component: DishListView, meta: { title: '菜品列表' } },
      { path: 'dish/category', name: 'DishCategory', component: DishCategoryView, meta: { title: '菜品分类' } },
      { path: 'dish/recipe', name: 'RecipeManage', component: RecipeManageView, meta: { title: '菜谱管理' } },
      { path: 'ingredient/list', name: 'IngredientList', component: IngredientListView, meta: { title: '食材列表' } },
      { path: 'ingredient/category', name: 'IngredientCategory', component: IngredientCategoryView, meta: { title: '食材分类' } },
      { path: 'ingredient/inventory', name: 'Inventory', component: InventoryView, meta: { title: '库存管理' } },
      { path: 'member/list', name: 'MemberList', component: MemberListView, meta: { title: '成员管理' } },
      { path: 'member/dietary', name: 'MemberDietary', component: MemberDietaryView, meta: { title: '过敏忌口' } },
      { path: 'member/preference', name: 'MemberPreference', component: MemberPreferenceView, meta: { title: '口味偏好' } },
      { path: 'ai/model', name: 'AIModel', component: AIModelView, meta: { title: '模型配置' } },
      { path: 'ai/prompt', name: 'AIPrompt', component: AIPromptView, meta: { title: 'Prompt 模板' } },
      { path: 'ai/rules', name: 'AIRules', component: AIRulesView, meta: { title: '推荐规则' } },
      { path: 'report/nutrition', name: 'ReportNutrition', component: ReportNutritionView, meta: { title: '营养分析' } },
      { path: 'report/consumption', name: 'ReportConsumption', component: ReportConsumptionView, meta: { title: '消耗统计' } },
      { path: 'report/preference', name: 'ReportPreference', component: ReportPreferenceView, meta: { title: '偏好分析' } },
      { path: 'system/holiday', name: 'SystemHoliday', component: SystemHolidayView, meta: { title: '节假日配置' } },
      { path: 'system/params', name: 'SystemParams', component: SystemParamsView, meta: { title: '系统参数' } },
      { path: 'system/backup', name: 'SystemBackup', component: SystemBackupView, meta: { title: '数据备份' } },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/login' && !token) {
    next('/login');
  } else if (to.path === '/login' && token) {
    next('/dashboard');
  } else {
    next();
  }
});

const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
app.use(router).use(ElementPlus).mount('#app');
