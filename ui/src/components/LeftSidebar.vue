<template>
  <aside class="left-sidebar" :class="{ collapsed }">
    <!-- 侧边栏折叠按钮 -->
    <div class="collapse-trigger" @click="toggleSidebar">
<!--      <IconMdiChevronRight v-if="collapsed" />-->
<!--      <IconMdiChevronLeft v-else />-->
    </div>

    <!-- 用户信息卡片 -->
    <div
        class="user-panel-wrapper"
        @mouseenter="onUserMouseEnter"
        @mouseleave="onUserMouseLeave"
    >
      <div class="user-org-card">
        <div class="user-org">
          <img class="avatar" :src="userInfo?.avatarUrl" alt="avatar" />
          <div class="user-meta" v-if="!collapsed">
            <div class="nickname">👤 {{ userInfo?.username }}</div>
          </div>
        </div>
      </div>

      <!-- 用户信息面板 -->
      <transition name="fade">
        <div class="user-panel" v-show="showUserPanel">
          <div class="panel-header">
            <h3>{{ userInfo?.username }}</h3>
            <p class="user-type">普通用户，购买可享更多权益</p>
            <button class="upgrade-btn">购买</button>
          </div>
          <div class="panel-divider"></div>
          <ul class="panel-menu">
            <li class="panel-item" @click="goToCreationCenter">创作中心</li>
            <li class="panel-item" @click="goToSettings">设置</li>
            <li class="panel-item" @click="logout">退出登录</li>
          </ul>
        </div>
      </transition>
    </div>

    <!-- 组织信息栏 -->
    <div
        class="org-info-wrapper"
        @mouseenter="onOrgMouseEnter"
        @mouseleave="onOrgMouseLeave"
        v-if="!collapsed"
    >
      <div class="org-info-card">
        <i class="iconfont icon-organization"></i>
        <span class="org-prefix"><strong>当前所属的组织 :  </strong></span>
        <span class="org-name"><strong>{{ currentOrg?.name || "还未选择组织噢"}}</strong></span>
        <i class="iconfont icon-chevron-right"></i>
        <div class="card-decoration"></div>
      </div>

      <!-- 组织工具提示 -->
      <transition name="tooltip-slide">
        <div class="org-tooltip" v-show="showOrgTooltip">
          <div class="tooltip-content">
            <!-- 组织管理中心入口 -->
            <div class="tooltip-header" @click="goToOrgManagement">
              <i class="iconfont icon-setting"></i>
              <span>进入组织管理中心</span>
              <!-- 交互图标 -->
              <div class="header-action">
                <i class="iconfont icon-external-link"></i>
                <i class="iconfont icon-arrow-right"></i>
              </div>
            </div>

            <!-- 可滚动组织列表 -->
            <div class="org-list-container" @wheel.prevent="handleScroll">
              <div
                  v-for="org in visibleOrgs"
                  :key="org.id"
                  class="org-item"
                  :class="{ active: org.id === currentOrgId }"
                  @click="switchOrg(org)"
              >
                <i class="iconfont icon-org"></i>
                <span class="org-item-name">{{ org.name }}</span>
                <span class="org-item-meta">{{ org.currentMembers }}人</span>
              </div>
              <div class="scroll-indicator" v-if="showScrollIndicator">
                <i class="iconfont icon-chevron-down"></i>
              </div>
            </div>
          </div>
          <div class="tooltip-arrow"></div>
        </div>
      </transition>
    </div>

    <!-- 搜索框 -->
    <div class="search-container" v-if="!collapsed">
      <div class="search-wrapper">
        <i class="iconfont icon-search"></i>
        <input
            type="text"
            placeholder="搜索 Ctrl+J"
            class="search-input"
        />
      </div>
      <button class="add-btn">
        <div class="plus-icon">+</div>
      </button>
    </div>

    <!-- 功能菜单项 -->
    <nav class="menu" v-if="!collapsed">
      <div
          class="menu-item"
          @click="handleMenuClick(startCreateItem)"
          :class="{ active: selectedMenuItem === startCreateItem.id }"
      >
        <i class="iconfont icon-create" />
        <span class="label">✏️ {{ startCreateItem.label }}</span>
        <span class="hover-effect"></span>
      </div>

      <div
          v-for="item in menuItems"
          :key="item.id"
          class="menu-item"
          @click="handleMenuClick(item)"
          :class="{ active: selectedMenuItem === item.id }"
      >
        <i :class="['iconfont', item.icon]" />
        <span class="label">{{ item.emoji }} {{ item.label }}</span>
        <span class="hover-effect"></span>
      </div>
    </nav>

    <!-- 知识库区域 -->
    <div class="repos" v-if="!collapsed">
      <div class="section-title-wrapper">
        <h3 class="section-title">📚 我的知识库</h3>
        <button class="add-repo-btn" @click="isCreateModalVisible = true"><strong>+</strong></button>
      </div>
      <ul class="repo-list">
        <li
            v-for="repo in repositories"
            :key="repo.id"
            class="repo-item"
            @click="selectRepository(repo)"
        >
          <i class="iconfont icon-folder" />
          <span class="repo-name">📁 {{ repo.name }}</span>
          <span class="hover-effect"></span>
        </li>
      </ul>
    </div>

    <!-- 创建知识库弹窗 -->
    <div v-if="isCreateModalVisible" class="modal-overlay">
      <div class="modal">
        <h3>新建知识库</h3>
        <input
            type="text"
            placeholder="请输入知识库名称"
            v-model="newRepoName"
            class="modal-input"
        />
        <div class="modal-actions">
          <button class="modal-btn cancel" @click="isCreateModalVisible = false">取消</button>
          <button class="modal-btn confirm" @click="handleCreateRepository">创建</button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import api from '../api/index'

