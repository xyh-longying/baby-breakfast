<script setup lang="ts">
const dishes = [
  { name: '南瓜小米粥', duration: '25 分钟' },
  { name: '鸡蛋卷', duration: '10 分钟' },
  { name: '蓝莓酸奶杯', duration: '5 分钟' },
];
</script>

<template>
  <section class="panel hero">
    <div>
      <p class="eyebrow">今日早餐</p>
      <h2>温暖饱腹的周一早餐</h2>
      <p>适合凉爽天气，兼顾蛋白质和碳水，准备时间控制在 30 分钟内。</p>
    </div>
    <button class="primary-button">开始做早餐</button>
  </section>

  <section class="grid two-col">
    <article class="panel">
      <h3>菜品列表</h3>
      <ul class="dish-list">
        <li v-for="dish in dishes" :key="dish.name">
          <strong>{{ dish.name }}</strong>
          <span>{{ dish.duration }}</span>
        </li>
      </ul>
    </article>

    <article class="panel accent-card">
      <h3>AI 小助手</h3>
      <p>支持换方案、做菜求助、采购提醒和儿童偏好建议。</p>
      <button class="secondary-button">询问 AI</button>
    </article>
  </section>
</template>
