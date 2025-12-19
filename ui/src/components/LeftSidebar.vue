<template>
  <aside class="left-sidebar" :class="{ collapsed }">
    <!-- 侧边栏折叠按钮 -->
    <div class="collapse-trigger" @click="toggleSidebar">
      <ChevronRight v-if="collapsed" class="collapse-icon" />
      <ChevronLeft v-else class="collapse-icon" />
    </div>

    <!-- 用户信息卡片 -->
    <a-popover
        v-model:popup-visible="showUserPanel"
        trigger="hover"
        position="right"
        :content-style="{ padding: 0, borderRadius: '12px' }"
        v-if="!collapsed"
    >
      <template #content>
        <div class="user-panel">
          <div class="panel-header">
            <a-avatar :size="48" :style="avatarStyle" class="panel-avatar">
              {{ avatarText }}
            </a-avatar>
            <a-typography-title :heading="6" class="panel-username">{{ userInfo?.username }}</a-typography-title>
            <a-typography-text type="secondary" class="user-type">普通用户，购买可享更多权益</a-typography-text>
            <a-button type="primary" long class="upgrade-btn" @click="goToCreationCenter">
              <template #icon>
                <icon-gift />
              </template>
              升级会员
            </a-button>
          </div>
          <a-divider margin="0" />
          <a-menu :style="{ border: 'none', boxShadow: 'none', background: 'transparent' }" class="panel-menu">
            <a-menu-item @click="goToCreationCenter">
              <template #icon>
                <icon-apps />
              </template>
              创作中心
            </a-menu-item>
            <a-menu-item @click="goToSettings">
              <template #icon>
                <icon-settings />
              </template>
              设置
            </a-menu-item>
            <a-menu-item @click="logout">
              <template #icon>
                <icon-poweroff />
              </template>
              退出登录
            </a-menu-item>
          </a-menu>
        </div>
      </template>
      <a-card class="user-org-card" :bordered="false" :hoverable="true" :body-style="{ padding: '12px' }">
        <a-space :size="12" align="center">
          <a-avatar :size="42" :style="avatarStyle" class="user-avatar">
            {{ avatarText }}
          </a-avatar>
          <div class="user-meta">
            <a-typography-text class="nickname" strong>{{ userInfo?.username }}</a-typography-text>
            <a-typography-text type="secondary" class="user-role">普通用户</a-typography-text>
          </div>
        </a-space>
      </a-card>
    </a-popover>
    
    <!-- 折叠状态下的用户头像 -->
    <a-popover
        v-if="collapsed"
        v-model:popup-visible="showUserPanel"
        trigger="hover"
        position="right"
        :content-style="{ padding: 0, borderRadius: '12px' }"
    >
      <template #content>
        <div class="user-panel">
          <div class="panel-header">
            <a-avatar :size="48" :style="avatarStyle" class="panel-avatar">
              {{ avatarText }}
            </a-avatar>
            <a-typography-title :heading="6" class="panel-username">{{ userInfo?.username }}</a-typography-title>
            <a-typography-text type="secondary" class="user-type">普通用户，购买可享更多权益</a-typography-text>
            <a-button type="primary" long class="upgrade-btn" @click="goToCreationCenter">
              <template #icon>
                <icon-gift />
              </template>
              升级会员
            </a-button>
          </div>
          <a-divider margin="0" />
          <a-menu :style="{ border: 'none', boxShadow: 'none', background: 'transparent' }" class="panel-menu">
            <a-menu-item @click="goToCreationCenter">
              <template #icon>
                <icon-apps />
              </template>
              创作中心
            </a-menu-item>
            <a-menu-item @click="goToSettings">
              <template #icon>
                <icon-settings />
              </template>
              设置
            </a-menu-item>
            <a-menu-item @click="logout">
              <template #icon>
                <icon-poweroff />
              </template>
              退出登录
            </a-menu-item>
          </a-menu>
        </div>
      </template>
      <div class="collapsed-user-avatar">
        <a-avatar :size="36" :style="avatarStyle">
          {{ avatarText }}
        </a-avatar>
      </div>
    </a-popover>

    <!-- 组织信息栏 -->
    <a-popover
        v-model:popup-visible="showOrgTooltip"
        trigger="hover"
        position="right"
        :content-style="{ padding: 0, width: '300px', borderRadius: '12px' }"
        v-if="!collapsed"
        @popup-visible-change="(visible) => { if (visible) getOrganizationListQuickly() }"
    >
      <template #content>
        <div class="org-tooltip-content">
          <!-- 组织管理中心入口 -->
          <div class="tooltip-header-wrapper">
            <a-button type="primary" long class="tooltip-header" @click="goToOrgManagement">
              <template #icon>
                <icon-settings />
              </template>
              进入组织管理中心
            </a-button>
          </div>

          <!-- 可滚动组织列表 -->
          <div class="org-list-container" @wheel.prevent="handleScroll">
            <a-list :bordered="false" :split="false">
              <a-list-item
                  v-for="org in visibleOrgs"
                  :key="org.id"
                  :class="{ 'org-item-active': org.id === currentOrgId }"
                  @click="switchOrg(org)"
                  class="org-item"
              >
                <template #actions>
                  <a-tag size="small" color="blue">{{ org.currentMembers }}人</a-tag>
                </template>
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar :size="32" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                      <icon-home />
                    </a-avatar>
                  </template>
                  <template #title>
                    <a-typography-text strong class="org-item-name">{{ org.name }}</a-typography-text>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </a-list>
            <div class="scroll-indicator" v-if="showScrollIndicator">
              <icon-down />
            </div>
          </div>
        </div>
      </template>
      <a-card class="org-info-card" :bordered="false" :hoverable="true" :body-style="{ padding: '12px' }">
        <a-space :size="8" align="center">
          <a-avatar :size="28" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <icon-home />
          </a-avatar>
          <div class="org-info-content" v-if="!collapsed">
            <a-typography-text type="secondary" class="org-prefix">当前组织</a-typography-text>
            <a-typography-text strong class="org-name">{{ currentOrg?.name || '未选择' }}</a-typography-text>
          </div>
          <icon-right class="org-arrow" />
        </a-space>
      </a-card>
    </a-popover>

    <!-- 搜索框 -->
    <div v-if="!collapsed" class="search-container">
      <a-input
          placeholder="搜索 Ctrl+J"
          allow-clear
          size="large"
          class="search-input"
      >
        <template #prefix>
          <icon-search />
        </template>
      </a-input>
      <a-button type="primary" shape="circle" size="large" class="add-btn">
        <template #icon>
          <icon-plus />
        </template>
      </a-button>
    </div>

    <!-- 功能菜单项 -->
    <a-menu
        v-if="!collapsed"
        :selected-keys="[selectedMenuItem]"
        class="menu"
        @menu-item-click="(key) => handleMenuClick(menuItems.find(item => item.id === key) || startCreateItem)"
    >
      <a-menu-item :key="startCreateItem.id">
        <template #icon>
          <Edit class="inline-icon" />
        </template>
        {{ startCreateItem.label }}
      </a-menu-item>
      <a-menu-item
          v-for="item in menuItems"
          :key="item.id"
      >
        <template #icon>
          <icon-robot v-if="item.id === 'ai/documents'" />
          <icon-apps v-else />
        </template>
        {{ item.label }}
      </a-menu-item>
    </a-menu>

    <!-- 知识库区域 -->
    <div class="repos" v-if="!collapsed">
      <div class="section-title-wrapper">
        <h3 class="section-title"><BookOpen class="inline-icon" /> 我的知识库</h3>
        <a-button type="primary" shape="circle" size="small" @click="isCreateModalVisible = true">
          <template #icon>
            <icon-plus />
          </template>
        </a-button>
      </div>
      <a-list :bordered="false" class="repo-list">
        <a-list-item
            v-for="repo in repositories"
            :key="repo.id"
            class="repo-item"
            @click="selectRepository(repo)"
        >
          <template #actions>
            <a-dropdown trigger="click" @select="(key) => handleRepoAction(key, repo)">
              <a-button type="text" size="small" @click.stop>
                <template #icon>
                  <icon-more />
                </template>
              </a-button>
              <template #content>
                <a-doption key="edit" value="edit">
                  <template #icon>
                    <icon-edit />
                  </template>
                  重命名
                </a-doption>
                <a-doption key="delete" value="delete" class="danger-option">
                  <template #icon>
                    <icon-delete />
                  </template>
                  删除
                </a-doption>
              </template>
            </a-dropdown>
          </template>
          <a-list-item-meta>
            <template #avatar>
              <Folder class="inline-icon" />
            </template>
            <template #title>
              <span class="repo-name">{{ repo.name }}</span>
            </template>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
    </div>

    <!-- 创建知识库弹窗 -->
    <a-modal
        v-model:visible="isCreateModalVisible"
        title="新建知识库"
        @ok="handleCreateRepository"
        @cancel="isCreateModalVisible = false"
    >
      <a-input
          v-model="newRepoName"
          placeholder="请输入知识库名称"
          :max-length="50"
          show-word-limit
      />
    </a-modal>

    <!-- 编辑知识库弹窗 -->
    <a-modal
        v-model:visible="isEditModalVisible"
        title="重命名知识库"
        @ok="handleUpdateRepository"
        @cancel="isEditModalVisible = false"
    >
      <a-input
          v-model="editRepoName"
          placeholder="请输入知识库名称"
          :max-length="50"
          show-word-limit
      />
    </a-modal>

    <!-- 删除确认弹窗 -->
    <a-modal
        v-model:visible="isDeleteModalVisible"
        title="删除知识库"
        @ok="handleDeleteRepository"
        @cancel="isDeleteModalVisible = false"
    >
      <p>确定要删除知识库 "{{ currentEditRepo?.name }}" 吗？此操作不可恢复。</p>
    </a-modal>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import api from '../api/index'