/**
 * 组织数据结构定义
 * @property {number} id - 组织唯一标识
 * @property {string} name - 组织名称
 * @property {number} memberCount - 组织成员数量
 */
interface Org {
  id: number
  name: string
  currentMembers: number
}

/**
 * 菜单项数据结构定义
 * @property {string} id - 菜单项唯一标识
 * @property {string} label - 菜单项显示文本
 * @property {string} icon - 菜单项图标类名
 * @property {string} [emoji] - 可选的表情符号
 */
interface MenuItem {
  id: string
  label: string
  icon: string
  emoji?: string
}

/**
 * 知识库数据结构定义
 * @property {number|string} id - 知识库唯一标识
 * @property {string} name - 知识库名称
 */
interface Repo {
  id: number | string
  name: string
}

// 定义组件事件
const emit = defineEmits(['menuClick', 'repoClick'])

// 路由和认证相关
const router = useRouter()
const { getUserInfo, clearAuth } = useAuth()
const userInfo = getUserInfo

// ========== 状态管理 ==========
const collapsed = ref(false) // 侧边栏折叠状态
const showUserPanel = ref(false) // 用户面板显示状态
const selectedMenuItem = ref('startCreate') // 当前选中的菜单项
const isCreateModalVisible = ref(false) // 知识库创建弹窗显示状态
const newRepoName = ref('') // 新知识库名称
const repositories = ref<Repo[]>([]) // 知识库列表
const showOrgTooltip = ref(false) // 组织工具提示显示状态
const currentOrgId = ref(0) // 当前组织ID
const scrollPosition = ref(0) // 组织列表滚动位置

// ========== 数据定义 ==========
// 组织列表数据
const orgList = ref<Org[]>([])

// 菜单项数据
const menuItems = ref<MenuItem[]>([
  { id: 'ai/documents', label: 'AI 写作', icon: 'icon-robot', emoji: '🤖' },
  { id: 'diagrams', label: '绘图', icon: 'icon-diagram', emoji: '🧩' }
])

// 开始创作菜单项
const startCreateItem = {
  id: '/',
  label: '开始创作',
  icon: 'icon-create'
}

