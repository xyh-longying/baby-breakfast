<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const loginForm = ref({
  username: '',
  password: '',
});

async function handleLogin() {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }

  loading.value = true;
  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(loginForm.value),
    });
    const data = await response.json();

    if (data.code === 0) {
      localStorage.setItem('token', data.data.token);
      localStorage.setItem('session', JSON.stringify(data.data));
      ElMessage.success('登录成功');
      router.push('/dashboard');
    } else {
      ElMessage.error(data.message || '登录失败');
    }
  } catch (error) {
    ElMessage.error('登录失败，请检查网络连接');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand-area">
          <h1 class="brand-name">宝贝早餐</h1>
          <p class="brand-desc">温馨早餐 · 从爱开始</p>
        </div>
        <div class="decoration-circle c1"></div>
        <div class="decoration-circle c2"></div>
        <div class="decoration-circle c3"></div>
      </div>

      <div class="login-right">
        <div class="login-form-wrapper">
          <h2 class="form-heading">欢迎回来</h2>
          <p class="form-subheading">请登录您的管理员账户</p>

          <div class="input-group">
            <input
              v-model="loginForm.username"
              type="text"
              placeholder="用户名"
              class="input-field"
              @keyup.enter="handleLogin"
            />
            <input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              class="input-field"
              @keyup.enter="handleLogin"
            />
            <button
              class="submit-btn"
              :class="{ loading }"
              :disabled="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.login-container {
  display: flex;
  width: 100%;
  height: 100%;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #FF9F43 0%, #FF6B35 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-area {
  text-align: center;
  z-index: 1;
}

.brand-name {
  font-size: 42px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 8px;
  margin-bottom: 12px;
}

.brand-desc {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.75);
  letter-spacing: 4px;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
}

.c1 {
  width: 400px;
  height: 400px;
  top: -120px;
  right: -80px;
}

.c2 {
  width: 280px;
  height: 280px;
  bottom: -60px;
  left: -40px;
}

.c3 {
  width: 160px;
  height: 160px;
  top: 50%;
  left: 30%;
  transform: translateY(-50%);
}

.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.login-form-wrapper {
  width: 320px;
  padding: 0 20px;
}

.form-heading {
  font-size: 26px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.form-subheading {
  font-size: 14px;
  color: #999;
  margin-bottom: 40px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-field {
  width: 100%;
  height: 48px;
  border: 1.5px solid #e8e8e8;
  border-radius: 10px;
  padding: 0 18px;
  font-size: 15px;
  color: #333;
  outline: none;
  transition: all 0.25s ease;
  background: #fafafa;
}

.input-field:focus {
  border-color: #FF9F43;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(255, 159, 67, 0.08);
}

.input-field::placeholder {
  color: #bbb;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #FF9F43 0%, #FF6B35 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.submit-btn:hover:not(.loading) {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(255, 107, 53, 0.35);
}

.submit-btn.loading {
  opacity: 0.7;
  cursor: not-allowed;
}

@media (max-width: 900px) {
  .login-left {
    display: none;
  }
  .login-right {
    width: 100%;
  }
}
</style>
