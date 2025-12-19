<template>
  <a-modal
      v-model:visible="isVisible"
      title="分享文档"
      :width="600"
      :footer="false"
      @cancel="closeDialog"
      unmount-on-close
  >
    <template #title>
      <div style="display: flex; align-items: center; gap: 8px;">
        <icon-link />
        <span>分享文档</span>
      </div>
    </template>

    <a-form :model="shareData" layout="vertical" @submit="createShare">
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="分享权限" field="permission">
            <a-select v-model="shareData.permission" placeholder="请选择权限">
              <a-option value="VIEW">仅查看</a-option>
              <a-option value="EDIT">可编辑</a-option>
              <a-option value="COMMENT">可评论</a-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="访问口令（可选）" field="accessPassword">
            <a-input-password
                v-model="shareData.accessPassword"
                placeholder="设置访问口令"
                allow-clear
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-form-item label="过期时间" field="expireTime">
        <a-date-picker
            v-model="shareData.expireTime"
            show-time
            style="width: 100%"
            placeholder="选择过期时间"
        />
      </a-form-item>

      <a-form-item label="备注" field="remark">
        <a-textarea
            v-model="shareData.remark"
            placeholder="分享说明（可选）"
            :rows="3"
            :max-length="200"
            show-word-limit
        />
      </a-form-item>

      <a-form-item>
        <a-checkbox v-model="shareData.disableExport">
          禁用导出
        </a-checkbox>
      </a-form-item>

      <div v-if="shareResult" class="share-result">
        <a-alert type="success" show-icon>
          <template #icon>
            <icon-check-circle-fill />
          </template>
          <div style="font-weight: 600; margin-bottom: 12px;">分享链接已创建</div>
        </a-alert>
        
        <a-space direction="vertical" :size="12" style="width: 100%; margin-top: 16px;">
          <div>
            <a-typography-text type="secondary" style="font-size: 12px;">分享链接</a-typography-text>
            <a-input-group style="margin-top: 4px;">
              <a-input :value="shareResult.shareLink" readonly />
              <a-button type="primary" @click="copyLink">
                <template #icon>
                  <icon-copy />
                </template>
                复制
              </a-button>
            </a-input-group>
          </div>
          
          <div>
            <a-typography-text type="secondary" style="font-size: 12px;">分享码</a-typography-text>
            <a-tag color="blue" style="margin-top: 4px; font-size: 14px; padding: 4px 12px;">
              {{ shareResult.shortCode }}
            </a-tag>
          </div>
        </a-space>
      </div>

      <a-form-item style="margin-top: 24px; margin-bottom: 0;">
        <a-space>
          <a-button @click="closeDialog">取消</a-button>
          <a-button type="primary" html-type="submit" :loading="loading">
            创建分享
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { Message } from '@arco-design/web-vue'
import { IconLink, IconCheckCircleFill, IconCopy } from '@arco-design/web-vue/es/icon'
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
const shareResult = ref<any>(null)

const shareData = reactive({
  permission: 'VIEW',
  accessPassword: '',
  expireTime: null as any,
  remark: '',
  disableExport: false
})

const createShare = async () => {
  loading.value = true
  try {
    const data = {
      documentId: props.documentId,
      permission: shareData.permission,
      accessPassword: shareData.accessPassword || undefined,
      expireTime: shareData.expireTime ? new Date(shareData.expireTime).toISOString() : undefined,
      remark: shareData.remark || undefined,
      disableExport: shareData.disableExport,
      shareType: 'LINK',
      status: 'ACTIVE'
    }
    
    const result = await api.documentShareApi.createShareLink(data)
    if (result.code === 200 && result.data) {
      shareResult.value = result.data
      Message.success('分享链接创建成功')
    } else {
      Message.error(result.message || '创建分享失败')
    }
  } catch (error: any) {
    console.error('创建分享失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '创建分享失败'
    Message.error(errorMessage)
  } finally {
    loading.value = false
  }
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(shareResult.value.shareLink)
    Message.success('链接已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    Message.error('复制失败')
  }
}

const closeDialog = () => {
  emit('close')
  shareResult.value = null
  // 重置表单
  Object.assign(shareData, {
    permission: 'VIEW',
    accessPassword: '',
    expireTime: null,
    remark: '',
    disableExport: false
  })
}
</script>

<style scoped>
.share-result {
  margin-top: 16px;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
}
</style>