// ========== 计算属性 ==========
/**
 * 获取当前组织信息
 * @returns {Org|undefined} 当前组织对象
 */
const currentOrg = computed(() => {
  return orgList.value.find(org => org.id === currentOrgId.value)
})

/**
 * 获取可见的组织列表（基于滚动位置）
 * @returns {Org[]} 当前可见的组织列表
 */
const visibleOrgs = computed(() => {
  return orgList.value.slice(scrollPosition.value, scrollPosition.value + 3)
})

/**
 * 是否显示滚动指示器
 * @returns {boolean} 是否还有更多组织可以滚动查看
 */
const showScrollIndicator = computed(() => {
  return scrollPosition.value < orgList.value.length - 3
})

// ========== 方法定义 ==========
/**
 * 切换侧边栏折叠状态
 */
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
  if (collapsed.value) showUserPanel.value = false
}

/**
 * 处理菜单点击事件
 * @param {MenuItem} item - 被点击的菜单项
 */
const handleMenuClick = (item: MenuItem) => {
  selectedMenuItem.value = item.id
  emit('menuClick', item)
}

/**
 * 选择知识库
 * @param {Repo} repo - 被选中的知识库
 */
const selectRepository = (repo: Repo) => {
  emit('repoClick', repo)
}

/**
 * 跳转到创作中心
 */
const goToCreationCenter = () => {
  router.push('/back/stats')
}

/**
 * 跳转到设置页面
 */
const goToSettings = () => {
  router.push('/back/settings')
}

/**
 * 退出登录
 */
const logout = async () => {
  try {
    clearAuth()
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    alert('退出登录失败，请稍后再试')
  }
}

/**
 * 加载知识库列表
 */
const loadRepositories = async () => {
  try {
    const res = await api.knowledgeBaseApi.getOrganizationKnowledgeBases(currentOrgId.value)
    repositories.value = res.data || []
  } catch (err) {
    console.error('加载知识库失败', err)
  }
}

/**
 * 创建新知识库
 */
const handleCreateRepository = async () => {
  if (!newRepoName.value.trim()) return

  try {
    await api.knowledgeBaseApi.createPersonalKnowledgeBase({ name: newRepoName.value })
    newRepoName.value = ''
    isCreateModalVisible.value = false
    await loadRepositories()
  } catch (err) {
    console.error('创建知识库失败', err)
  }
}

/**
 *  快速查询当前用户加入的全部组织
 */
const getOrganizationListQuickly = async () => {
  try {
    const res = await api.organizationApi.getOrganizationListQuickly();
    orgList.value = res.data || []
  } catch (err) {
    console.error('查询组织失败', err)
  }
}

/**
 * 处理组织列表滚动
 * @param {WheelEvent} e - 鼠标滚轮事件
 */
const handleScroll = (e: WheelEvent) => {
  if (e.deltaY > 0) {
    // 向下滚动
    if (scrollPosition.value < orgList.value.length - 3) {
      scrollPosition.value += 1
    }
  } else {
    // 向上滚动
    if (scrollPosition.value > 0) {
      scrollPosition.value -= 1
    }
  }
}

/**
 * 切换组织
 * @param {Org} org - 要切换到的组织
 */
const  switchOrg = async (org: Org) => {
  currentOrgId.value = org.id
  showOrgTooltip.value = false
  // 这里可以添加实际切换组织的逻辑
  await loadRepositories()
}

/**
 * 进入组织管理中心
 */
const goToOrgManagement = () => {
  router.push('/organization/management')
}

// 定时器变量
let userPanelHideTimer: ReturnType<typeof setTimeout> | null = null
let orgTooltipHideTimer: ReturnType<typeof setTimeout> | null = null

// 用户面板 hover 逻辑
function onUserMouseEnter() {
  if (userPanelHideTimer) {
    clearTimeout(userPanelHideTimer)
    userPanelHideTimer = null
  }
  showUserPanel.value = true
}

