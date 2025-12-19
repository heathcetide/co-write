<template>
  <aside class="right-sidebar" :class="{ collapsed }">
    <!-- 折叠按钮 -->
    <div class="collapse-trigger" @click="emit('toggle')">
      <ChevronRight v-if="collapsed" class="collapse-icon" />
      <ChevronLeft v-else class="collapse-icon" />
    </div>

    <!-- 展开时内容区域 -->
    <div v-if="!collapsed" class="sidebar-content">
      <!-- 当前知识库 - 卡片设计 -->
      <a-card
          class="repo-card"
          :hoverable="true"
          :bordered="false"
          @click="emit('switchRepo')"
      >
        <a-typography-text type="secondary" class="repo-label">当前知识库</a-typography-text>
        <a-typography-title :heading="6" class="repo-name">
          {{ currentRepo.name || '未选择' }}
        </a-typography-title>
      </a-card>

      <!-- 新建一级文档按钮 -->
      <a-button type="primary" long @click="createTopLevel" class="create-btn">
        <template #icon>
          <icon-plus />
        </template>
        新建一级文档
      </a-button>

      <NestedOutlineEditor
          v-model:items="documents"
          :knowledgeBaseId="currentRepo.id"
          :documents="documents"
          @select="handleSelect"
      />

      <!-- 插槽 -->
      <slot />
    </div>
  </aside>
</template>
<script lang="ts" setup>
import { defineProps, defineEmits, ref, watch } from 'vue'
import NestedOutlineEditor from '../components/NestedOutlineEditor.vue'
import api from '../api/index'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { IconPlus } from '@arco-design/web-vue/es/icon'

interface DocumentItem {
  id: string
  title: string
  level: number
  parentId: string | null
}

const props = defineProps<{
  collapsed: boolean
  currentRepo: { id: number; name: string }
  catalog: DocumentItem[]
}>()

const emit = defineEmits<{
  (e: 'toggle'): void
  (e: 'switchRepo'): void
  (e: 'selectItem', item: DocumentItem): void
}>()

const documents = ref<DocumentItem[]>([])

watch(
    () => props.catalog,
    (newVal) => {
      documents.value = [...newVal]
    },
    { immediate: true, deep: true }
)

const selectedDoc = ref<DocumentItem | null>(null)

const handleSelect = (doc: DocumentItem) => {
  selectedDoc.value = doc
  emit('selectItem', doc)
}

const createTopLevel = async () => {
  const newDoc = {
    title: '新一级文档',
    knowledgeBaseId: props.currentRepo.id,
    parentId: null,
    level: 0,
    order: documents.value.length,
  }
  try {
    const res = await api.documentApi.createDocument(newDoc)
    const created = res.data
    documents.value.push(created)
  } catch (e) {
    console.error('创建失败', e)
  }
}
</script>

<style scoped>
.right-sidebar {
  position: relative;
  width: 280px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  overflow-y: auto;
  overflow-x: hidden;
  border-left: 1px solid #e8e0f5;
  box-shadow: 0 2px 12px rgba(148, 108, 230, 0.08);
  height: 100vh;
}

/* 自定义滚动条样式 */
.right-sidebar::-webkit-scrollbar {
  width: 6px;
}

.right-sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.right-sidebar::-webkit-scrollbar-thumb {
  background: #c9cdd4;
  border-radius: 3px;
}

.right-sidebar::-webkit-scrollbar-thumb:hover {
  background: #86909c;
}

.right-sidebar.collapsed {
  width: 24px;
  padding: 1.5rem 0.5rem;
  overflow: visible;
}

/* 折叠按钮 */
.collapse-trigger {
  position: absolute;
  top: 16px;
  left: 5px;
  width: 24px;
  height: 24px;
  background: #ffffff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(148, 108, 230, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 10;
  border: 1px solid #e8e0f5;
  transition: all 0.2s ease;
}

.collapse-icon {
  width: 16px;
  height: 16px;
  color: #6b7280;
}

.collapse-trigger:hover {
  background: #f0e9ff;
  transform: scale(1.05);
}

/* 当前知识库区域 */
.repo-card {
  margin-bottom: 1.5rem;
  cursor: pointer;
}

:deep(.repo-card .arco-card-body) {
  padding: 1rem;
}

.repo-label {
  display: block;
  margin-bottom: 6px;
}

.repo-name {
  margin: 0;
}

.create-btn {
  margin-bottom: 1.5rem;
}

.repo-icon {
  color: #946ce6;
  opacity: 0.7;
}

/* 列表项样式 */
.sidebar-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  color: #4e5969;
  transition: all 0.2s ease;
  overflow: hidden;
  margin-bottom: 4px;
}

.hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 100%;
  background: linear-gradient(90deg, rgba(148, 108, 230, 0.1), transparent);
  transition: width 0.3s ease;
}

.sidebar-item:hover {
  background-color: #f3eeff;
  color: #946ce6;
}

.sidebar-item:hover .hover-effect {
  width: 100%;
}

.sidebar-icon {
  font-size: 16px;
  margin-right: 10px;
  color: #946ce6;
}

.sidebar-text {
  flex: 1;
}

.sidebar-sublist {
  padding-left: 24px;
  margin-top: 4px;
}

.subitem {
  font-size: 13px;
  padding: 8px 14px;
}

.subitem .sidebar-icon {
  font-size: 14px;
}

.parent > .sidebar-item-label {
  font-weight: 500;
}
</style>