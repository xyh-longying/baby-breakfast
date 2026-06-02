import { createRouter, createWebHistory } from 'vue-router';
import HomeView from './views/HomeView.vue';
import PlanView from './views/PlanView.vue';
import LoginView from './views/LoginView.vue';
import MembersView from './views/MembersView.vue';
import AdminView from './views/AdminView.vue';
import IngredientAdminView from './views/IngredientAdminView.vue';
import { authState } from './auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView },
    { path: '/', component: HomeView },
    { path: '/plan', component: PlanView },
    { path: '/members', component: MembersView },
    { path: '/admin', component: AdminView },
    { path: '/admin/ingredients', component: IngredientAdminView },
  ],
});

router.beforeEach((to) => {
  if (!authState.state.ready) {
    return true;
  }

  if (!authState.isAuthenticated.value && to.path !== '/login') {
    return '/login';
  }

  if (authState.isAuthenticated.value && to.path === '/login') {
    return '/';
  }

  return true;
});

export default router;
