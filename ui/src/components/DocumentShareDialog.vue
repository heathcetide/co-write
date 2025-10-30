<template>
  <div v-if="visible" class="share-dialog-overlay" @click="closeDialog">
    <div class="share-dialog" @click.stop>
      <div class="dialog-header">
        <h3>分享文档</h3>
        <button class="close-btn" @click="closeDialog">×</button>
      </div>
      
      <div class="dialog-content">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">
              <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M9 12l2 2 4-4"/>
                <path d="M21 12c-1 0-3-1-3-3s2-3 3-3 3 1 3 3-2 3-3 3"/>
                <path d="M3 12c1 0 3-1 3-3s-2-3-3-3-3 1-3 3 2 3 3 3"/>
                <path d="M12 3c0 1-1 3-3 3s-3-2-3-3 1-3 3-3 3 2 3 3"/>
                <path d="M12 21c0-1 1-3 3-3s3 2 3 3-1 3-3 3-3-2-3-3"/>
              </svg>
              分享权限
            </label>
            <select v-model="shareData.permission" class="form-select">
              <option value="VIEW">仅查看</option>
              <option value="EDIT">可编辑</option>
              <option value="COMMENT">可评论</option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <circle cx="12" cy="16" r="1"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              访问口令（可选）
            </label>
            <input 
              v-model="shareData.accessPassword" 
              type="password" 
              placeholder="设置访问口令"
              class="form-input"
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">
            <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/>
              <line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            过期时间
          </label>
          <input 
            v-model="shareData.expireTime" 
            type="datetime-local"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label class="form-label">
            <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14,2 14,8 20,8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
            </svg>
            备注
          </label>
          <textarea 
            v-model="shareData.remark" 
            placeholder="分享说明（可选）"
            rows="3"
            class="form-textarea"
          ></textarea>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input 
              v-model="shareData.disableExport" 
              type="checkbox"
              class="checkbox-input"
            />
            <span class="checkbox-custom"></span>
            <span class="checkbox-text">禁用导出</span>
          </label>
        </div>

        <div class="dialog-actions">
          <button class="btn-secondary" @click="closeDialog">取消</button>
          <button class="btn-primary" @click="createShare" :disabled="loading">
            {{ loading ? '创建中...' : '创建分享' }}
          </button>
        </div>

        <div v-if="shareResult" class="share-result">
          <h4>分享链接已创建</h4>
          <div class="share-link">
            <input :value="shareResult.shareLink" readonly />
            <button @click="copyLink">复制</button>
          </div>
          <div class="share-code">
            分享码: {{ shareResult.shortCode }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import api from '../api/index.js'

const props = defineProps<{
  visible: boolean
  documentId: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const loading = ref(false)
const shareResult = ref<any>(null)

const shareData = reactive({
  permission: 'VIEW',
  accessPassword: '',
  expireTime: '',
  remark: '',
  disableExport: false
})

const createShare = async () => {
  loading.value = true
  try {
    const data = {
      documentId: props.documentId,
      ...shareData,
      shareType: 'LINK',
      status: 'ACTIVE'
    }
    
    const result = await api.documentShareApi.createShareLink(data)
    shareResult.value = result.data
  } catch (error) {
    console.error('创建分享失败:', error)
    alert('创建分享失败')
  } finally {
    loading.value = false
  }
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(shareResult.value.shareLink)
    alert('链接已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
  }
}

const closeDialog = () => {
  emit('close')
  shareResult.value = null
  // 重置表单
  Object.assign(shareData, {
    permission: 'VIEW',
    accessPassword: '',
    expireTime: '',
    remark: '',
    disableExport: false
  })
}
</script>

<style scoped>
.share-dialog-overlay {
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

.share-dialog {
  background: white;
  border-radius: 16px;
  width: 600px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 16px 16px 0 0;
}

.dialog-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.dialog-header h3::before {
  content: '🔗';
  font-size: 24px;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.dialog-content {
  padding: 32px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.label-icon {
  width: 16px;
  height: 16px;
  color: #6366f1;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.2s;
  background: #fafbfc;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #6366f1;
  background: white;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.checkbox-group {
  margin-top: 8px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  font-weight: 500;
  color: #374151;
}

.checkbox-input {
  display: none;
}

.checkbox-custom {
  width: 20px;
  height: 20px;
  border: 2px solid #d1d5db;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  background: white;
}

.checkbox-input:checked + .checkbox-custom {
  background: #6366f1;
  border-color: #6366f1;
}

.checkbox-input:checked + .checkbox-custom::after {
  content: '✓';
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.checkbox-text {
  font-size: 14px;
}

.dialog-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
  min-width: 100px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-secondary {
  background: #f8fafc;
  color: #64748b;
  border: 2px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
}

.share-result {
  margin-top: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 16px;
  border: 2px solid #bae6fd;
  position: relative;
  overflow: hidden;
}

.share-result::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #0ea5e9, #3b82f6, #8b5cf6);
}

.share-result h4 {
  margin: 0 0 16px 0;
  color: #0c4a6e;
  font-size: 18px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}

.share-result h4::before {
  content: '✨';
  font-size: 20px;
}

.share-link {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.share-link input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #bae6fd;
  border-radius: 12px;
  background: white;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 13px;
  color: #0c4a6e;
}

.share-link button {
  padding: 12px 20px;
  background: linear-gradient(135deg, #0ea5e9 0%, #3b82f6 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
  white-space: nowrap;
}

.share-link button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.3);
}

.share-code {
  font-size: 13px;
  color: #0369a1;
  font-weight: 600;
  padding: 8px 12px;
  background: rgba(14, 165, 233, 0.1);
  border-radius: 8px;
  display: inline-block;
}
</style>
