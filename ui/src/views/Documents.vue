<template>
  <div class="yuque-documents">
    <!-- 顶部操作区 -->
    <a-row :gutter="16" class="action-section">
      <a-col :xs="24" :sm="12" :md="12" :lg="12">
        <a-card class="action-card" :hoverable="true" @click="openTemplates">
          <div class="action-btn-content">
            <Palette class="action-icon" />
            <div class="btn-content">
              <span class="btn-title">模板中心</span>
              <span class="btn-desc">从模板中获取灵感</span>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="12" :lg="12">
        <a-card class="action-card" :hoverable="true" @click="useAI">
          <div class="action-btn-content">
            <Bot class="action-icon" />
            <div class="btn-content">
              <span class="btn-title">AI帮你写</span>
              <span class="btn-desc">AI助手帮你一键生成文档</span>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 文档分类区和筛选区 -->
    <a-card class="filter-card" :bordered="false">
      <a-row :gutter="16" align="middle">
        <a-col :xs="24" :sm="12" :md="12" :lg="12">
          <h2 class="section-title">文档</h2>
          <a-space wrap>
            <a-tag
                v-for="category in docCategories"
                :key="category.id"
                :color="activeCategory === category.id ? 'blue' : 'gray'"
                :checkable="true"
                :checked="activeCategory === category.id"
                @click="filterByCategory(category.id)"
                class="doc-tag"
            >
              {{ category.label }}
            </a-tag>
          </a-space>
        </a-col>
        <a-col :xs="24" :sm="12" :md="12" :lg="12">
          <a-space>
            <a-dropdown
                v-for="filter in docFilters"
                :key="filter.id"
                trigger="click"
                @select="(key) => selectFilterItem(filter.id, key as string)"
            >
              <a-button type="outline" :class="{ 'filter-active': activeFilter === filter.id }">
                {{ filter.label }}
                <template #icon>
                  <icon-down />
                </template>
              </a-button>
              <template #content>
                <a-doption
                    v-for="item in filterOptions[filter.id]"
                    :key="item.value"
                    :value="item.value"
                >
                  {{ item.label }}
                </a-doption>
              </template>
            </a-dropdown>
          </a-space>
        </a-col>
      </a-row>
    </a-card>

    <!-- 文档列表区域 -->
    <a-list 
        :bordered="false" 
        class="documents-list"
        :loading="loading"
        v-if="filteredDocuments.length > 0 || loading"
    >
      <a-list-item v-for="doc in filteredDocuments" :key="doc.id" class="document-item">
        <a-list-item-meta>
          <template #title>
            <h3 class="doc-title">{{ doc.title }}</h3>
          </template>
          <template #description>
            <p class="doc-desc" v-if="doc.description">{{ doc.description }}</p>
            <div class="doc-meta">
              <a-typography-text type="secondary">{{ doc.author }}</a-typography-text>
              <a-typography-text type="secondary" class="doc-date">{{ doc.updatedAt }}</a-typography-text>
            </div>
          </template>
        </a-list-item-meta>
        <template #actions>
          <a-space>
            <a-button type="outline" size="small" @click="editDocument(doc.id)">编辑</a-button>
            <a-button type="outline" size="small" @click="openShareDialog(doc.id)">分享</a-button>
            <a-button type="outline" size="small" @click="openPermissionDialog(doc.id)">权限</a-button>
            <a-button type="outline" size="small" @click="openAuditDialog(doc.id)">审计</a-button>
          </a-space>
        </template>
      </a-list-item>
    </a-list>
    
    <!-- 空状态 -->
    <a-empty 
        v-else-if="!loading" 
        description="暂无文档"
        style="margin: 40px 0;"
    />
    
    <!-- 分页组件 -->
    <a-pagination
        v-if="total > pageSize && !loading"
        :current="currentPage"
        :total="total"
        :page-size="pageSize"
        :show-total="true"
        :show-page-size="true"
        @change="handlePageChange"
        @page-size-change="handlePageSizeChange"
        style="margin-top: 24px; text-align: center;"
    />

    <!-- 特色功能区 -->
    <a-card class="feature-section" :bordered="false">
      <a-row justify="space-between" align="center">
        <a-col>
          <a-button type="text" @click="openFeature">
            <template #icon>
              <Wand2 class="lucide-icon" />
            </template>
            试试新建~
          </a-button>
        </a-col>
        <a-col>
          <a-space>
            <a-avatar :size="24" :src="userInfo?.avatarUrl">
              {{ userInfo?.username?.[0] }}
            </a-avatar>
            <span>{{ userInfo?.username }} / {{ currentKnowledgeBaseName }}</span>
            <a-typography-text type="secondary">{{ dayjs().format('YYYY-MM-DD') }}</a-typography-text>
          </a-space>
        </a-col>
      </a-row>
    </a-card>

    <!-- 对话框组件 -->
    <DocumentShareDialog
      :visible="showShareDialog"
      :documentId="currentDocumentId"
      @close="showShareDialog = false"
    />

    <DocumentPermissionDialog
      :visible="showPermissionDialog"
      :documentId="currentDocumentId"
      @close="showPermissionDialog = false"
    />

    <AuditLogDialog
      :visible="showAuditDialog"
      :documentId="currentDocumentId"
      @close="showAuditDialog = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import { Palette, Bot, Wand2 } from 'lucide-vue-next'
