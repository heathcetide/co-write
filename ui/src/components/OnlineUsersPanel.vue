<template>
  <div class="online-wrapper">
    <button class="toggle-btn" @click="visible = !visible">
      {{ visible ? '👥 隐藏用户列表' : '👥 查看在线用户' }}
    </button>

    <transition name="fade">
      <div v-if="visible" class="online-panel">
        <div class="header">在线用户（{{ users.length }}）</div>
        <ul class="user-list">
          <li
              v-for="user in users"
              :key="user"
              :class="{ self: user === self }"
          >
            <span class="dot"></span>
            {{ user }}
          </li>
        </ul>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  users: string[]
  self: string
}>()

const visible = ref(true)
</script>

<style scoped lang="scss">
.online-wrapper {
  position: fixed;
  right: 3rem;
  top: 5.5rem;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.toggle-btn {
  background-color: #0ea5e9;
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0.4rem 0.75rem;
  cursor: pointer;
  font-size: 0.8rem;
  margin-bottom: 0.5rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}
.toggle-btn:hover {
  background-color: #0284c7;
}

.online-panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
  padding: 1rem;
  width: 220px;
  max-height: 250px;
  overflow-y: auto;
  font-size: 0.85rem;
}

.header {
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #0f172a;
  font-size: 0.9rem;
}

.user-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.user-list li {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-list li.self {
  font-weight: bold;
  color: #0ea5e9;
}

.dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
}

/* 滚动条美化 */
.online-panel::-webkit-scrollbar {
  width: 6px;
}
.online-panel::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 3px;
}
.online-panel::-webkit-scrollbar-track {
  background: transparent;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
