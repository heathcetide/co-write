<template>
  <div class="org-management-page">
    <!-- 顶部操作栏 -->
    <a-card :bordered="false" class="header-card">
      <a-space :size="16" align="center" style="width: 100%; justify-content: space-between;">
        <a-space :size="12" align="center">
          <a-button type="text" @click="goBack">
            <template #icon>
              <icon-arrow-left />
            </template>
            返回
          </a-button>
          <a-divider type="vertical" />
          <a-typography-title :heading="5" style="margin: 0;">组织管理中心</a-typography-title>
        </a-space>
        
        <a-space>
          <a-radio-group 
              v-model="viewType" 
              type="button"
              @change="handleViewTypeChange"
          >
            <a-radio value="managed">我管理的团队</a-radio>
            <a-radio value="participated">我参与的团队</a-radio>
          </a-radio-group>
          <a-button type="primary" @click="showCreateModal">
            <template #icon>
              <icon-plus />
            </template>
            创建组织
          </a-button>
        </a-space>
      </a-space>
    </a-card>

    <!-- 组织列表 -->
    <a-spin :loading="loading" style="width: 100%;">
      <a-empty 
          v-if="!loading && organizations.length === 0" 
          description="暂无组织"
          style="margin: 60px 0;"
      />
      
      <a-row :gutter="[24, 24]" v-else>
        <a-col 
            v-for="org in organizations" 
            :key="org.id"
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6"
        >
          <a-card 
              class="org-card" 
              :hoverable="true"
              :bordered="true"
          >
            <template #cover>
              <div class="org-cover">
                <a-avatar 
                    :size="64" 
                    :style="getOrgAvatarStyle(org.name)"
                >
                  {{ org.name?.[0]?.toUpperCase() || 'O' }}
                </a-avatar>
              </div>
            </template>
            
            <a-card-meta>
              <template #title>
                <a-typography-title :heading="6" style="margin: 0;">
                  {{ org.name }}
                </a-typography-title>
              </template>
              <template #description>
                <a-space direction="vertical" :size="4" style="width: 100%;">
                  <a-typography-text 
                      type="secondary" 
                      :ellipsis="{ rows: 2 }"
                      style="font-size: 12px;"
                  >
                    {{ org.description || '暂无描述' }}
                  </a-typography-text>
                  
                  <a-space :size="16" style="margin-top: 8px;">
                    <a-tag color="blue">
                      <template #icon>
                        <icon-user />
                      </template>
                      {{ org.currentMembers || 0 }} / {{ org.maxMembers || 50 }}
                    </a-tag>
                    <a-tag v-if="org.published" color="green">公开</a-tag>
                    <a-tag v-else color="gray">私有</a-tag>
                  </a-space>
                </a-space>
              </template>
            </a-card-meta>
            
            <template #actions>
              <a-space :size="8" style="width: 100%; justify-content: center;">
                <a-button 
                    type="primary" 
                    size="small"
                    @click="showInviteModal(org)"
                >
                  <template #icon>
                    <icon-user-add />
                  </template>
                  邀请
                </a-button>
                <a-button 
                    type="outline" 
                    size="small"
                    @click="viewMembers(org)"
                >
                  <template #icon>
                    <icon-user-group />
                  </template>
                  成员
                </a-button>
                <a-button 
                    type="outline" 
                    size="small"
                    @click="manageOrganization(org)"
                >
                  <template #icon>
                    <icon-settings />
                  </template>
                  管理
                </a-button>
              </a-space>
            </template>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>

    <!-- 创建组织弹窗 -->
    <a-modal
        v-model:visible="createModalVisible"
        title="创建组织"
        :width="500"
        @ok="handleCreateOrganization"
        @cancel="createModalVisible = false"
        :ok-loading="creating"
    >
      <a-form :model="createForm" layout="vertical">
        <a-form-item label="组织名称" field="name" :rules="[{ required: true, message: '请输入组织名称' }]">
          <a-input 
              v-model="createForm.name" 
              placeholder="请输入组织名称"
              :max-length="50"
              show-word-limit
          />
        </a-form-item>
        
        <a-form-item label="组织描述" field="description">
          <a-textarea 
              v-model="createForm.description" 
              placeholder="请输入组织描述（可选）"
              :rows="3"
              :max-length="200"
              show-word-limit
          />
        </a-form-item>
        
        <a-form-item label="最大成员数" field="maxMembers">
          <a-input-number 
              v-model="createForm.maxMembers" 
              :min="1"
              :max="1000"
              placeholder="最大成员数"
              style="width: 100%"
          />
        </a-form-item>
        
        <a-form-item label="是否公开" field="published">
          <a-switch v-model="createForm.published" />
          <a-typography-text type="secondary" style="margin-left: 8px;">
            公开后其他用户可以搜索并申请加入
          </a-typography-text>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 邀请协作弹窗 -->
    <a-modal
        v-model:visible="inviteModalVisible"
        title="生成团队邀请码"
        :width="600"
        :footer="false"
        @cancel="closeInviteModal"
        unmount-on-close
    >
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <icon-user-add />
          <span>生成团队邀请码</span>
        </div>
      </template>

      <a-form :model="inviteForm" layout="vertical" @submit="handleGenerateInvite">
        <a-form-item label="成员角色" field="role">
          <a-select v-model="inviteForm.role" placeholder="请选择成员角色">
            <a-option value="OWNER">OWNER</a-option>
            <a-option value="ADMIN">ADMIN</a-option>
            <a-option value="MEMBER">MEMBER</a-option>
          </a-select>
        </a-form-item>

        <a-form-item label="最大使用次数" field="maxUses">
          <a-input-number 
              v-model="inviteForm.maxUses" 
              :min="1"
              placeholder="最大使用次数"
              style="width: 100%"
          />
        </a-form-item>

        <a-form-item label="过期时间" field="expiresAt">
          <a-date-picker
              v-model="inviteForm.expiresAt"
              show-time
              style="width: 100%"
              placeholder="选择过期时间（可选）"
          />
        </a-form-item>

        <!-- 生成的邀请码结果 -->
        <div v-if="inviteResult" class="invite-result">
          <a-alert type="success" show-icon>
            <template #icon>
              <icon-check-circle-fill />
            </template>
            <div style="font-weight: 600; margin-bottom: 12px;">邀请码已成功生成</div>
          </a-alert>
          
          <a-space direction="vertical" :size="12" style="width: 100%; margin-top: 16px;">
            <div>
              <a-typography-text type="secondary" style="font-size: 12px;">邀请码</a-typography-text>
              <a-input-group style="margin-top: 4px;">
                <a-input :value="inviteResult.inviteCode" readonly />
                <a-button type="primary" @click="copyInviteCode">
                  <template #icon>
                    <icon-copy />
                  </template>
                  复制
                </a-button>
              </a-input-group>
            </div>
            
            <div>
              <a-typography-text type="secondary" style="font-size: 12px;">短链接</a-typography-text>
              <a-input-group style="margin-top: 4px;">
                <a-input :value="inviteResult.shortUrl" readonly />
                <a-button type="primary" @click="copyShortLink">
                  <template #icon>
                    <icon-copy />
                  </template>
                  复制
                </a-button>
              </a-input-group>
            </div>
            
            <div v-if="inviteResult.qrCodeUrl" style="text-align: center; margin-top: 16px;">
              <a-typography-text type="secondary" style="font-size: 12px; display: block; margin-bottom: 8px;">
                二维码
              </a-typography-text>
              <div style="display: inline-block; padding: 16px; background: #f7f8fa; border-radius: 8px;">
                <qrcode-vue
                    :value="inviteResult.qrCodeUrl"
                    :size="180"
                    level="H"
                    background="#ffffff"
                    foreground="#333333"
                />
              </div>
            </div>
          </a-space>
          
          <a-button 
              type="outline" 
              long 
              style="margin-top: 16px;"
              @click="exportToPDF"
          >
            <template #icon>
              <icon-download />
            </template>
            导出为PDF
          </a-button>
        </div>

        <a-form-item style="margin-top: 24px; margin-bottom: 0;">
          <a-space>
            <a-button @click="closeInviteModal">取消</a-button>
            <a-button type="primary" html-type="submit" :loading="generating">
              生成邀请码
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 成员列表弹窗 -->
    <a-modal
        v-model:visible="membersModalVisible"
        :title="`${currentOrg?.name || ''} - 成员列表`"
        :width="700"
        :footer="false"
        @cancel="membersModalVisible = false"
        unmount-on-close
    >
      <a-spin :loading="membersLoading" style="width: 100%;">
        <a-list
            :bordered="false"
            :data="members"
            :pagination="false"
        >
          <template #item="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #avatar>
                  <a-avatar :size="40">
                    {{ item.username?.[0]?.toUpperCase() || 'U' }}
                  </a-avatar>
                </template>
                <template #title>
                  <a-space>
                    <span>{{ item.username }}</span>
                    <a-tag v-if="item.id === currentOrg?.ownerId" color="red">所有者</a-tag>
                  </a-space>
                </template>
                <template #description>
                  <a-typography-text type="secondary" style="font-size: 12px;">
                    {{ item.email }}
                  </a-typography-text>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </a-spin>
    </a-modal>

    <!-- 管理组织弹窗 -->
    <a-modal
        v-model:visible="manageModalVisible"
        :title="`管理组织 - ${currentOrg?.name || ''}`"
        :width="600"
        @ok="handleUpdateOrganization"
        @cancel="manageModalVisible = false"
        :ok-loading="updating"
    >
      <a-form :model="manageForm" layout="vertical">
        <a-form-item label="组织名称" field="name" :rules="[{ required: true, message: '请输入组织名称' }]">
          <a-input 
              v-model="manageForm.name" 
              placeholder="请输入组织名称"
              :max-length="50"
              show-word-limit
          />
        </a-form-item>
        
        <a-form-item label="组织描述" field="description">
          <a-textarea 
              v-model="manageForm.description" 
              placeholder="请输入组织描述（可选）"
              :rows="3"
              :max-length="200"
              show-word-limit
          />
        </a-form-item>
        
        <a-form-item label="最大成员数" field="maxMembers">
          <a-input-number 
              v-model="manageForm.maxMembers" 
              :min="1"
              :max="1000"
              placeholder="最大成员数"
              style="width: 100%"
          />
        </a-form-item>
        
        <a-form-item label="是否公开" field="published">
          <a-switch v-model="manageForm.published" />
          <a-typography-text type="secondary" style="margin-left: 8px;">
            公开后其他用户可以搜索并申请加入
          </a-typography-text>
        </a-form-item>
      </a-form>

      <template #footer>
        <a-space>
          <a-popconfirm
              content="确定要删除此组织吗？删除后无法恢复！"
              @ok="handleDeleteOrganization"
              :ok-loading="deleting"
          >
            <a-button type="outline" status="danger" :loading="deleting">
              删除组织
            </a-button>
          </a-popconfirm>
          <a-space>
            <a-button @click="manageModalVisible = false">取消</a-button>
            <a-button type="primary" @click="handleUpdateOrganization" :loading="updating">
              保存
            </a-button>
          </a-space>
        </a-space>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { 
  IconArrowLeft, 
  IconPlus, 
  IconUser, 
  IconUserAdd, 
  IconUserGroup, 
  IconSettings,
  IconCheckCircleFill,
  IconCopy,
  IconDownload
} from '@arco-design/web-vue/es/icon'
import QrcodeVue from 'qrcode.vue'
import { jsPDF } from 'jspdf'
import html2canvas from 'html2canvas'
import api from '../../api/index'
import { useAuth } from '../../composables/useAuth'
import dayjs from 'dayjs'