import { User, BookOpen, Folder, Edit, ChevronRight, ChevronLeft } from 'lucide-vue-next'
import {
  IconSettings,
  IconHome,
  IconRight,
  IconDown,
  IconSearch,
  IconPlus,
  IconRobot,
  IconApps,
  IconMore,
  IconGift,
  IconPoweroff,
  IconEdit,
  IconDelete
} from '@arco-design/web-vue/es/icon'
import { Message, Modal } from '@arco-design/web-vue'

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
const isEditModalVisible = ref(false) // 知识库编辑弹窗显示状态
const isDeleteModalVisible = ref(false) // 知识库删除确认弹窗显示状态
const newRepoName = ref('') // 新知识库名称
const editRepoName = ref('') // 编辑知识库名称
const currentEditRepo = ref<Repo | null>(null) // 当前编辑的知识库
const repositories = ref<Repo[]>([]) // 知识库列表
const showOrgTooltip = ref(false) // 组织工具提示显示状态
const currentOrgId = ref(0) // 当前组织ID
const scrollPosition = ref(0) // 组织列表滚动位置

// ========== 数据定义 ==========
// 组织列表数据
const orgList = ref<Org[]>([])

// 菜单项数据
const menuItems = ref<MenuItem[]>([
  { id: 'ai/documents', label: 'AI 写作', icon: 'icon-robot' },
  { id: 'diagrams', label: '绘图', icon: 'icon-diagram' }
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
  if (!newRepoName.value.trim()) {
    Message.warning('请输入知识库名称')
    return
  }

  try {
    await api.knowledgeBaseApi.createPersonalKnowledgeBase({ name: newRepoName.value })
    newRepoName.value = ''
    isCreateModalVisible.value = false
    await loadRepositories()
    Message.success('知识库创建成功')
  } catch (err: any) {
    console.error('创建知识库失败', err)
    Message.error(err.message || '创建知识库失败')
  }
}

/**
 * 处理知识库操作（编辑/删除）
 */
const handleRepoAction = (action: string, repo: Repo) => {
  if (action === 'edit') {
    currentEditRepo.value = repo
    editRepoName.value = repo.name
    isEditModalVisible.value = true
  } else if (action === 'delete') {
    currentEditRepo.value = repo
    isDeleteModalVisible.value = true
  }
}

/**
 * 更新知识库
 */
const handleUpdateRepository = async () => {
  if (!currentEditRepo.value || !editRepoName.value.trim()) {
    Message.warning('请输入知识库名称')
    return
  }

  try {
    const response = await api.knowledgeBaseApi.updateKnowledgeBase({
      id: currentEditRepo.value.id,
      name: editRepoName.value
    })
    
    // 检查响应是否成功
    if (response.code === 200 || response.code === undefined) {
      isEditModalVisible.value = false
      currentEditRepo.value = null
      editRepoName.value = ''
      await loadRepositories()
      Message.success('知识库更新成功')
    } else {
      // 显示后端返回的错误消息
      Message.error(response.message || '更新知识库失败')
    }
  } catch (err: any) {
    console.error('更新知识库失败', err)
    // 处理错误响应
    const errorMessage = err.response?.data?.message || err.message || err.data?.message || '更新知识库失败'
    Message.error(errorMessage)
  }
}

/**
 * 删除知识库
 */
const handleDeleteRepository = async () => {
  if (!currentEditRepo.value) return

  try {
    const response = await api.knowledgeBaseApi.deleteKnowledgeBase(currentEditRepo.value.id)
    
    // 检查响应是否成功
    if (response.code === 200 || response.code === undefined) {
      isDeleteModalVisible.value = false
      const deletedRepo = currentEditRepo.value
      currentEditRepo.value = null
      await loadRepositories()
      Message.success('知识库删除成功')
      // 如果删除的是当前选中的知识库，清空选中状态
      // 这里可以根据需要添加逻辑
    } else {
      // 显示后端返回的错误消息
      Message.error(response.message || '删除知识库失败')
    }
  } catch (err: any) {
    console.error('删除知识库失败', err)
    // 处理错误响应
    const errorMessage = err.response?.data?.message || err.message || err.data?.message || '删除知识库失败'
    Message.error(errorMessage)
  }
}

/**
 *  快速查询当前用户加入的全部组织
 */
const getOrganizationListQuickly = async () => {
  try {
    const res = await api.organizationApi.getOrganizationListQuickly();
    orgList.value = res.data || []
    // 默认选择第一个组织
    if (!currentOrgId.value && orgList.value.length > 0) {
      currentOrgId.value = orgList.value[0].id
      await loadRepositories()
    }
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

// 组织 tooltip hover 逻辑已由 a-popover 处理

// 计算头像样式和文本
const avatarText = computed(() => {
  const username = userInfo.value?.username || 'U'
  return username.charAt(0).toUpperCase()
})

const avatarStyle = computed(() => {
  const username = userInfo.value?.username || 'User'
  // 基于用户名生成颜色
  let hash = 0
  for (let i = 0; i < username.length; i++) {
    hash = username.charCodeAt(i) + ((hash << 5) - hash)
  }
  const hue = hash % 360
  const saturation = 70
  const lightness = 50
  return {
    backgroundColor: `hsl(${hue}, ${saturation}%, ${lightness}%)`,
    color: '#ffffff'
  }
})

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
  background-color: #ffffff; /* 中性背景 */
  height: 100vh; /* 全屏高度 */
  padding: 1.25rem 0.875rem 0; /* 更紧凑的内边距 */
  transition: all 0.3s ease; /* 平滑过渡效果 */
  overflow-y: auto; /* 允许垂直滚动 */
  overflow-x: hidden; /* 隐藏水平滚动 */
  box-shadow: 0 1px 0 rgba(2, 6, 23, 0.04); /* 细分隔阴影 */
  display: flex;
  flex-direction: column;
}

/* 自定义滚动条样式 */
.left-sidebar::-webkit-scrollbar {
  width: 6px;
}

.left-sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.left-sidebar::-webkit-scrollbar-thumb {
  background: #c9cdd4;
  border-radius: 3px;
}

.left-sidebar::-webkit-scrollbar-thumb:hover {
  background: #86909c;
}

/* 折叠状态下的样式 */
.left-sidebar.collapsed {
  width: 60px;
  padding: 1.5rem 0.5rem 0;
  overflow: visible;
  align-items: center;
}

/* 折叠状态下的用户头像 */
.collapsed-user-avatar {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.collapsed-user-avatar:hover {
  background-color: #f7f8fa;
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
  box-shadow: 0 2px 6px rgba(2, 6, 23, 0.06); /* 轻阴影 */
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 1001; /* 确保在最上层 */
  transition: all 0.2s ease; /* 过渡效果 */
  border: 1px solid #e5e7eb; /* 边框 */
}

/* 悬停效果 */
.collapse-trigger:hover {
  background: #f8fafc; /* 浅灰背景 */
  transform: scale(1.05); /* 轻微放大 */
}

.collapse-icon {
  width: 16px;
  height: 16px;
  color: #64748b;
}


/* ========== 用户卡片样式 ========== */

.user-org-card {
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 10;
  position: relative;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e5e7eb;
}

/* 悬停效果 */
.user-org-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.15);
  border-color: #165dff;
}

.user-org {
  display: flex;
  align-items: center;
}

.user-avatar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 2px solid #ffffff;
}