function onUserMouseLeave() {
  userPanelHideTimer = setTimeout(() => {
    showUserPanel.value = false
  }, 100)
}

// 组织 tooltip hover 逻辑
async function onOrgMouseEnter() {
  if (orgTooltipHideTimer) {
    clearTimeout(orgTooltipHideTimer)
    orgTooltipHideTimer = null
  }
  showOrgTooltip.value = true
  // TODO: 获取组织列表数据并显示
  await getOrganizationListQuickly();
}

function onOrgMouseLeave() {
  orgTooltipHideTimer = setTimeout(() => {
    showOrgTooltip.value = false
  }, 100)
}

// 组件挂载时时默认执行的初始化函数:
onMounted(() => {
  // loadRepositories();       // 加载知识库
  getOrganizationListQuickly();  // 获取组织列表
});
</script>


<style scoped>
/* ========== 基础样式 ========== */
.left-sidebar {
  position: relative;
  width: 280px;
  background-color: #f8f5ff; /* 浅紫色背景 */
  border-right: 1px solid #e8e0f5; /* 浅紫色边框 */
  height: 100vh; /* 全屏高度 */
  padding: 1.5rem 1rem 0; /* 内边距 */
  transition: all 0.3s ease; /* 平滑过渡效果 */
  overflow: visible; /* 允许内容溢出 */
  box-shadow: 0 2px 12px rgba(148, 108, 230, 0.08); /* 柔和阴影 */
}

/* 折叠状态下的样式 */
.left-sidebar.collapsed {
  width: 45px;
  padding: 1.5rem 0.5rem 0;
}

/* ========== 折叠按钮样式 ========== */
.collapse-trigger {
  position: absolute;
  top: 2px;
  right: 5px;
  width: 30px;
  height: 30px;
  background: #ffffff; /* 白色背景 */
  border-radius: 50%; /* 圆形 */
  box-shadow: 0 2px 8px rgba(148, 108, 230, 0.2); /* 阴影效果 */
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 1001; /* 确保在最上层 */
  transition: all 0.2s ease; /* 过渡效果 */
  border: 1px solid #e8e0f5; /* 边框 */
}

/* 悬停效果 */
.collapse-trigger:hover {
  background: #f0e9ff; /* 浅紫色背景 */
  transform: scale(1.05); /* 轻微放大 */
}


/* ========== 用户卡片样式 ========== */

.user-org-card {
  border-radius: 12px; /* 圆角 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); /* 自定义缓动效果 */
  z-index: 10;
  position: relative;
}

/* 悬停效果 */
.user-org-card:hover {
  transform: translateY(-2px); /* 上浮效果 */
  box-shadow: 0 4px 12px rgba(148, 108, 230, 0.15); /* 阴影增强 */
}

.user-org {
  display: flex;
  align-items: center;
}

/* 用户头像样式 */
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%; /* 圆形 */
  margin-right: 0.75rem; /* 右边距 */
  object-fit: cover; /* 保证图片比例 */
  border: 2px solid #f0e9ff; /* 浅紫色边框 */
  box-shadow: 0 2px 4px rgba(148, 108, 230, 0.1); /* 阴影 */
}

.user-meta {
  flex: 1; /* 占据剩余空间 */
  display: flex;
  flex-direction: column; /* 垂直排列 */
}

/* 用户昵称样式 */
.nickname {
  font-size: 15px;
  font-weight: 600; /* 加粗 */
  color: #5e4dcd; /* 紫色文字 */
  margin-bottom: 6px; /* 下边距 */
}

/* ========== 组织信息栏样式 ========== */
.org-info-wrapper {
  position: relative;
  margin: 0.75rem 0 1.25rem; /* 外边距 */
  perspective: 1000px; /* 3D透视效果 */
  z-index: 100; /* 层级 */
}

