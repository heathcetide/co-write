<template>
  <a-modal
      v-model:visible="isVisible"
      title="文档审计日志"
      :width="900"
      :footer="false"
      @cancel="closeDialog"
      unmount-on-close
  >
    <template #title>
      <div style="display: flex; align-items: center; gap: 8px;">
        <icon-file />
        <span>文档审计日志</span>
      </div>
    </template>

    <a-space direction="vertical" :size="16" style="width: 100%;">
      <!-- 筛选区域 -->
      <a-card :bordered="false" :body-style="{ padding: '16px' }">
        <a-form :model="filters" layout="inline" @submit="loadLogs">
          <a-form-item label="操作类型" field="operation">
            <a-select
                v-model="filters.operation"
                placeholder="全部"
                style="width: 150px"
                allow-clear
            >
              <a-option value="INSERT">插入</a-option>
              <a-option value="DELETE">删除</a-option>
              <a-option value="UPDATE">更新</a-option>
              <a-option value="EXPORT">导出</a-option>
            </a-select>
          </a-form-item>
          
          <a-form-item label="用户" field="username">
            <a-input
                v-model="filters.username"
                placeholder="搜索用户名"
                style="width: 200px"
                allow-clear
            />
          </a-form-item>
          
          <a-form-item>
            <a-space>
              <a-button type="primary" html-type="submit" :loading="loading">
                筛选
              </a-button>
              <a-button @click="resetFilters">重置</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>

      <!-- 日志列表 -->
      <a-card :bordered="false" :body-style="{ padding: '16px' }">
        <a-spin :loading="loading && logs.length === 0" style="width: 100%; min-height: 200px;">
          <a-empty v-if="!loading && logs.length === 0" description="暂无审计日志" />
          
          <a-list
              v-else
              :bordered="false"
              :data="paginatedLogs"
          >
            <template #item="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>
                    <a-space>
                      <a-tag :color="getOperationColor(item.operation)">
                        {{ getOperationText(item.operation) }}
                      </a-tag>
                      <a-typography-text strong>{{ item.username }}</a-typography-text>
                      <a-typography-text type="secondary" style="font-size: 12px;">
                        {{ formatTime(item.createdAt) }}
                      </a-typography-text>
                    </a-space>
                  </template>
                  <template #description>
                    <a-space direction="vertical" :size="4" style="margin-top: 8px;">
                      <div v-if="item.pos !== undefined" style="font-size: 12px;">
                        <a-typography-text type="secondary">位置:</a-typography-text>
                        <a-typography-text>{{ item.pos }}</a-typography-text>
                      </div>
                      <div v-if="item.length !== undefined" style="font-size: 12px;">
                        <a-typography-text type="secondary">长度:</a-typography-text>
                        <a-typography-text>{{ item.length }}</a-typography-text>
                      </div>
                      <div v-if="item.deltaText" style="font-size: 12px;">
                        <a-typography-text type="secondary">内容:</a-typography-text>
                        <a-tag color="blue" style="font-family: monospace; font-size: 11px;">
                          {{ item.deltaText }}
                        </a-tag>
                      </div>
                      <div v-if="item.ip" style="font-size: 12px;">
                        <a-typography-text type="secondary">IP:</a-typography-text>
                        <a-typography-text>{{ item.ip }}</a-typography-text>
                      </div>
                    </a-space>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-spin>
      </a-card>

      <!-- 分页 -->
      <div v-if="logs.length > 0" style="display: flex; justify-content: center;">
        <a-pagination
            :current="currentPage"
            :total="logs.length"
            :page-size="pageSize"
            show-total
            @change="handlePageChange"
        />
      </div>

      <!-- 导出按钮 -->
      <div style="text-align: right;">
        <a-button type="outline" @click="exportLogs" :loading="loading">
          <template #icon>
            <icon-download />
          </template>
          导出CSV
        </a-button>
      </div>
    </a-space>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { Message } from '@arco-design/web-vue'
import { IconFile, IconDownload } from '@arco-design/web-vue/es/icon'
import api from '../api/index.js'
import dayjs from 'dayjs'

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
const logs = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)

const filters = reactive({
  operation: '',
  username: ''
})

const paginatedLogs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return logs.value.slice(start, end)
})

const loadLogs = async () => {
  loading.value = true
  try {
    const result = await api.auditApi.getDocumentAuditLogs(props.documentId)
    if (result.code === 200) {
      let filteredLogs = result.data || []
      
      // 应用筛选
      if (filters.operation) {
        filteredLogs = filteredLogs.filter((log: any) => log.operation === filters.operation)
      }
      if (filters.username) {
        filteredLogs = filteredLogs.filter((log: any) => 
          log.username?.toLowerCase().includes(filters.username.toLowerCase())
        )
      }
      
      logs.value = filteredLogs
      currentPage.value = 1
    } else {
      Message.error(result.message || '加载审计日志失败')
    }
  } catch (error: any) {
    console.error('加载审计日志失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '加载审计日志失败'
    Message.error(errorMessage)
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
    link.download = `audit_log_${props.documentId}_${dayjs().format('YYYY-MM-DD')}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    Message.success('导出成功')
  } catch (error: any) {
    console.error('导出失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '导出失败'
    Message.error(errorMessage)
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

const getOperationColor = (operation: string) => {
  const map: Record<string, string> = {
    'INSERT': 'green',
    'DELETE': 'red',
    'UPDATE': 'blue',
    'EXPORT': 'orange'
  }
  return map[operation] || 'gray'
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

const resetFilters = () => {
  filters.operation = ''
  filters.username = ''
  loadLogs()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
}

const closeDialog = () => {
  emit('close')
  logs.value = []
  currentPage.value = 1
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
:deep(.arco-card-header) {
  padding-bottom: 12px;
}

:deep(.arco-list-item) {
  padding: 12px 0;
  border-bottom: 1px solid #f2f3f5;
}

:deep(.arco-list-item:last-child) {
  border-bottom: none;
}
</style>
