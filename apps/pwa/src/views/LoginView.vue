<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { authState, loginWithPassword } from '../auth';

const router = useRouter();
const form = reactive({
  username: '',
  password: '',
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
  <div class="login-page">
    <div class="login-card">
      <h1 class="brand">宝贝早餐</h1>

      <div class="form-fields">
        <input
          v-model="form.username"
          type="text"
          placeholder="用户名"
          autocomplete="username"
          class="field"
        />
        <input
          v-model="form.password"
          type="password"
          placeholder="密码"
          autocomplete="current-password"
          class="field"
          @keyup.enter="submit"
        />
        <button
          class="btn-login"
          :disabled="authState.state.loading"
          @click="submit"
        >
          {{ authState.state.loading ? '登录中...' : '登 录' }}
        </button>
      </div>

      <p class="demo-hint">演示账号：lingyun / baby_breakfast</p>

      <p v-if="authState.state.error" class="error-text">{{ authState.state.error }}</p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #FFF8F0 0%, #FFE8D0 100%);
}

.login-card {
  width: 400px;
  padding: 48px 40px 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(255, 159, 67, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.brand {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: 4px;
  margin-bottom: 36px;
}

.form-fields {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.field {
  width: 100%;
  height: 46px;
  border: 1.5px solid #eee;
  border-radius: 10px;
  padding: 0 18px;
  font-size: 15px;
  color: #333;
  outline: none;
  transition: all 0.25s ease;
  background: #fafafa;
}

.field:focus {
  border-color: #FF9F43;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(255, 159, 67, 0.08);
}

.field::placeholder {
  color: #bbb;
}

.btn-login {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #FF9F43 0%, #FF6B35 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  cursor: pointer;
  margin-top: 6px;
  transition: all 0.3s ease;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(255, 107, 53, 0.35);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.demo-hint {
  margin-top: 24px;
  font-size: 13px;
  color: #aaa;
  text-align: center;
}

.error-text {
  margin-top: 12px;
  color: #d14343;
  font-size: 13px;
  text-align: center;
}
</style>
