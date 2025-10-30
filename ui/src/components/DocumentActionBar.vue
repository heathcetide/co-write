<template>
  <div class="document-action-bar">
    <div class="action-group">
      <button 
        class="action-btn" 
        @click="showShareDialog = true"
        title="分享文档"
      >
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/>
          <polyline points="16,6 12,2 8,6"/>
          <line x1="12" y1="2" x2="12" y2="15"/>
        </svg>
        分享
      </button>
      
      <button 
        class="action-btn" 
        @click="showPermissionDialog = true"
        title="权限管理"
      >
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
          <circle cx="12" cy="16" r="1"/>
          <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
        </svg>
        权限
      </button>
      
      <button 
        class="action-btn" 
        @click="showAuditDialog = true"
        title="审计日志"
      >
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14,2 14,8 20,8"/>
          <line x1="16" y1="13" x2="8" y2="13"/>
          <line x1="16" y1="17" x2="8" y2="17"/>
          <polyline points="10,9 9,9 8,9"/>
        </svg>
        审计
      </button>
    </div>
    
    <div class="action-group">
      <button 
        class="action-btn" 
        @click="exportDocument"
        :disabled="!canExport"
        title="导出文档"
      >
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="7,10 12,15 17,10"/>
          <line x1="12" y1="15" x2="12" y2="3"/>
        </svg>
        导出
      </button>
      
      <button 
        class="action-btn" 
        @click="toggleFavorite"
        :class="{ active: isFavorited }"
        title="收藏文档"
      >
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
        </svg>
        {{ isFavorited ? '已收藏' : '收藏' }}
      </button>
    </div>

    <!-- 分享对话框 -->
    <DocumentShareDialog
      :visible="showShareDialog"
      :documentId="documentId"
      @close="showShareDialog = false"
    />

    <!-- 权限管理对话框 -->
    <DocumentPermissionDialog
      :visible="showPermissionDialog"
      :documentId="documentId"
      @close="showPermissionDialog = false"
    />

    <!-- 审计日志对话框 -->
    <AuditLogDialog
      :visible="showAuditDialog"
      :documentId="documentId"
      @close="showAuditDialog = false"
    />

    <!-- 分享口令验证对话框 -->
    <SharePasswordDialog
      :visible="showPasswordDialog"
      :shareCode="shareCode"
      @close="showPasswordDialog = false"
      @success="handlePasswordSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import DocumentShareDialog from './DocumentShareDialog.vue'
import DocumentPermissionDialog from './DocumentPermissionDialog.vue'
import AuditLogDialog from './AuditLogDialog.vue'
import SharePasswordDialog from './SharePasswordDialog.vue'
import api from '../api/index.js'

const props = defineProps<{
  documentId: string
  isFavorited?: boolean
}>()

const emit = defineEmits<{
  (e: 'favorite-changed', favorited: boolean): void
  (e: 'export-document'): void
}>()

const showShareDialog = ref(false)
const showPermissionDialog = ref(false)
const showAuditDialog = ref(false)
const showPasswordDialog = ref(false)
const shareCode = ref('')
const canExport = ref(true)

const exportDocument = () => {
  if (canExport.value) {
    emit('export-document')
  }
}

const toggleFavorite = async () => {
  try {
    if (props.isFavorited) {
      // 取消收藏
      await api.documentApi.deleteFavorite(props.documentId)
      emit('favorite-changed', false)
    } else {
      // 添加收藏
      await api.documentApi.addFavorite(props.documentId)
      emit('favorite-changed', true)
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    alert('操作失败')
  }
}

const handlePasswordSuccess = (data: any) => {
  // 口令验证成功，可以访问文档
  console.log('口令验证成功:', data)
  showPasswordDialog.value = false
}

const checkExportPermission = async () => {
  try {
    // 检查当前用户是否有导出权限
    const result = await api.documentShareApi.getDocumentPermissions(props.documentId)
    // 这里需要根据实际API返回的数据结构来判断
    // 假设API返回当前用户的权限信息
    canExport.value = true // 临时设置为true，实际需要根据权限判断
  } catch (error) {
    console.error('检查导出权限失败:', error)
    canExport.value = false
  }
}

onMounted(() => {
  checkExportPermission()
})
</script>

<style scoped>
.document-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  gap: 16px;
}

.action-group {
  display: flex;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.action-btn:disabled {
  background: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
  border-color: #e5e7eb;
}

.action-btn.active {
  background: #dbeafe;
  border-color: #3b82f6;
  color: #1d4ed8;
}

.icon {
  width: 16px;
  height: 16px;
  stroke-width: 2;
}
</style>