const router = useRouter()
const { getUserInfo } = useAuth()
const userInfo = getUserInfo

// 状态管理
const loading = ref(false)
const viewType = ref<'managed' | 'participated'>('managed')
const organizations = ref<any[]>([])
const createModalVisible = ref(false)
const creating = ref(false)
const inviteModalVisible = ref(false)
const generating = ref(false)
const membersModalVisible = ref(false)
const membersLoading = ref(false)
const currentOrg = ref<any>(null)
const members = ref<any[]>([])
const inviteResult = ref<any>(null)
const manageModalVisible = ref(false)
const updating = ref(false)
const deleting = ref(false)

// 创建组织表单
const createForm = ref({
  name: '',
  description: '',
  maxMembers: 50,
  published: false
})

// 邀请表单
const inviteForm = ref({
  role: 'MEMBER',
  maxUses: 1,
  expiresAt: null as any
})

// 管理组织表单
const manageForm = ref({
  name: '',
  description: '',
  maxMembers: 50,
  published: false
})

// 获取组织头像样式
const getOrgAvatarStyle = (name: string) => {
  const hash = [...(name || '')].reduce((acc, c) => acc + c.charCodeAt(0), 0)
  const gradients = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  ]
  return {
    background: gradients[hash % gradients.length],
    color: 'white',
    fontSize: '24px',
    fontWeight: 'bold'
  }
}

