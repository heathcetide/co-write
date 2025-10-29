<template>
  <div class="yuque-documents">
    <!-- 顶部操作区 -->
    <div class="action-section">
      <div class="action-group">
        <button class="action-btn" @click="createDocument">
          <i class="iconfont icon-file-add"></i>
          <div class="btn-content">
            <span class="btn-title"><FileText class="lucide-icon" /> 新建文档</span>
            <span class="btn-desc">文档、表格、画板、数据表</span>
          </div>
        </button>
        <button class="action-btn" @click="createRepo">
          <i class="iconfont icon-repo-add"></i>
          <div class="btn-content">
            <span class="btn-title"><BookOpen class="lucide-icon" /> 新建知识库</span>
            <span class="btn-desc">使用知识库整理知识</span>
          </div>
        </button>
      </div>
      <div class="action-group">
        <button class="action-btn" @click="openTemplates">
          <i class="iconfont icon-template"></i>
          <div class="btn-content">
            <span class="btn-title"><Palette class="lucide-icon" /> 模板中心</span>
            <span class="btn-desc">从模板中获取灵感</span>
          </div>
        </button>
        <button class="action-btn" @click="useAI">
          <i class="iconfont icon-ai"></i>
          <div class="btn-content">
            <span class="btn-title"><Bot class="lucide-icon" /> AI帮你写</span>
            <span class="btn-desc">AI助手帮你一键生成文档</span>
          </div>
        </button>
      </div>
    </div>

    <!-- 文档分类区和筛选区（核心修复区域） -->
    <div class="combined-section">
      <div class="doc-section">
        <h2 class="section-title">文档</h2>
        <div class="tag-group">
          <button
              v-for="category in docCategories"
              :key="category.id"
              class="doc-tag"
              :class="{ active: activeCategory === category.id }"
              @click="filterByCategory(category.id)"
          >
            {{ category.label }}
          </button>
        </div>
      </div>
      <div class="filter-section">
        <div class="filter-dropdown" v-for="filter in docFilters" :key="filter.id">
          <button
              class="filter-btn"
              :class="{ active: activeFilter === filter.id }"
              @click="toggleDropdown(filter.id)"
          >
            {{ filter.label }}
            <i class="iconfont icon-arrow-down"></i>
          </button>
          <div class="dropdown-menu" v-if="activeDropdown === filter.id">
            <div
                v-for="item in filterOptions[filter.id]"
                :key="item.value"
                class="dropdown-item"
                @click="selectFilterItem(filter.id, item.value)"
            >
              {{ item.label }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 特色功能区 -->
    <div class="feature-section combined">
      <button class="feature-btn" @click="openFeature">
        <i class="iconfont icon-magic"></i>
        <span><Wand2 class="lucide-icon" /> 试试新建~ </span>
      </button>
      <div class="user-info-combined">
        <img class="user-avatar" :src="userInfo?.avatarUrl" />
        <span>{{ userInfo?.username }} / 默认知识库</span>
        <span class="date-info">{{ new Date().toISOString().split('T')[0] }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '../composables/useAuth'
import { FileText, BookOpen, Palette, Bot, Wand2 } from 'lucide-vue-next'

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
const activeDropdown = ref<string | null>(null)

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
const toggleDropdown = (filterId: string) => {
  activeDropdown.value = activeDropdown.value === filterId ? null : filterId
}

const selectFilterItem = (filterId: string, value: string) => {
  activeFilter.value = filterId
  console.log(`Selected ${filterId}: ${value}`)
  activeDropdown.value = null
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
</script>


<style scoped>
.yuque-documents {
  max-width: 1080px;
  margin: 0 auto;
  padding: 20px 24px;
  color: #0f172a;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

/* 操作区样式（保持不变） */
.action-section {
  display: flex;
  gap: 20px;
  margin-bottom: 32px;
}
.action-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.action-btn {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease, transform 0.12s ease;
  border: 1px solid #e5e7eb;
  text-align: left;
  width: 100%;
  background-color: #ffffff;
  color: #0f172a;
}
.action-btn i {
  font-size: 20px;
  margin-right: 12px;
  flex-shrink: 0;
  color: #2563eb;
}
.btn-content {
  display: flex;
  flex-direction: column;
}
.btn-title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 2px;
}
.btn-desc {
  font-size: 12px;
  color: #64748b;
}
.action-btn:hover {
  background-color: #f8fafc;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(2, 6, 23, 0.06);
}
.action-btn:active {
  background-color: #eef2ff;
  transform: translateY(0);
  box-shadow: inset 0 1px 2px rgba(2, 6, 23, 0.06);
}

/* 核心区域：分类标签 + 筛选按钮 */
.combined-section {
  display: flex;
  align-items: center; /* 垂直居中，避免高度差 */
  flex-wrap: nowrap;   /* 禁止换行！确保永远同行 */
  gap: 16px;           /* 按钮组间距 */
  margin-bottom: 20px;
}

/* 如果筛选按钮有下沉问题，补充： */
.filter-section {
  align-self: center; /* 强制筛选区垂直居中 */
}

/* 文档分类区 */
.doc-section {
  flex: 1; /* 占据主要宽度 */
  min-width: 0; /* 允许内容收缩 */
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #0f172a;
}
.tag-group {
  display: flex;
  gap: 12px; /* 增大分类按钮间距（原12px） */
  flex-wrap: wrap; /* 防止极端宽度下拥挤 */
}
.doc-tag {
  padding: 8px 14px; /* 精简内边距 */
  border-radius: 9999px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  white-space: nowrap; /* 防止文字换行 */
}
.doc-tag:hover {
  background: #f8fafc;
  color: #2563eb;
  border-color: #dbeafe;
}
.doc-tag.active {
  background: #2563eb;
  color: #ffffff;
  border-color: #2563eb;
}

/* 筛选区（核心修复） */
.filter-section {
  display: flex;
  gap: 12px; /* 筛选按钮间距 */
  align-items: center;
  /* 移除原padding-top: 38px，让筛选区与分类区顶部对齐 */
  min-width: 0;
}
.filter-dropdown {
  position: relative;
}
.filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  white-space: nowrap;
}
.filter-btn:hover {
  border-color: #dbeafe;
  color: #2563eb;
}
.filter-btn.active {
  background: #eff6ff;
  color: #2563eb;
  border-color: #bfdbfe;
}
.filter-btn i {
  font-size: 12px;
  transition: transform 0.2s;
}
.filter-btn.active i {
  transform: rotate(180deg);
}
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  width: 160px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(2, 6, 23, 0.06);
  z-index: 10;
  margin-top: 4px;
  overflow: hidden;
}
.dropdown-item {
  padding: 8px 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}
.dropdown-item:hover {
  background-color: #f8fafc;
  color: #2563eb;
}

/* 特色功能区（保持不变） */
.feature-section.combined {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
  padding: 10px 0;
}
.feature-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  color: #2563eb;
  font-size: 14px;
  cursor: pointer;
  background: none;
  border: none;
  transition: all 0.2s;
  border-radius: 6px;
}
.feature-btn:hover {
  background-color: #eff6ff;
}
.feature-btn:active {
  background-color: #dbeafe;
}
.feature-btn i {
  font-size: 18px;
}
.user-info-combined {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #555;
}
.user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}
.date-info {
  color: #94a3b8;
  margin-left: 12px;
}

/* lucide 图标在按钮标题内的统一尺寸与对齐 */
.lucide-icon {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  vertical-align: -3px;
  color: #2563eb;
}
</style>