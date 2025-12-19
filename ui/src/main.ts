import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import router from './router';
import Notify from './plugins/notify'

// Arco Design
import ArcoVue from '@arco-design/web-vue';
import '@arco-design/web-vue/dist/arco.css';

const app = createApp(App);
app.use(router);
app.use(Notify);
app.use(ArcoVue);
app.mount('#app').$nextTick(() => {
  // Use contextBridge
  if (window.ipcRenderer) {
    window.ipcRenderer.on('main-process-message', (_event, message) => {
      console.log(message);
    });
  }
});