.org-info-card {
  position: relative;
  display: flex;
  align-items: center;
  padding: 12px 14px; /* 内边距 */
  background: white; /* 白色背景 */
  border-radius: 10px; /* 圆角 */
  border: 1px solid rgba(138, 109, 232, 0.3); /* 半透明紫色边框 */
  box-shadow:
      inset 0 0 0 1px rgba(255, 255, 255, 0.8), /* 内阴影 */
      0 2px 8px rgba(148, 108, 230, 0.1); /* 外阴影 */
  cursor: pointer; /* 手型指针 */
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); /* 自定义缓动 */
  overflow: hidden; /* 隐藏溢出内容 */
  z-index: 1; /* 层级 */
}

/* 悬停效果 */
.org-info-card:hover {
  background: #f9f5ff; /* 浅紫色背景 */
  transform: translateY(-2px); /* 上浮效果 */
  border-color: rgba(138, 109, 232, 0.6); /* 边框颜色加深 */
  box-shadow:
      inset 0 0 0 1px rgba(255, 255, 255, 0.9), /* 内阴影增强 */
      0 6px 16px rgba(148, 108, 230, 0.2); /* 外阴影增强 */
}

/* 组织前缀文本样式 */
.org-prefix {
  font-size: 13px;
  color: #718096; /* 灰色文字 */
  margin-right: 4px; /* 右边距 */
  white-space: nowrap; /* 不换行 */
}

/* 组织名称样式 */
.org-name {
  font-size: 13px;
  font-weight: 500; /* 中等粗细 */
  color: #5e4dcd; /* 紫色文字 */
  white-space: nowrap; /* 不换行 */
  overflow: hidden; /* 隐藏溢出 */
  text-overflow: ellipsis; /* 显示省略号 */
  flex-grow: 1; /* 占据剩余空间 */
}

/* ========== 组织工具提示样式 ========== */
.org-tooltip {
  position: absolute;
  top: calc(100% + 8px); /* 定位在卡片下方 */
  left: 0;
  width: 100%;
  background: white; /* 白色背景 */
  border-radius: 12px; /* 圆角 */
  box-shadow:
      0 12px 28px rgba(0, 0, 0, 0.15), /* 大阴影 */
      0 0 0 1px rgba(138, 109, 232, 0.1); /* 边框效果 */
  border: 1px solid rgba(138, 109, 232, 0.2); /* 半透明紫色边框 */
  z-index: 100; /* 层级 */
  overflow: hidden; /* 隐藏溢出内容 */
  transform-origin: top center; /* 变换原点 */
}

/* 工具提示箭头 */
.tooltip-arrow {
  position: absolute;
  top: -6px;
  left: 20px;
  width: 12px;
  height: 12px;
  background: white; /* 白色背景 */
  transform: rotate(45deg); /* 旋转45度形成箭头 */
  border-left: 1px solid rgba(138, 109, 232, 0.2); /* 左边框 */
  border-top: 1px solid rgba(138, 109, 232, 0.2); /* 上边框 */
  z-index: -1; /* 在内容下方 */
  box-shadow: -2px -2px 4px rgba(0, 0, 0, 0.05); /* 阴影 */
}

