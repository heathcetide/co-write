<template>
  <div v-if="visible" class="password-dialog-overlay" @click="closeDialog">
    <div class="password-dialog" @click.stop>
      <div class="dialog-header">
        <h3>输入访问口令</h3>
        <button class="close-btn" @click="closeDialog">×</button>
      </div>
      
      <div class="dialog-content">
        <div class="form-group">
          <label>访问口令</label>
          <input 
            v-model="password" 
            type="password" 
            placeholder="请输入访问口令"
            @keyup.enter="validatePassword"
            ref="passwordInput"
          />
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div v-if="retryInfo" class="retry-info">
          {{ retryInfo }}
        </div>

        <div class="dialog-actions">
          <button class="btn-secondary" @click="closeDialog">取消</button>
          <button class="btn-primary" @click="validatePassword" :disabled="loading">
            {{ loading ? '验证中...' : '确认' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import api from '../api/index.js'

const props = defineProps<{
  visible: boolean
  shareCode: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'success'): void
}>()

const password = ref('')
const loading = ref(false)
const errorMessage = ref('')
const retryInfo = ref('')
const passwordInput = ref<HTMLInputElement>()

const validatePassword = async () => {
  if (!password.value.trim()) {
    errorMessage.value = '请输入访问口令'
    return
  }

  loading.value = true
  errorMessage.value = ''
  retryInfo.value = ''

  try {
    const result = await api.documentShareApi.validatePassword({
      code: props.shareCode,
      password: password.value
    })
    
    if (result.data) {
      emit('success')
      closeDialog()
    } else {
      errorMessage.value = '口令验证失败'
    }
  } catch (error: any) {
    const message = error.response?.data?.message || '验证失败'
    errorMessage.value = message
    
    // 检查是否包含重试次数信息
    if (message.includes('剩余重试次数')) {
      retryInfo.value = message
    }
  } finally {
    loading.value = false
  }
}

const closeDialog = () => {
  emit('close')
  password.value = ''
  errorMessage.value = ''
  retryInfo.value = ''
}

// 对话框打开时自动聚焦输入框
const focusInput = async () => {
  if (props.visible && passwordInput.value) {
    await nextTick()
    passwordInput.value.focus()
  }
}

// 监听 visible 变化
watch(() => props.visible, focusInput)
</script>

<style scoped>
.password-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.password-dialog {
  background: white;
  border-radius: 12px;
  width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
}

.dialog-content {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #374151;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 16px;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.error-message {
  color: #dc2626;
  font-size: 14px;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
}

.retry-info {
  color: #d97706;
  font-size: 14px;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #fffbeb;
  border: 1px solid #fed7aa;
  border-radius: 6px;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-primary,
.btn-secondary {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}
</style>
