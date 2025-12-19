<template>
  <a-modal
      v-model:visible="isVisible"
      title="文档权限管理"
      :width="800"
      :footer="false"
      @cancel="closeDialog"
      unmount-on-close
  >
    <template #title>
      <div style="display: flex; align-items: center; gap: 8px;">
        <icon-lock />
        <span>文档权限管理</span>
      </div>
    </template>

    <a-space direction="vertical" :size="24" style="width: 100%;">
      <!-- 添加权限区域 -->
      <a-card :bordered="false" :body-style="{ padding: '16px' }">
        <template #title>
          <span style="font-size: 14px; font-weight: 600;">添加权限</span>
        </template>
        <a-form :model="newPermission" layout="inline" @submit="addPermission">
          <a-form-item label="用户ID" field="userId">
            <a-input-number
                v-model="newPermission.userId"
                placeholder="请输入用户ID"
                :min="1"
                style="width: 150px"
            />
          </a-form-item>
          
          <a-form-item label="权限" field="permission">
            <a-select
                v-model="newPermission.permission"
                placeholder="选择权限"
                style="width: 150px"
            >
              <a-option value="VIEW">仅查看</a-option>
              <a-option value="EDIT">可编辑</a-option>
              <a-option value="COMMENT">可评论</a-option>
              <a-option value="ADMIN">管理员</a-option>
            </a-select>
          </a-form-item>
          
          <a-form-item>
            <a-checkbox v-model="newPermission.disableExport">
              禁用导出
            </a-checkbox>
          </a-form-item>
          
          <a-form-item>
            <a-button type="primary" html-type="submit" :loading="loading">
              添加
            </a-button>
          </a-form-item>
        </a-form>
      </a-card>

      <!-- 权限列表 -->
      <a-card :bordered="false" :body-style="{ padding: '16px' }">
        <template #title>
          <span style="font-size: 14px; font-weight: 600;">当前权限</span>
        </template>

        <a-spin :loading="loading && permissions.length === 0" style="width: 100%;">
          <a-empty v-if="!loading && permissions.length === 0" description="暂无权限设置" />
          
          <a-table
              v-else
              :columns="columns"
              :data="permissions"
              :pagination="false"
              :bordered="false"
          >
            <template #permission="{ record }">
              <a-select
                  :model-value="record.permission"
                  @change="(val) => updatePermission(record.id, 'permission', val)"
                  :disabled="loading"
                  style="width: 120px"
              >
                <a-option value="VIEW">仅查看</a-option>
                <a-option value="EDIT">可编辑</a-option>
                <a-option value="COMMENT">可评论</a-option>
                <a-option value="ADMIN">管理员</a-option>
              </a-select>
            </template>

            <template #disableExport="{ record }">
              <a-switch
                  :model-value="record.disableExport"
                  @change="(val) => updatePermission(record.id, 'disableExport', val)"
                  :disabled="loading"
              />
            </template>

            <template #actions="{ record }">
              <a-popconfirm
                  content="确定要删除这个权限吗？"
                  @ok="deletePermission(record.id)"
              >
                <a-button type="text" status="danger" size="small">
                  <template #icon>
                    <icon-delete />
                  </template>
                  删除
                </a-button>
              </a-popconfirm>
            </template>
          </a-table>
        </a-spin>
      </a-card>
    </a-space>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { Message } from '@arco-design/web-vue'
import { IconLock, IconDelete } from '@arco-design/web-vue/es/icon'
import api from '../api/index.js'

const props = defineProps<{
  visible: boolean
  documentId: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const isVisible = computed({
  get: () => props.visible,
  set: (val) => {
    if (!val) {
      emit('close')
    }
  }
})

const loading = ref(false)
const permissions = ref<any[]>([])

const columns = [
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 120,
  },
  {
    title: '权限',
    dataIndex: 'permission',
    slotName: 'permission',
    width: 150,
  },
  {
    title: '禁用导出',
    dataIndex: 'disableExport',
    slotName: 'disableExport',
    width: 120,
  },
  {
    title: '操作',
    slotName: 'actions',
    width: 100,
  },
]

const newPermission = reactive({
  userId: undefined as number | undefined,
  permission: 'VIEW',
  disableExport: false
})

const loadPermissions = async () => {
  loading.value = true
  try {
    const result = await api.documentShareApi.getDocumentPermissions(props.documentId)
    if (result.code === 200) {
      permissions.value = result.data || []
    } else {
      Message.error(result.message || '加载权限失败')
    }
  } catch (error: any) {
    console.error('加载权限失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '加载权限失败'
    Message.error(errorMessage)
  } finally {
    loading.value = false
  }
}

const addPermission = async () => {
  if (!newPermission.userId) {
    Message.warning('请输入用户ID')
    return
  }

  loading.value = true
  try {
    const result = await api.documentShareApi.setDocumentPermission({
      documentId: props.documentId,
      userId: newPermission.userId,
      permission: newPermission.permission,
      disableExport: newPermission.disableExport
    })
    
    if (result.code === 200) {
      Message.success('权限添加成功')
      // 重置表单
      newPermission.userId = undefined
      newPermission.permission = 'VIEW'
      newPermission.disableExport = false
      
      // 重新加载权限列表
      await loadPermissions()
    } else {
      Message.error(result.message || '添加权限失败')
    }
  } catch (error: any) {
    console.error('添加权限失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '添加权限失败'
    Message.error(errorMessage)
  } finally {
    loading.value = false
  }
}

const updatePermission = async (id: number, field: string, value: any) => {
  loading.value = true
  try {
    const updateData: any = { id }
    updateData[field] = value
    
    const result = await api.documentShareApi.updateDocumentPermission(updateData)
    
    if (result.code === 200) {
      Message.success('权限更新成功')
      // 重新加载权限列表
      await loadPermissions()
    } else {
      Message.error(result.message || '更新权限失败')
    }
  } catch (error: any) {
    console.error('更新权限失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '更新权限失败'
    Message.error(errorMessage)
  } finally {
    loading.value = false
  }
}

const deletePermission = async (id: number) => {
  loading.value = true
  try {
    const result = await api.documentShareApi.deleteDocumentPermission(id)
    
    if (result.code === 200) {
      Message.success('权限删除成功')
      // 重新加载权限列表
      await loadPermissions()
    } else {
      Message.error(result.message || '删除权限失败')
    }
  } catch (error: any) {
    console.error('删除权限失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '删除权限失败'
    Message.error(errorMessage)
  } finally {
    loading.value = false
  }
}

const closeDialog = () => {
  emit('close')
  permissions.value = []
  newPermission.userId = undefined
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
:deep(.arco-card-header) {
  padding-bottom: 12px;
}
</style>