// 加载组织列表
const loadOrganizations = async () => {
  loading.value = true
  try {
    console.log('开始加载组织列表，类型:', viewType.value)
    const response = await api.organizationApi.getOrganizedOrganizations()
    console.log('组织列表响应:', response)
    
    if (response.code === 200 && response.data) {
      let orgs = response.data
      
      // 如果是"我管理的团队"，过滤出当前用户拥有的组织
      if (viewType.value === 'managed') {
        orgs = orgs.filter((org: any) => org.ownerId === userInfo.value?.id)
      }
      
      organizations.value = orgs.map((org: any) => ({
        ...org,
        currentMembers: org.currentMembers || 0,
        maxMembers: org.maxMembers || 50
      }))
      
      console.log('处理后的组织列表:', organizations.value)
    } else {
      Message.error(response.message || '加载组织列表失败')
      organizations.value = []
    }
  } catch (error: any) {
    console.error('加载组织列表失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '加载组织列表失败'
    Message.error(errorMessage)
    organizations.value = []
  } finally {
    loading.value = false
  }
}

// 切换视图类型
const handleViewTypeChange = () => {
  loadOrganizations()
}

// 返回
const goBack = () => {
  router.back()
}

// 显示创建组织弹窗
const showCreateModal = () => {
  createForm.value = {
    name: '',
    description: '',
    maxMembers: 50,
    published: false
  }
  createModalVisible.value = true
}

// 创建组织
const handleCreateOrganization = async () => {
  if (!createForm.value.name.trim()) {
    Message.warning('请输入组织名称')
    return
  }

  creating.value = true
  try {
    const response = await api.organizationApi.createOrganization(createForm.value)
    
    if (response.code === 200 && response.data) {
      Message.success('组织创建成功')
      createModalVisible.value = false
      await loadOrganizations()
    } else {
      Message.error(response.message || '创建组织失败')
    }
  } catch (error: any) {
    console.error('创建组织失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '创建组织失败'
    Message.error(errorMessage)
  } finally {
    creating.value = false
  }
}

// 显示邀请弹窗
const showInviteModal = (org: any) => {
  currentOrg.value = org
  inviteForm.value = {
    role: 'MEMBER',
    maxUses: 1,
    expiresAt: null
  }
  inviteResult.value = null
  inviteModalVisible.value = true
}

// 关闭邀请弹窗
const closeInviteModal = () => {
  inviteModalVisible.value = false
  inviteResult.value = null
  currentOrg.value = null
}

// 生成邀请码
const handleGenerateInvite = async () => {
  if (!currentOrg.value) {
    Message.warning('请选择组织')
    return
  }

  generating.value = true
  try {
    const data = {
      organizationId: currentOrg.value.id,
      role: inviteForm.value.role,
      maxUses: inviteForm.value.maxUses,
      expiresAt: inviteForm.value.expiresAt 
        ? dayjs(inviteForm.value.expiresAt).format('YYYY-MM-DDTHH:mm:ss')
        : null
    }
    
    const response = await api.organizationApi.createInvite(data)
    console.log('生成邀请码响应:', response)
    
    if (response.code === 200 && response.data) {
      inviteResult.value = response.data
      Message.success('邀请码生成成功')
    } else {
      Message.error(response.message || '生成邀请码失败')
    }
  } catch (error: any) {
    console.error('生成邀请码失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '生成邀请码失败'
    Message.error(errorMessage)
  } finally {
    generating.value = false
  }
}

// 复制邀请码
const copyInviteCode = async () => {
  if (!inviteResult.value?.inviteCode) return
  try {
    await navigator.clipboard.writeText(inviteResult.value.inviteCode)
    Message.success('邀请码已复制到剪贴板')
  } catch (error) {
    Message.error('复制失败')
  }
}

// 复制短链接
const copyShortLink = async () => {
  if (!inviteResult.value?.shortUrl) return
  try {
    await navigator.clipboard.writeText(inviteResult.value.shortUrl)
    Message.success('短链接已复制到剪贴板')
  } catch (error) {
    Message.error('复制失败')
  }
}

// 导出PDF
const exportToPDF = async () => {
  if (!inviteResult.value) {
    Message.warning('请先生成邀请码')
    return
  }

  try {
    const exportContent = document.querySelector('.invite-result') as HTMLElement
    if (!exportContent) {
      Message.error('未找到导出内容')
      return
    }

    const canvas = await html2canvas(exportContent, {
      scale: 2,
      useCORS: true,
    })

    const pdf = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4',
    })

    const imgWidth = 210
    const imgHeight = canvas.height * (imgWidth / canvas.width)

    pdf.addImage(
      canvas.toDataURL('image/jpeg', 0.95),
      'JPEG',
      10,
      10,
      imgWidth,
      imgHeight
    )

    pdf.save(`团队邀请二维码_${inviteResult.value.inviteCode}.pdf`)
    Message.success('PDF导出成功')
  } catch (error) {
    console.error('PDF导出失败:', error)
    Message.error('PDF导出失败，请重试')
  }
}

// 查看成员
const viewMembers = async (org: any) => {
  currentOrg.value = org
  membersModalVisible.value = true
  membersLoading.value = true
  
  try {
    const response = await api.organizationApi.getOrganizationMembers(org.id)
    
    if (response.code === 200 && response.data) {
      members.value = response.data
    } else {
      Message.error(response.message || '加载成员列表失败')
      members.value = []
    }
  } catch (error: any) {
    console.error('加载成员列表失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '加载成员列表失败'
    Message.error(errorMessage)
    members.value = []
  } finally {
    membersLoading.value = false
  }
}

// 管理组织
const manageOrganization = (org: any) => {
  currentOrg.value = org
  manageModalVisible.value = true
  manageForm.value = {
    name: org.name,
    description: org.description || '',
    maxMembers: org.maxMembers || 50,
    published: org.published || false
  }
}

// 更新组织
const handleUpdateOrganization = async () => {
  if (!currentOrg.value) return
  
  if (!manageForm.value.name.trim()) {
    Message.warning('请输入组织名称')
    return
  }

  updating.value = true
  try {
    const response = await api.organizationApi.updateOrganization(currentOrg.value.id, manageForm.value)
    
    if (response.code === 200 && response.data) {
      Message.success('组织更新成功')
      manageModalVisible.value = false
      await loadOrganizations()
    } else {
      Message.error(response.message || '更新组织失败')
    }
  } catch (error: any) {
    console.error('更新组织失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '更新组织失败'
    Message.error(errorMessage)
  } finally {
    updating.value = false
  }
}

// 删除组织
const handleDeleteOrganization = async () => {
  if (!currentOrg.value) return
  
  deleting.value = true
  try {
    const response = await api.organizationApi.deleteOrganization(currentOrg.value.id)
    
    if (response.code === 200) {
      Message.success('组织删除成功')
      manageModalVisible.value = false
      await loadOrganizations()
    } else {
      Message.error(response.message || '删除组织失败')
    }
  } catch (error: any) {
    console.error('删除组织失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '删除组织失败'
    Message.error(errorMessage)
  } finally {
    deleting.value = false
  }
}

// 生命周期
onMounted(() => {
  loadOrganizations()
})
</script>

<style scoped>
.org-management-page {
  padding: 24px;
  min-height: calc(100vh - 48px);
  background: #f7f8fa;
}

.header-card {
  margin-bottom: 24px;
}

:deep(.header-card .arco-card-body) {
  padding: 16px 20px;
}

.org-card {
  transition: all 0.3s ease;
  height: 100%;
}

.org-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.org-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

:deep(.org-card .arco-card-body) {
  padding: 16px;
}

:deep(.org-card .arco-card-actions) {
  padding: 12px 16px;
  border-top: 1px solid #f2f3f5;
}

.invite-result {
  margin-top: 16px;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .org-management-page {
    padding: 16px;
  }
}
</style>
