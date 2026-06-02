<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';

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
    <div class="login-box">
      <div class="brand">
        <div class="brand-icon">🍳</div>
        <h1 class="brand-title">宝贝早餐</h1>
        <p class="brand-subtitle">温馨早餐 · 从爱开始</p>
      </div>

      <div class="form-section">
        <h2 class="form-title">欢迎回来</h2>

        <el-form :model="loginForm" label-width="0" class="login-form">
          <el-form-item class="form-item">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item class="form-item">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item class="form-item">
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="submit-btn"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="demo-account">
          <span class="demo-label">演示账号：</span>
          <code>lingyun</code>
          <span class="divider">/</span>
          <code>baby-breakfast</code>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #FFF5EB 0%, #FFE8D0 100%);
}

.login-box {
  width: 520px;
  padding: 48px 56px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 16px 48px rgba(255, 159, 67, 0.12);
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.brand {
  text-align: center;
  margin-bottom: 40px;
}

.brand-icon {
  font-size: 64px;
  margin-bottom: 12px;
  display: block;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  color: #1A1A1A;
  margin-bottom: 6px;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 13px;
  color: #999;
  letter-spacing: 3px;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-title {
  font-size: 20px;
  font-weight: 600;
  color: #1A1A1A;
  text-align: center;
  margin-bottom: 8px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  margin-bottom: 0;
}

:deep(.el-input__wrapper) {
  background: #FAFAFA;
  border: 2px solid #F0F0F0;
  border-radius: 10px;
  padding: 12px 16px;
  box-shadow: none;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  border-color: #FFE0B2;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #FF9F43;
  box-shadow: 0 0 0 3px rgba(255, 159, 67, 0.08);
}

:deep(.el-input__inner) {
  font-size: 15px;
  color: #1A1A1A;
}

:deep(.el-input__inner::placeholder) {
  color: #CCC;
}

:deep(.el-input__prefix) {
  margin-right: 8px;
  color: #FF9F43;
}

.submit-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #FF9F43 0%, #FF8E2E 100%);
  border: none;
  border-radius: 10px;
  box-shadow: 0 6px 20px rgba(255, 159, 67, 0.3);
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 159, 67, 0.4);
}

.demo-account {
  margin-top: 16px;
  padding: 16px;
  background: #FFF8F0;
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
}

.demo-label {
  color: #999;
  margin-right: 8px;
}

.demo-account code {
  font-family: 'SF Mono', 'Monaco', monospace;
  color: #5D4037;
  background: #FFF;
  padding: 4px 10px;
  border-radius: 6px;
  border: 1px solid #FFE0B2;
}

.divider {
  color: #DDD;
  margin: 0 4px;
}

@media (max-width: 768px) {
  .login-box {
    width: 90%;
    padding: 40px 32px;
  }
}
</style>