.user-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* 用户昵称样式 */
.nickname {
  font-size: 14px;
  line-height: 1.4;
}

.user-role {
  font-size: 12px;
  line-height: 1.2;
}

/* 用户面板样式 */
.user-panel {
  width: 260px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.panel-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  text-align: center;
}

.panel-avatar {
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.panel-username {
  margin: 0 0 8px 0 !important;
}

.user-type {
  margin-bottom: 16px;
  display: block;
}

.upgrade-btn {
  margin-top: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.panel-menu {
  padding: 8px 0;
}

:deep(.panel-menu .arco-menu-item) {
  padding: 8px 16px;
  margin: 0 8px;
  border-radius: 8px;
  height: auto;
  line-height: 1.5;
}

:deep(.panel-menu .arco-menu-item:hover) {
  background: #f2f3f5;
}

/* ========== 组织信息栏样式 ========== */
.org-info-wrapper {
  position: relative;
  margin: 0 0 1rem;
  z-index: 100;
}

.org-info-card {
  position: relative;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e5e7eb;
}

/* 悬停效果 */
.org-info-card:hover {
  transform: translateY(-1px);
  border-color: #165dff;
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.15);
}

.org-info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

/* 组织前缀文本样式 */
.org-prefix {
  font-size: 11px;
  line-height: 1.2;
  white-space: nowrap;
}

/* 组织名称样式 */
.org-name {
  font-size: 13px;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.org-arrow {
  color: #86909c;
  transition: transform 0.2s ease;
}

.org-info-card:hover .org-arrow {
  transform: translateX(2px);
  color: #165dff;
}

/* ========== 组织工具提示样式 ========== */
.org-tooltip-content {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.tooltip-header-wrapper {
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.tooltip-header {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
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
  background: #2563eb; /* 纯色强调背景 */
  color: white; /* 白色文字 */
  cursor: pointer; /* 手型指针 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); /* 自定义缓动 */
  position: relative; /* 相对定位 */
  overflow: hidden; /* 隐藏溢出内容 */
}

/* 悬停效果 */
.tooltip-header:hover {
  background: #1d4ed8; /* 深一号蓝 */
  transform: translateY(-1px); /* 上浮效果 */
  box-shadow: 0 2px 8px rgba(2, 6, 23, 0.12); /* 阴影 */
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
  max-height: 200px;
  overflow-y: auto;
  position: relative;
}

:deep(.org-list-container .arco-list-item) {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f2f3f5;
}

:deep(.org-list-container .arco-list-item:last-child) {
  border-bottom: none;
}

/* 悬停效果 */
:deep(.org-list-container .arco-list-item:hover) {
  background-color: #f7f8fa;
}

/* 当前选中组织样式 */
.org-item-active {
  background-color: #e8f3ff !important;
}

.org-item-active .org-item-name {
  color: #165dff !important;
}

.org-item i {
  color: #2563eb; /* 蓝色图标 */
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
  color: #2563eb; /* 蓝色 */
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
  margin-bottom: 1.5rem;
  gap: 8px;
}

.search-input {
  flex: 1;
}

:deep(.search-input .arco-input-wrapper) {
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
}

:deep(.search-input .arco-input-wrapper:hover) {
  border-color: #165dff;
}

:deep(.search-input .arco-input-wrapper.arco-input-focus) {
  border-color: #165dff;
  box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
}

.add-btn {
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.2);
  transition: all 0.2s ease;
}

.add-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.3);
}

/* 菜单样式 */
.menu {
  margin-bottom: 1.5rem;
  background: transparent;
  border: none;
}

:deep(.menu .arco-menu-item) {
  margin-bottom: 2px;
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.2s ease;
  height: auto;
  line-height: 1.5;
}

:deep(.menu .arco-menu-item:hover) {
  background-color: #f7f8fa;
}

:deep(.menu .arco-menu-item-selected) {
  background-color: #e8f3ff;
  color: #165dff;
  font-weight: 500;
}

:deep(.menu .arco-menu-item-selected .arco-icon) {
  color: #165dff;
}

:deep(.menu .arco-menu-item .arco-icon) {
  font-size: 16px;
  margin-right: 8px;
}

/* 知识库样式 */
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #165dff;
  margin: 1rem 0 0.75rem;
  padding-left: 8px;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-title-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.repo-list {
  background: transparent;
  border: none;
}

:deep(.repo-list .arco-list-item) {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

:deep(.repo-list .arco-list-item:hover) {
  background-color: #f7f8fa;
  border-color: #e5e7eb;
  transform: translateX(2px);
}

.repo-name {
  font-size: 14px;
  color: #1d2129;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.repo-list .arco-list-item:hover .repo-name) {
  color: #165dff;
}

:deep(.danger-option) {
  color: #f53f3f;
}

:deep(.danger-option:hover) {
  background-color: #fff2f0;
  color: #f53f3f;
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
  background: #2563eb;
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
  background: #1d4ed8;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  transform: scale(1.05);
}

/* 统一内嵌 lucide 图标的尺寸 */
.inline-icon { width: 16px; height: 16px; vertical-align: -2px; }



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