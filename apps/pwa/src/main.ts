import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { bootstrapAuth } from './auth';
import './styles.css';

bootstrapAuth().finally(() => {
  createApp(App).use(router).mount('#app');
});
