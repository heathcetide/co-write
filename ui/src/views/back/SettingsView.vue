<script setup lang="ts">
import { ref } from 'vue';
import { Settings, Sun, Moon, Bell, Languages } from 'lucide-vue-next';

const isDarkMode = ref(false);
const notificationsEnabled = ref(true);
const language = ref('zh-CN');

const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value;
};

const toggleNotifications = () => {
  notificationsEnabled.value = !notificationsEnabled.value;
};

const changeLanguage = (e: Event) => {
  const target = e.target as HTMLSelectElement;
  language.value = target.value;
};
</script>

<template>
  <div class="settings-view">
    <div class="header">
      <Settings class="icon" />
      <h1 class="title">系统设置</h1>
    </div>
    <p class="subtitle">自定义您的偏好设置，让系统更贴合您的使用习惯。</p>

    <div class="setting-section">
      <!-- 主题切换 -->
      <div class="setting-card">
        <div class="setting-icon">
          <component :is="isDarkMode ? Moon : Sun" class="icon" />
        </div>
        <div class="setting-info">
          <h3>主题模式</h3>
          <p>{{ isDarkMode ? '深色模式' : '浅色模式' }}</p>
        </div>
        <button @click="toggleTheme">
          切换为{{ isDarkMode ? '浅色' : '深色' }}
        </button>
      </div>

      <!-- 通知开关 -->
      <div class="setting-card">
        <div class="setting-icon">
          <Bell class="icon" />
        </div>
        <div class="setting-info">
          <h3>通知提醒</h3>
          <p>{{ notificationsEnabled ? '已开启通知' : '通知已关闭' }}</p>
        </div>
        <button @click="toggleNotifications">
          {{ notificationsEnabled ? '关闭通知' : '开启通知' }}
        </button>
      </div>

      <!-- 语言设置 -->
      <div class="setting-card">
        <div class="setting-icon">
          <Languages class="icon" />
        </div>
        <div class="setting-info">
          <h3>界面语言</h3>
          <p>{{ language === 'zh-CN' ? '简体中文' : 'English' }}</p>
        </div>
        <select :value="language" @change="changeLanguage">
          <option value="zh-CN">简体中文</option>
          <option value="en-US">English</option>
        </select>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-view { padding: 24px; background-color: #ffffff; min-height: 100%; font-family: system-ui, sans-serif; }

.header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.icon { width: 20px; height: 20px; color: #2563eb; }

.title { font-size: 22px; font-weight: 700; color: #0f172a; }

.subtitle { font-size: 13px; color: #64748b; margin-bottom: 16px; }

.setting-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.setting-card { background: #ffffff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 14px; display: flex; align-items: center; justify-content: space-between; gap: 16px; }

.setting-icon { width: 38px; height: 38px; border-radius: 9999px; background: #eff6ff; display: flex; align-items: center; justify-content: center; }

.setting-info h3 { font-size: 15px; font-weight: 600; color: #0f172a; margin-bottom: 2px; }

.setting-info p { font-size: 13px; color: #64748b; }

button, select { background: #2563eb; color: #ffffff; border: none; padding: 8px 12px; border-radius: 8px; cursor: pointer; transition: background 0.2s; font-size: 13px; }
button:hover, select:hover { background: #1d4ed8; }
</style>