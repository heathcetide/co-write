<template>
  <div v-if="visible" class="permission-dialog-overlay" @click="closeDialog">
    <div class="permission-dialog" @click.stop>
      <div class="dialog-header">
        <h3>文档权限管理</h3>
        <button class="close-btn" @click="closeDialog">×</button>
      </div>
      
      <div class="dialog-content">
        <div class="add-permission-section">
          <h4>添加权限</h4>
          <div class="form-row">
            <div class="form-group">
              <label>用户</label>
              <input 
                v-model="newPermission.userId" 
                placeholder="用户ID"
                type="number"
              />
            </div>
            
            <div class="form-group">
              <label>权限</label>
              <select v-model="newPermission.permission">
                <option value="VIEW">仅查看</option>
                <option value="EDIT">可编辑</option>
                <option value="COMMENT">可评论</option>
                <option value="ADMIN">管理员</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>
                <input 
                  v-model="newPermission.disableExport" 
                  type="checkbox"
                />
                禁用导出
              </label>
            </div>
            
            <button class="btn-add" @click="addPermission" :disabled="loading">
              {{ loading ? '添加中...' : '添加' }}
            </button>
          </div>
        </div>

        <div class="permissions-list">
          <h4>当前权限</h4>
          
          <div v-if="loading && permissions.length === 0" class="loading">
            加载中...
          </div>
          
          <div v-else-if="permissions.length === 0" class="empty">
            暂无权限设置
          </div>
          
          <div v-else class="permissions-table">
            <div class="table-header">
              <div class="col-user">用户</div>
              <div class="col-permission">权限</div>
              <div class="col-export">导出</div>
              <div class="col-actions">操作</div>
            </div>
            
            <div 
              v-for="permission in permissions" 
              :key="permission.id" 
              class="table-row"
            >
              <div class="col-user">
                <span class="user-id">ID: {{ permission.userId }}</span>
              </div>
              
              <div class="col-permission">
                <select 
                  :value="permission.permission"
                  @change="updatePermission(permission.id, 'permission', $event.target.value)"
                  :disabled="loading"
                >
                  <option value="VIEW">仅查看</option>
                  <option value="EDIT">可编辑</option>
                  <option value="COMMENT">可评论</option>
                  <option value="ADMIN">管理员</option>
                </select>
              </div>
              
              <div class="col-export">
                <label class="checkbox-label">
                  <input 
                    type="checkbox"
                    :checked="permission.disableExport"
                    @change="updatePermission(permission.id, 'disableExport', $event.target.checked)"
                    :disabled="loading"
                  />
                  <span class="checkmark"></span>
                </label>
              </div>
              
              <div class="col-actions">
                <button 
                  class="btn-delete" 
                  @click="deletePermission(permission.id)"
                  :disabled="loading"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import api from '../api/index.js'

const props = defineProps<{
  visible: boolean
  documentId: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const loading = ref(false)
const permissions = ref<any[]>([])

const newPermission = reactive({
  userId: '',
  permission: 'VIEW',
  disableExport: false
})

const loadPermissions = async () => {
  loading.value = true
  try {
    const result = await api.documentShareApi.getDocumentPermissions(props.documentId)
    permissions.value = result.data || []
  } catch (error) {
    console.error('加载权限失败:', error)
    alert('加载权限失败')
  } finally {
    loading.value = false
  }
}

const addPermission = async () => {
  if (!newPermission.userId) {
    alert('请输入用户ID')
    return
  }

  loading.value = true
  try {
    await api.documentShareApi.setDocumentPermission({
      documentId: props.documentId,
      userId: parseInt(newPermission.userId),
      permission: newPermission.permission,
      disableExport: newPermission.disableExport
    })
    
    // 重置表单
    newPermission.userId = ''
    newPermission.permission = 'VIEW'
    newPermission.disableExport = false
    
    // 重新加载权限列表
    loadPermissions()
  } catch (error) {
    console.error('添加权限失败:', error)
    alert('添加权限失败')
  } finally {
    loading.value = false
  }
}

const updatePermission = async (id: number, field: string, value: any) => {
  loading.value = true
  try {
    const updateData: any = { id }
    updateData[field] = value
    
    await api.documentShareApi.updateDocumentPermission(updateData)
    
    // 重新加载权限列表
    loadPermissions()
  } catch (error) {
    console.error('更新权限失败:', error)
    alert('更新权限失败')
  } finally {
    loading.value = false
  }
}

const deletePermission = async (id: number) => {
  if (!confirm('确定要删除这个权限吗？')) {
    return
  }

  loading.value = true
  try {
    await api.documentShareApi.deleteDocumentPermission(id)
    
    // 重新加载权限列表
    loadPermissions()
  } catch (error) {
    console.error('删除权限失败:', error)
    alert('删除权限失败')
  } finally {
    loading.value = false
  }
}

const closeDialog = () => {
  emit('close')
  permissions.value = []
  newPermission.userId = ''
  newPermission.permission = 'VIEW'
  newPermission.disableExport = false
}

// 监听弹窗显示状态
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadPermissions()
  }
})
</script>

<style scoped>
.permission-dialog-overlay {
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

.permission-dialog {
  background: white;
  border-radius: 16px;
  width: 800px;
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
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
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
  content: '🔐';
  font-size: 24px;
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

.add-permission-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.add-permission-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.form-row {
  display: flex;
  gap: 16px;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.form-group input,
.form-group select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  min-width: 120px;
}

.btn-add {
  padding: 8px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  height: fit-content;
}

.btn-add:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.permissions-list h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.loading,
.empty {
  text-align: center;
  color: #6b7280;
  padding: 40px 0;
}

.permissions-table {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 1fr 150px 80px 80px;
  gap: 16px;
  padding: 12px 16px;
  background: #f9fafb;
  font-weight: 600;
  font-size: 14px;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}

.table-row {
  display: grid;
  grid-template-columns: 1fr 150px 80px 80px;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
  align-items: center;
}

.table-row:last-child {
  border-bottom: none;
}

.user-id {
  font-size: 14px;
  color: #6b7280;
}

.table-row select {
  padding: 4px 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  background: white;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  margin: 0;
  margin-right: 8px;
}

.btn-delete {
  padding: 4px 8px;
  background: #dc2626;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.btn-delete:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
</style>
