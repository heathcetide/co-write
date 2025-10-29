<template>
  <div class="toast-container">
    <transition-group name="fade" tag="div">
      <div
          class="toast"
          v-for="(msg, index) in messages"
          :key="msg.id"
      >
        {{ msg.text }}
      </div>
    </transition-group>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface ToastMessage {
  id: number
  text: string
}

const messages = ref<ToastMessage[]>([])

let counter = 0
const DURATION = 3000 // 每条提示显示时长（毫秒）

function addMessage(text: string) {
  const id = counter++
  messages.value.push({ id, text })

  setTimeout(() => {
    messages.value = messages.value.filter(m => m.id !== id)
  }, DURATION)
}

defineExpose({ addMessage })
</script>

<style scoped lang="scss">
.toast-container {
  position: fixed;
  right: 1rem;
  bottom: 1rem;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.toast {
  background-color: #0ea5e9;
  color: white;
  padding: 0.6rem 1rem;
  border-radius: 0.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  font-size: 0.875rem;
  max-width: 260px;
  word-break: break-word;
  animation: slide-in 0.3s ease;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@keyframes slide-in {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