/* ========== 组织管理中心头部样式 ========== */
.tooltip-header {
  display: flex;
  align-items: center;
  padding: 12px 16px; /* 内边距 */
  background: linear-gradient(135deg, #6a4dff 0%, #8a6de8 100%); /* 渐变背景 */
  color: white; /* 白色文字 */
  cursor: pointer; /* 手型指针 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); /* 自定义缓动 */
  position: relative; /* 相对定位 */
  overflow: hidden; /* 隐藏溢出内容 */
}

/* 悬停效果 */
.tooltip-header:hover {
  background: linear-gradient(135deg, #5a3dff 0%, #7a5dd8 100%); /* 渐变背景变深 */
  transform: translateY(-1px); /* 上浮效果 */
  box-shadow: 0 2px 8px rgba(106, 77, 255, 0.2); /* 阴影 */
}

.tooltip-header i {
  margin-right: 8px; /* 图标右边距 */
  font-size: 14px; /* 图标大小 */
  color: white; /* 白色图标 */
}

.tooltip-header span {
  flex: 1; /* 占据剩余空间 */
  font-size: 13px;
  font-weight: 600; /* 加粗 */
  letter-spacing: 0.5px; /* 字间距 */
}

/* 交互图标容器 */
.header-action {
  display: flex;
  align-items: center;
  margin-left: 8px; /* 左边距 */
  transition: all 0.3s ease; /* 过渡效果 */
}

.header-action i {
  font-size: 12px; /* 图标大小 */
  margin-left: 6px; /* 左边距 */
  transition: all 0.2s ease; /* 过渡效果 */
  opacity: 0.8; /* 半透明 */
}

/* 悬停时图标效果 */
.tooltip-header:hover .header-action i {
  transform: translateX(2px); /* 向右移动 */
  opacity: 1; /* 完全不透明 */
}

.tooltip-header:hover .icon-external-link {
  color: #ffffff; /* 白色 */
}

.tooltip-header:hover .icon-arrow-right {
  color: #f0e9ff; /* 浅紫色 */
}

/* 组织列表容器 */
.org-list-container {
  max-height: 132px; /* 最大高度 */
  overflow: hidden; /* 隐藏溢出 */
  position: relative; /* 相对定位 */
}

/* 组织项样式 */
.org-item {
  display: flex;
  align-items: center;
  padding: 10px 16px; /* 内边距 */
  cursor: pointer; /* 手型指针 */
  transition: all 0.2s ease; /* 过渡效果 */
  border-bottom: 1px solid rgba(138, 109, 232, 0.1); /* 底部边框 */
}

.org-item:last-child {
  border-bottom: none; /* 最后一项无边框 */
}

/* 悬停效果 */
.org-item:hover {
  background-color: rgba(138, 109, 232, 0.05); /* 浅紫色背景 */
}

/* 当前选中组织样式 */
.org-item.active {
  background-color: rgba(138, 109, 232, 0.1); /* 稍深的紫色背景 */
}

.org-item i {
  color: #8a6de8; /* 紫色图标 */
  font-size: 14px; /* 图标大小 */
  margin-right: 10px; /* 右边距 */
}

/* 组织名称样式 */
.org-item-name {
  flex: 1; /* 占据剩余空间 */
  font-size: 13px;
  color: #4a5568; /* 深灰色文字 */
}

/* 组织成员数量样式 */
.org-item-meta {
  font-size: 12px;
  color: #718096; /* 灰色文字 */
  margin-left: 8px; /* 左边距 */
}

/* 滚动指示器 */
.scroll-indicator {
  display: flex;
  justify-content: center;
  padding: 8px 0; /* 内边距 */
  color: #8a6de8; /* 紫色 */
  animation: bounce 2s infinite; /* 弹跳动画 */
}

/* 弹跳动画关键帧 */
@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
  40% {transform: translateY(-5px);}
  60% {transform: translateY(-3px);}
}

/* ========== 卡片装饰元素 ========== */
.card-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 3px; /* 宽度 */
  height: 100%; /* 全高 */
  background: linear-gradient(to bottom, #9b7cff, #6a4dff); /* 渐变 */
  transform: scaleY(0); /* 初始不可见 */
  transform-origin: top; /* 变换原点 */
  transition: transform 0.6s cubic-bezier(0.34, 1.56, 0.64, 1); /* 自定义缓动 */
}

/* 悬停时显示装饰条 */
.org-info-card:hover .card-decoration {
  transform: scaleY(1); /* 完全显示 */
}

/* 微光动画效果 */
.org-info-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%; /* 初始位置在左侧 */
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent); /* 渐变 */
  transition: none; /* 无过渡 */
}

/* 悬停时触发微光动画 */
.org-info-card:hover::after {
  animation: shine 1.2s ease; /* 微光动画 */
}

