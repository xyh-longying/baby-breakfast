<script setup lang="ts">
const plans = [
  { day: '周一', meal: '小米粥 + 鸡蛋卷 + 香蕉牛奶' },
  { day: '周二', meal: '番茄鸡蛋面 + 玉米 + 豆浆' },
  { day: '周三', meal: '虾仁蔬菜粥 + 奶黄包 + 苹果片' },
  { day: '周四', meal: '鸡肉三明治 + 南瓜汤 + 奇异果' },
  { day: '周五', meal: '鲜肉小馄饨 + 蒸红薯 + 酸奶' },
];
</script>

<template>
  <section class="panel hero compact">
    <div>
      <p class="eyebrow">一周计划</p>
      <h2>工作日智能早餐方案</h2>
    </div>
    <button class="primary-button">生成新计划</button>
  </section>

  <section class="panel">
    <ul class="plan-list">
      <li v-for="item in plans" :key="item.day">
        <span>{{ item.day }}</span>
        <strong>{{ item.meal }}</strong>
        <button class="text-button">替换</button>
      </li>
    </ul>
  </section>
</template>
