<template>
  <div v-if="visible" class="audit-dialog-overlay" @click="closeDialog">
    <div class="audit-dialog" @click.stop>
      <div class="dialog-header">
        <h3>文档审计日志</h3>
        <div class="header-actions">
          <button class="btn-export" @click="exportLogs" :disabled="loading">
            {{ loading ? '导出中...' : '导出CSV' }}
          </button>
          <button class="close-btn" @click="closeDialog">×</button>
        </div>
      </div>
      
      <div class="dialog-content">
        <div class="filter-section">
          <div class="filter-group">
            <label>操作类型</label>
            <select v-model="filters.operation">
              <option value="">全部</option>
              <option value="INSERT">插入</option>
              <option value="DELETE">删除</option>
              <option value="UPDATE">更新</option>
              <option value="EXPORT">导出</option>
            </select>
          </div>
          
          <div class="filter-group">
            <label>用户</label>
            <input 
              v-model="filters.username" 
              placeholder="搜索用户名"
            />
          </div>
          
          <div class="filter-group">
            <button class="btn-filter" @click="loadLogs">筛选</button>
            <button class="btn-reset" @click="resetFilters">重置</button>
          </div>
        </div>

        <div class="logs-section">
          <div v-if="loading && logs.length === 0" class="loading">
            加载中...
          </div>
          
          <div v-else-if="logs.length === 0" class="empty">
            暂无审计日志
          </div>
          
          <div v-else class="logs-list">
            <div 
              v-for="log in logs" 
              :key="log.id" 
              class="log-item"
            >
              <div class="log-header">
                <span class="operation">{{ getOperationText(log.operation) }}</span>
                <span class="username">{{ log.username }}</span>
                <span class="time">{{ formatTime(log.createdAt) }}</span>
              </div>
              
              <div class="log-details">
                <div v-if="log.pos !== undefined" class="detail-item">
                  <span class="label">位置:</span>
                  <span class="value">{{ log.pos }}</span>
                </div>
                
                <div v-if="log.length !== undefined" class="detail-item">
                  <span class="label">长度:</span>
                  <span class="value">{{ log.length }}</span>
                </div>
                
                <div v-if="log.deltaText" class="detail-item">
                  <span class="label">内容:</span>
                  <span class="value content">{{ log.deltaText }}</span>
                </div>
                
                <div v-if="log.ip" class="detail-item">
                  <span class="label">IP:</span>
                  <span class="value">{{ log.ip }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="logs.length > 0" class="pagination">
          <button 
            class="btn-prev" 
            @click="prevPage" 
            :disabled="currentPage <= 1"
          >
            上一页
          </button>
          <span class="page-info">
            第 {{ currentPage }} 页，共 {{ totalPages }} 页
          </span>
          <button 
            class="btn-next" 
            @click="nextPage" 
            :disabled="currentPage >= totalPages"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import api from '../api/index.js'

const props = defineProps<{
  visible: boolean
  documentId: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const loading = ref(false)
const logs = ref<any[]>([])
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 20

const filters = reactive({
  operation: '',
  username: ''
})

const loadLogs = async () => {
  loading.value = true
  try {
    const result = await api.auditApi.getDocumentAuditLogs(props.documentId)
    logs.value = result.data || []
    totalPages.value = Math.ceil(logs.value.length / pageSize)
  } catch (error) {
    console.error('加载审计日志失败:', error)
    alert('加载审计日志失败')
  } finally {
    loading.value = false
  }
}

const exportLogs = async () => {
  loading.value = true
  try {
    const result = await api.auditApi.exportAuditLogs(props.documentId, 'current-user')
    
    // 创建下载链接
    const blob = new Blob([result.data], { type: 'text/csv' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `audit_log_${props.documentId}_${new Date().toISOString().split('T')[0]}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('导出失败:', error)
    alert('导出失败')
  } finally {
    loading.value = false
  }
}

const getOperationText = (operation: string) => {
  const map: Record<string, string> = {
    'INSERT': '插入',
    'DELETE': '删除', 
    'UPDATE': '更新',
    'EXPORT': '导出'
  }
  return map[operation] || operation
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

const resetFilters = () => {
  filters.operation = ''
  filters.username = ''
  loadLogs()
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadLogs()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadLogs()
  }
}

const closeDialog = () => {
  emit('close')
  logs.value = []
  currentPage.value = 1
  totalPages.value = 1
  resetFilters()
}

// 监听弹窗显示状态
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadLogs()
  }
})
</script>

<style scoped>
.audit-dialog-overlay {
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

.audit-dialog {
  background: white;
  border-radius: 16px;
  width: 900px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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
  content: '📊';
  font-size: 24px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-export {
  padding: 8px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.btn-export:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
}

.dialog-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.filter-section {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  gap: 16px;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.filter-group input,
.filter-group select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  min-width: 120px;
}

.btn-filter,
.btn-reset {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  border: none;
}

.btn-filter {
  background: #3b82f6;
  color: white;
}

.btn-reset {
  background: #f3f4f6;
  color: #374151;
}

.logs-section {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}

.loading,
.empty {
  text-align: center;
  color: #6b7280;
  padding: 40px 0;
}

.logs-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.log-item {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  background: #f9fafb;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.operation {
  background: #3b82f6;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.username {
  font-weight: 500;
  color: #374151;
}

.time {
  color: #6b7280;
  font-size: 14px;
}

.log-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  gap: 8px;
}

.label {
  font-weight: 500;
  color: #6b7280;
  min-width: 60px;
}

.value {
  color: #374151;
}

.value.content {
  background: #f3f4f6;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-prev,
.btn-next {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  font-size: 14px;
}

.btn-prev:disabled,
.btn-next:disabled {
  background: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
}

.page-info {
  color: #6b7280;
  font-size: 14px;
}
</style>