/* 微光动画关键帧 */
@keyframes shine {
  100% { left: 150%; } /* 从左侧移动到右侧 */
}

/* 工具提示进入关键帧 */
@keyframes tooltip-enter {
  from { opacity: 0; transform: translateY(-10px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

/* 工具提示离开关键帧 */
@keyframes tooltip-leave {
  from { opacity: 1; transform: translateY(0) scale(1); }
  to { opacity: 0; transform: translateY(-5px) scale(0.98); }
}

/* ========== 其他组件样式 ========== */
/* 用户面板样式 */
.user-panel {
  position: absolute;
  top: 0;
  left: calc(100% + 10px); /* 定位在用户卡片右侧 */
  width: 220px; /* 固定宽度 */
  background: white; /* 白色背景 */
  border-radius: 12px; /* 圆角 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); /* 阴影 */
  z-index: 1000; /* 高层级 */
  overflow: hidden; /* 隐藏溢出 */
  border: 1px solid #e8e0f5; /* 边框 */
}

.panel-header {
  padding: 16px; /* 内边距 */
  background: linear-gradient(135deg, #f9f5ff 0%, #f3eeff 100%); /* 渐变背景 */
}

.panel-header h3 {
  margin: 0 0 4px; /* 下边距 */
  font-size: 16px;
  color: #2d3748; /* 深灰色文字 */
}

.user-type {
  margin: 0 0 12px; /* 下边距 */
  font-size: 12px;
  color: #718096; /* 灰色文字 */
}

.upgrade-btn {
  width: 100%;
  padding: 8px; /* 内边距 */
  background: #5e4dcd; /* 紫色背景 */
  color: white; /* 白色文字 */
  border: none;
  border-radius: 6px; /* 圆角 */
  font-size: 13px;
  cursor: pointer; /* 手型指针 */
  transition: all 0.2s; /* 过渡效果 */
}

.upgrade-btn:hover {
  background: #4c3cad; /* 深紫色背景 */
}

.panel-divider {
  height: 1px; /* 高度 */
  background: #e8e0f5; /* 浅紫色背景 */
  margin: 8px 0; /* 外边距 */
}

.panel-menu {
  list-style: none; /* 无列表样式 */
  padding: 8px 0; /* 内边距 */
  margin: 0; /* 无外边距 */
}

.panel-item {
  padding: 10px 16px; /* 内边距 */
  font-size: 14px;
  color: #4a5568; /* 深灰色文字 */
  cursor: pointer; /* 手型指针 */
  transition: all 0.2s; /* 过渡效果 */
}

.panel-item:hover {
  background: #f3eeff; /* 浅紫色背景 */
  color: #5e4dcd; /* 紫色文字 */
}

/* 搜索框样式 */
.search-container {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem; /* 下边距 */
  gap: 8px; /* 子元素间距 */
}

.search-wrapper {
  flex: 1; /* 占据剩余空间 */
  display: flex;
  align-items: center;
  background: white; /* 白色背景 */
  border-radius: 6px; /* 圆角 */
  padding: 0 12px; /* 内边距 */
  border: 1px solid #e0e0e0; /* 灰色边框 */
  height: 36px; /* 固定高度 */
  transition: all 0.2s ease; /* 过渡效果 */
}

/* 获取焦点时的样式 */
.search-wrapper:focus-within {
  border-color: #8a6de8; /* 紫色边框 */
  box-shadow: 0 0 0 2px rgba(138, 109, 232, 0.2); /* 阴影 */
}

.icon-search {
  color: #a0a4ab; /* 灰色图标 */
  font-size: 16px; /* 图标大小 */
  margin-right: 8px; /* 右边距 */
}

.search-input {
  flex: 1; /* 占据剩余空间 */
  padding: 8px 0; /* 内边距 */
  border: none;
  outline: none; /* 无轮廓 */
  font-size: 14px;
  color: #2d3748; /* 深灰色文字 */
  height: 100%; /* 全高 */
  background: transparent; /* 透明背景 */
}

.search-input::placeholder {
  color: #a0a4ab; /* 灰色占位符 */
  opacity: 1; /* 完全不透明 */
}

.add-btn {
  width: 36px;
  height: 36px;
  background: #5e4dcd; /* 紫色背景 */
  border: none;
  border-radius: 6px; /* 圆角 */
  cursor: pointer; /* 手型指针 */
  transition: all 0.2s; /* 过渡效果 */
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0; /* 无内边距 */
}

.add-btn:hover {
  background: #4c3cad; /* 深紫色背景 */
}

.plus-icon {
  color: white; /* 白色图标 */
  font-size: 20px; /* 图标大小 */
  font-weight: 500; /* 中等粗细 */
  line-height: 1; /* 行高 */
}

/* 菜单样式 */
.menu {
  margin-bottom: 1.5rem; /* 下边距 */
}

/* 激活菜单项样式 */
.menu-item.active {
  background-color: #f3eeff; /* 浅紫色背景 */
  color: #5e4dcd; /* 紫色文字 */
}

.menu-item.active .hover-effect {
  width: 100%; /* 悬停效果全宽 */
}

.menu-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 10px 14px; /* 内边距 */
  border-radius: 8px; /* 圆角 */
  cursor: pointer; /* 手型指针 */
  color: #4e5969; /* 深灰色文字 */
  font-size: 14px;
  transition: all 0.2s ease; /* 过渡效果 */
  overflow: hidden; /* 隐藏溢出 */
  margin-bottom: 4px; /* 下边距 */
}

.menu-item i {
  margin-right: 10px; /* 右边距 */
  font-size: 16px; /* 图标大小 */
  color: #8a6de8; /* 紫色图标 */
}

.menu-item .label {
  display: flex;
  align-items: center;
  gap: 6px; /* 子元素间距 */
}

/* 悬停效果 */
.hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 0; /* 初始宽度为0 */
  height: 100%; /* 全高 */
  background: linear-gradient(90deg, rgba(138, 109, 232, 0.1), transparent); /* 渐变 */
  transition: width 0.3s ease; /* 宽度过渡 */
}

/* 悬停时效果 */
.menu-item:hover {
  background-color: #f3eeff; /* 浅紫色背景 */
  color: #5e4dcd; /* 紫色文字 */
}

.menu-item:hover .hover-effect {
  width: 100%; /* 全宽 */
}

/* 知识库样式 */
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #8a6de8;
  margin: 1rem 0 0.75rem;
  padding-left: 8px;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.repo-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.repo-item {
  position: relative;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #4e5969;
  display: flex;
  align-items: center;
  overflow: hidden;
  margin-bottom: 4px;
}

.repo-item i {
  margin-right: 10px;
  color: #8a6de8;
}

.repo-item:hover {
  background-color: #f3eeff;
  color: #5e4dcd;
}

.repo-item:hover .hover-effect {
  width: 100%;
}

.repo-name {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-panel-wrapper {
  position: relative;
  display: inline-block;
  margin-top: 16px;
}

.section-title-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-repo-btn {
  width: 36px;
  height: 36px;
  background: #5e4dcd;
  color: white;
  border: none;
  border-radius: 50%; /* 圆形 */
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;

  display: flex;
  align-items: center;
  justify-content: center;

  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15); /* 柔和阴影 */
  transition: all 0.2s ease;
}

.add-repo-btn:hover {
  background: #4c3cad;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  transform: scale(1.05);
}



/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  z-index: 2000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 320px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.modal h3 {
  margin-bottom: 16px;
  font-size: 18px;
  color: #333;
}

.modal-input {
  width: 90%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  margin-bottom: 16px;
  outline: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.modal-btn.cancel {
  background: #f0f0f0;
  color: #666;
}

.modal-btn.confirm {
  background: #5e4dcd;
  color: white;
}

.modal-btn.confirm:hover {
  background: #4c3cad;
}


</style>