import { IconDown } from '@arco-design/web-vue/es/icon'
import { Message } from '@arco-design/web-vue'
import DocumentShareDialog from '../components/DocumentShareDialog.vue'
import DocumentPermissionDialog from '../components/DocumentPermissionDialog.vue'
import AuditLogDialog from '../components/AuditLogDialog.vue'
import api from '../api/index'
import dayjs from 'dayjs'

// -----------------------
// 路由和认证状态
// -----------------------
const router = useRouter()
const route = useRoute()
const { getUserInfo } = useAuth()
const userInfo = getUserInfo

// -----------------------
// 文档分类和筛选
// -----------------------
const activeCategory = ref('all')
const activeFilter = ref('')
const selectedType = ref('all')
const selectedOwner = ref('all')
const selectedCreator = ref('all')

const docCategories = [
  { id: 'all', label: '全部' },
  { id: 'edited', label: '编辑过' },
  { id: 'viewed', label: '浏览过' },
  { id: 'liked', label: '我点赞的' },
  { id: 'commented', label: '我评论过' }
]

const docFilters = [
  { id: 'type', label: '类型' },
  { id: 'owner', label: '归属' },
  { id: 'creator', label: '创建者' }
]

const filterOptions = {
  type: [
    { value: 'all', label: '✓ 所有' },
    { value: 'DOC', label: '文档' },
    { value: 'FOLDER', label: '文件夹' }
  ],
  owner: [
    { value: 'all', label: '✓ 所有' },
    { value: 'me', label: '我的' },
    { value: 'team', label: '团队的' },
    { value: 'shared', label: '共享的' }
  ],
  creator: [
    { value: 'all', label: '✓ 所有' },
    { value: 'me', label: '我创建的' },
    { value: 'others', label: '他人创建的' }
  ]
}

// -----------------------
// 文档列表状态
// -----------------------
const documents = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const currentKnowledgeBaseId = ref<number | null>(null)
const currentKnowledgeBaseName = ref<string>('默认知识库')
const currentKnowledgeBase = ref<any>(null) // 存储当前知识库的完整信息

// -----------------------
// 筛选后的文档列表
// -----------------------
const filteredDocuments = computed(() => {
  let result = [...documents.value]

  // 类型筛选 - 支持DOC和FOLDER
  if (selectedType.value !== 'all') {
    if (selectedType.value === 'DOC') {
      result = result.filter(doc => doc.type === 'DOC')
    } else if (selectedType.value === 'FOLDER') {
      result = result.filter(doc => doc.type === 'FOLDER')
    }
  }

  // 归属筛选
  if (selectedOwner.value === 'me') {
    // 我的文档：所有者是我
    result = result.filter(doc => doc.ownerId === userInfo.value?.id)
  } else if (selectedOwner.value === 'team') {
    // 团队文档：知识库属于组织的文档
    if (currentKnowledgeBase.value?.organizationId) {
      result = result.filter(doc => doc.knowledgeBaseId === currentKnowledgeBase.value.id)
    } else {
      // 如果当前知识库不是组织知识库，则显示空
      result = []
    }
  } else if (selectedOwner.value === 'shared') {
    // 共享文档：有共享链接的文档（这里暂时通过知识库类型判断，后续可以优化为查询共享表）
    // 暂时显示所有文档，后续可以优化
    // result = result.filter(doc => doc.shared === true)
  }

  // 创建者筛选
  if (selectedCreator.value === 'me') {
    result = result.filter(doc => doc.ownerId === userInfo.value?.id)
  } else if (selectedCreator.value === 'others') {
    result = result.filter(doc => doc.ownerId !== userInfo.value?.id)
  }

  return result
})

