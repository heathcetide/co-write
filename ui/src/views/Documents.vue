<template>
  <div class="yuque-documents">
    <!-- 顶部操作区 -->
    <a-row :gutter="16" class="action-section">
      <a-col :xs="24" :sm="12" :md="12" :lg="12">
        <a-card class="action-card" :hoverable="true" @click="createDocument">
          <div class="action-btn-content">
            <FileText class="action-icon" />
            <div class="btn-content">
              <span class="btn-title">新建文档</span>
              <span class="btn-desc">文档、表格、画板、数据表</span>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="12" :lg="12">
        <a-card class="action-card" :hoverable="true" @click="createRepo">
          <div class="action-btn-content">
            <BookOpen class="action-icon" />
            <div class="btn-content">
              <span class="btn-title">新建知识库</span>
              <span class="btn-desc">使用知识库整理知识</span>
            </div>
          </div>
        </a-card>
      </a-col>
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
    <a-list :bordered="false" class="documents-list">
      <a-list-item v-for="doc in sampleDocuments" :key="doc.id" class="document-item">
        <a-list-item-meta>
          <template #title>
            <h3 class="doc-title">{{ doc.title }}</h3>
          </template>
          <template #description>
            <p class="doc-desc">{{ doc.description }}</p>
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
            <span>{{ userInfo?.username }} / 默认知识库</span>
            <a-typography-text type="secondary">{{ new Date().toISOString().split('T')[0] }}</a-typography-text>
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
import { ref } from 'vue'
import { useAuth } from '../composables/useAuth'
import { FileText, BookOpen, Palette, Bot, Wand2 } from 'lucide-vue-next'
import { IconDown } from '@arco-design/web-vue/es/icon'
import DocumentShareDialog from '../components/DocumentShareDialog.vue'
import DocumentPermissionDialog from '../components/DocumentPermissionDialog.vue'
import AuditLogDialog from '../components/AuditLogDialog.vue'

// -----------------------
// 认证状态（用户信息）
// -----------------------
const { getUserInfo } = useAuth()
const userInfo = getUserInfo

// -----------------------
// 文档分类和筛选
// -----------------------
const activeCategory = ref('edited')
const activeFilter = ref('type')

const docCategories = [
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
    { value: 'doc', label: '文档' },
    { value: 'sheet', label: '表格' },
    { value: 'board', label: '画板' },
    { value: 'table', label: '数据表' }
  ],
  owner: [
    { value: 'me', label: '我的' },
    { value: 'team', label: '团队的' },
    { value: 'shared', label: '共享的' }
  ],
  creator: [
    { value: 'me', label: '我创建的' },
    { value: 'others', label: '他人创建的' }
  ]
}

// -----------------------
// 交互方法
// -----------------------
const selectFilterItem = (filterId: string, value: string) => {
  activeFilter.value = filterId
  console.log(`Selected ${filterId}: ${value}`)
}

const createDocument = () => {
  console.log('创建文档')
}

const createRepo = () => {
  console.log('创建知识库')
}

const openTemplates = () => {
  console.log('打开模板中心')
}

const useAI = () => {
  console.log('使用AI生成文档')
}

const filterByCategory = (category: string) => {
  activeCategory.value = category
  console.log('按分类筛选:', category)
}

const openFeature = () => {
  console.log('打开特色功能')
}

// -----------------------
// 文档列表和对话框状态
// -----------------------
const sampleDocuments = ref([
  {
    id: '1',
    title: '项目需求文档',
    description: '详细描述项目功能需求和用户故事',
    author: '张三',
    updatedAt: '2024-01-15'
  },
  {
    id: '2', 
    title: '技术架构设计',
    description: '系统架构图和技术选型说明',
    author: '李四',
    updatedAt: '2024-01-14'
  },
  {
    id: '3',
    title: 'API接口文档',
    description: 'RESTful API接口规范和示例',
    author: '王五',
    updatedAt: '2024-01-13'
  }
])

const currentDocumentId = ref('')
const showShareDialog = ref(false)
const showPermissionDialog = ref(false)
const showAuditDialog = ref(false)

const editDocument = (docId: string) => {
  // 跳转到编辑页面
  window.location.href = `/edit?id=${docId}`
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