<script setup lang="ts">
import { computed } from 'vue';
import { authState, logout, switchCurrentRole } from './auth';

const theme = computed(() => authState.theme.value);
const session = computed(() => authState.state.session);
const isLoggedIn = computed(() => !!session.value);

async function onRoleChange(event: Event) {
  const nextRole = (event.target as HTMLSelectElement).value as 'parent' | 'child';
  const targetMember = session.value?.currentFamily.members.find((member) => member.role === nextRole);
  if (!targetMember || !session.value) {
    return;
  }

  await switchCurrentRole(targetMember.id, targetMember.role);
}
</script>

<template>
  <div class="app-shell" :class="{ 'login-mode': !isLoggedIn }" :style="theme.cssVars">
    <header v-if="isLoggedIn" class="topbar">
      <div>
        <h1>宝贝早餐</h1>
        <p v-if="session" class="subtitle">
          {{ session.user.displayName }} · {{ session.currentFamily.name }} · 当前成员 {{ session.currentMember.name }}
        </p>
      </div>
      <div class="topbar-actions">
        <label class="role-switcher">
          <span>当前模式</span>
          <select :value="session?.currentMember.role" @change="onRoleChange">
            <option
              v-for="member in session?.currentFamily.members ?? []"
              :key="member.id"
              :value="member.role"
            >
              {{ member.role === 'parent' ? '家长模式' : '儿童模式' }}
            </option>
          </select>
        </label>
        <button class="text-button" @click="logout">退出</button>
      </div>
    </header>

    <nav v-if="isLoggedIn" class="tabs">
      <RouterLink to="/">今日早餐</RouterLink>
      <RouterLink to="/plan">一周计划</RouterLink>
      <RouterLink to="/members">家庭成员</RouterLink>
      <RouterLink to="/admin" class="admin-tab">管理后台</RouterLink>
    </nav>

    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.admin-tab {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 4px 12px;
  font-size: 13px;
}
.admin-tab.router-link-active {
  background: var(--accent-primary);
  color: white;
}
</style>