// -----------------------
// 加载文档列表
// -----------------------
const loadDocuments = async () => {
  if (!currentKnowledgeBaseId.value) {
    console.log('没有知识库ID，无法加载文档列表')
    documents.value = []
    return
  }

  console.log('开始加载文档列表，知识库ID:', currentKnowledgeBaseId.value)
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      size: pageSize.value,
      knowledgeBaseId: currentKnowledgeBaseId.value
    }

    console.log('发送请求，参数:', params)
    const response = await api.documentApi.getDocuments(params)
    console.log('收到响应:', response)
    
    if (response.code === 200 && response.data) {
      const pageData = response.data
      documents.value = (pageData.records || pageData.list || []).map((doc: any) => ({
        id: String(doc.id),
        title: doc.title || '未命名文档',
        description: doc.metadata ? (typeof doc.metadata === 'string' ? JSON.parse(doc.metadata).description : doc.metadata.description) : '',
        type: doc.type || 'DOC',
        ownerId: doc.ownerId,
        author: `用户${doc.ownerId}`, // 暂时显示用户ID，后续可以优化为用户名
        updatedAt: doc.updatedAt ? dayjs(doc.updatedAt).format('YYYY-MM-DD') : dayjs().format('YYYY-MM-DD'),
        createdAt: doc.createdAt ? dayjs(doc.createdAt).format('YYYY-MM-DD') : dayjs().format('YYYY-MM-DD'),
        knowledgeBaseId: doc.knowledgeBaseId
      }))
      total.value = pageData.total || 0
    } else {
      Message.error(response.message || '加载文档列表失败')
      documents.value = []
    }
  } catch (error: any) {
    console.error('加载文档列表失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '加载文档列表失败'
    Message.error(errorMessage)
    documents.value = []
  } finally {
    loading.value = false
  }
}

// -----------------------
// 获取当前知识库ID
// -----------------------
const getCurrentKnowledgeBase = async () => {
  console.log('开始获取当前知识库，路由参数:', route.query)
  console.log('用户信息:', userInfo.value)
  
  // 优先从路由参数获取
  const kbIdFromRoute = route.query.knowledgeBaseId || route.query.id
  if (kbIdFromRoute) {
    console.log('从路由参数获取知识库ID:', kbIdFromRoute)
    currentKnowledgeBaseId.value = Number(kbIdFromRoute)
    // 获取知识库信息
    try {
      const kbRes = await api.knowledgeBaseApi.getKnowledgeBaseById(Number(kbIdFromRoute))
      if (kbRes.code === 200 && kbRes.data) {
        currentKnowledgeBase.value = kbRes.data
        currentKnowledgeBaseName.value = kbRes.data.name || '默认知识库'
      }
    } catch (error) {
      console.error('获取知识库信息失败:', error)
    }
    await loadDocuments()
    return
  }

  // 如果没有路由参数，尝试获取个人知识库或组织知识库
  try {
    // 先尝试获取个人知识库
    console.log('尝试获取个人知识库')
    const personalRes = await api.knowledgeBaseApi.getPersonalKnowledgeBases()
    if (personalRes.code === 200 && personalRes.data && personalRes.data.length > 0) {
      console.log('找到个人知识库:', personalRes.data)
      currentKnowledgeBaseId.value = personalRes.data[0].id
      currentKnowledgeBaseName.value = personalRes.data[0].name || '默认知识库'
      // 获取知识库完整信息
      try {
        const kbRes = await api.knowledgeBaseApi.getKnowledgeBaseById(personalRes.data[0].id)
        if (kbRes.code === 200 && kbRes.data) {
          currentKnowledgeBase.value = kbRes.data
        }
      } catch (error) {
        console.error('获取知识库信息失败:', error)
      }
      await loadDocuments()
      return
    }
    
    // 如果没有个人知识库，尝试获取组织知识库
    const orgId = userInfo.value?.currentOrganizationId
    if (orgId) {
      console.log('尝试获取组织知识库，组织ID:', orgId)
      const orgRes = await api.knowledgeBaseApi.getOrganizationKnowledgeBases(orgId)
      if (orgRes.code === 200 && orgRes.data && orgRes.data.length > 0) {
        console.log('找到组织知识库:', orgRes.data)
        currentKnowledgeBaseId.value = orgRes.data[0].id
        currentKnowledgeBaseName.value = orgRes.data[0].name || '默认知识库'
        // 获取知识库完整信息
        try {
          const kbRes = await api.knowledgeBaseApi.getKnowledgeBaseById(orgRes.data[0].id)
          if (kbRes.code === 200 && kbRes.data) {
            currentKnowledgeBase.value = kbRes.data
          }
        } catch (error) {
          console.error('获取知识库信息失败:', error)
        }
        await loadDocuments()
        return
      }
    }
    
    console.warn('未找到任何知识库')
    Message.warning('未找到知识库，请先创建知识库')
  } catch (error) {
    console.error('获取知识库列表失败:', error)
    Message.error('获取知识库列表失败: ' + (error as any).message)
  }
}

