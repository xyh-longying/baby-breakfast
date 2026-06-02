<template>
  <el-row :gutter="20">
    <el-col :span="6">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-label">食材总数</div>
          <div class="stat-value">128</div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-label">菜品总数</div>
          <div class="stat-value">56</div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-label">本周计划</div>
          <div class="stat-value">5 天</div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-label">家庭成员</div>
          <div class="stat-value">3 人</div>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <el-card class="welcome-card">
    <template #header>
      <div class="card-header">
        <span>欢迎使用宝贝早餐管理后台</span>
      </div>
    </template>
    <p>请选择左侧菜单进行食材、菜品、计划等管理操作。</p>
  </el-card>
</template>

<style scoped>
.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px 0;
}

.stat-label {
  color: #8D6E63;
  font-size: 14px;
}

.stat-value {
  color: #FF9F43;
  font-size: 28px;
  font-weight: bold;
  margin-top: 8px;
}

.welcome-card {
  margin-top: 20px;
}

.welcome-card p {
  color: #6D4C41;
  line-height: 1.6;
}
</style>
