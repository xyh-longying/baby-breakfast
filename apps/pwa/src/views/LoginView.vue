<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { authState, loginWithPassword } from '../auth';

const router = useRouter();
const form = reactive({
  username: 'lingyun',
  password: 'baby-breakfast',
});

async function submit() {
  try {
    await loginWithPassword(form.username, form.password);
    await router.push('/');
  } catch {
    return;
  }
}
</script>

<template>
  <section class="login-shell">
    <article class="panel login-card">
      <p class="eyebrow">欢迎回来</p>
      <h2>登录宝贝早餐</h2>
      <p>首版默认演示账号已预置，后续会接入正式用户体系。</p>
      <label>
        <span>用户名</span>
        <input v-model="form.username" type="text" autocomplete="username" />
      </label>
      <label>
        <span>密码</span>
        <input v-model="form.password" type="password" autocomplete="current-password" />
      </label>
      <button class="primary-button" :disabled="authState.state.loading" @click="submit">
        {{ authState.state.loading ? '登录中...' : '登录' }}
      </button>
      <p v-if="authState.state.error" class="error-text">{{ authState.state.error }}</p>
    </article>
  </section>
</template>