// -----------------------
// 交互方法
// -----------------------
const selectFilterItem = (filterId: string, value: string) => {
  activeFilter.value = filterId
  if (filterId === 'type') {
    selectedType.value = value
  } else if (filterId === 'owner') {
    selectedOwner.value = value
  } else if (filterId === 'creator') {
    selectedCreator.value = value
  }
}


const openTemplates = () => {
  Message.info('模板中心功能')
}

const useAI = () => {
  Message.info('AI生成文档功能')
}

const filterByCategory = (category: string) => {
  activeCategory.value = category
  // 分类筛选可能需要额外的接口支持，暂时先不过滤
  // 后续可以根据category调用不同的接口
}

const openFeature = () => {
  Message.info('特色功能')
}

// -----------------------
// 文档操作
// -----------------------
const currentDocumentId = ref('')
const showShareDialog = ref(false)
const showPermissionDialog = ref(false)
const showAuditDialog = ref(false)

const editDocument = (docId: string) => {
  router.push(`/documents?id=${docId}`)
}

const openShareDialog = (docId: string) => {
  currentDocumentId.value = docId
  showShareDialog.value = true
}

const openPermissionDialog = (docId: string) => {
  currentDocumentId.value = docId
  showPermissionDialog.value = true
}

const openAuditDialog = (docId: string) => {
  currentDocumentId.value = docId
  showAuditDialog.value = true
}

// -----------------------
// 分页处理
// -----------------------
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadDocuments()
}

const handlePageSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadDocuments()
}

// -----------------------
// 生命周期
// -----------------------
onMounted(() => {
  console.log('Documents页面挂载，开始获取知识库和文档列表')
  getCurrentKnowledgeBase()
})

// 监听路由变化
watch(() => route.query, (newQuery) => {
  console.log('路由参数变化:', newQuery)
  if (newQuery.knowledgeBaseId || newQuery.id) {
    getCurrentKnowledgeBase()
  }
}, { immediate: true })
</script>


<style scoped>
.yuque-documents {
  max-width: 1080px;
  margin: 0 auto;
  padding: 20px 24px;
  color: #1d2129;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

/* 操作区样式 */
.action-section {
  margin-bottom: 32px;
}

.action-card {
  cursor: pointer;
  transition: all 0.2s ease;
}

:deep(.action-card .arco-card-body) {
  padding: 16px;
}

.action-btn-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-icon {
  width: 20px;
  height: 20px;
  color: #165dff;
  flex-shrink: 0;
}

.btn-content {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.btn-title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 2px;
  color: #1d2129;
}

.btn-desc {
  font-size: 12px;
  color: #86909c;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 24px;
}

:deep(.filter-card .arco-card-body) {
  padding: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1d2129;
}

.doc-tag {
  cursor: pointer;
}

.filter-active {
  background-color: #e8f3ff !important;
  border-color: #165dff !important;
  color: #165dff !important;
}

/* 特色功能区 */
.feature-section {
  margin: 20px 0;
}

:deep(.feature-section .arco-card-body) {
  padding: 12px 16px;
}

.lucide-icon {
  width: 18px;
  height: 18px;
  vertical-align: -3px;
  color: #165dff;
}

/* 文档列表样式 */
.documents-list {
  margin: 24px 0;
}

.document-item {
  transition: all 0.2s;
}

.document-item:hover {
  background-color: #f7f8fa;
}

.doc-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin: 0 0 8px 0;
}

.doc-desc {
  font-size: 14px;
  color: #86909c;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.doc-meta {
  display: flex;
  gap: 16px;
  align-items: center;
}

.doc-date {
  margin-left: 8px;
}
</style>