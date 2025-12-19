<template>
  <div class="layout">
    <div class="container">
      <!-- 左侧 -->
      <LeftSidebar
          title="知识库"
          :items="repositories"
          :collapsed="isLeftCollapsed"
          @toggle="isLeftCollapsed = !isLeftCollapsed"
          @select="selectRepository"
          @menuClick="onMenuClick"
          @repoClick="onRepoSelect"
      />

      <!-- 主内容区域 -->
      <main class="content">
        <RouterView />
      </main>
      <!-- 右侧 -->
      <RightSidebar
          v-if="!isHideRightSidebar && selectedRepo"
          :collapsed="isRightCollapsed"
          :currentRepo="selectedRepo"
          :catalog="docCatalog"
          @toggle="isRightCollapsed = !isRightCollapsed"
          @selectItem="handleSidebarItemClick"
          @switchRepo="openRepoSelector"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
import api from '../api/index'
import RightSidebar from '../components/RightSidebar.vue';
import LeftSidebar from "../components/LeftSidebar.vue";
const isRightCollapsed = ref(false)
const isHideRightSidebar = ref(false);
const selectedRepo = ref<Repository | null>(null)
const docCatalog = ref([])
function onMenuClick(item: any) {
  isHideRightSidebar.value = true;
  router.push(`/${item.id}`)
}

async function onRepoSelect(repo: any) {
  isHideRightSidebar.value = false;
  selectedRepo.value = repo
  console.log(selectedRepo.value)
  console.log(isHideRightSidebar.value)
  try {
    const results = await api.documentApi.getDocumentsByKnowledgeBase(repo.id)
    // 转换数据格式：将 id 和 parentId 从数字转换为字符串
    if (results && results.data) {
      docCatalog.value = results.data.map((doc: any) => ({
        ...doc,
        id: String(doc.id || doc.documentId || ''),
        parentId: doc.parentId ? String(doc.parentId) : null,
        title: doc.title || '未命名文档',
        level: doc.level || 0
      }))
    } else {
      docCatalog.value = []
    }
  } catch (error) {
    console.error('获取文档列表失败:', error)
    docCatalog.value = []
  }
}

interface Repository {
  id: number;
  name: string;
  icon?: string;
}

const isLeftCollapsed = ref(false);

const repositories = ref<Repository[]>([]);

function selectRepository(repo: Repository) {
  console.log('选择知识库:', repo);
  // 可以在这里加入路由跳转或状态更新逻辑
}

function handleSidebarItemClick(item: any) {
  console.log('右侧栏点击了目录项或首页：', item);

  if (item.id) {
    const targetRoute = {
      path: '/documents',
      query: { id: item.id, title: item.title }
    };
    console.log(targetRoute);
    if (
        router.currentRoute.value.query.id !== targetRoute.query.id
    ) {
      // 如果路径和参数都相同，手动刷新当前页面
      router.replace({ path: '/refresh' }).then(() => {
        router.replace(targetRoute);
      });
    } else {
      router.push(targetRoute);
    }
  }
}

function openRepoSelector() {
  console.log('点击了当前知识库名称，准备弹出选择框');
  // 在这里触发弹窗、下拉菜单或跳转组织选择页面
}
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  margin: 0;
  height: 100vh;
  overflow: hidden;
}

.container {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

nav ul {
  list-style: none;
  padding: 0;
}

nav ul li {
  margin: 1rem 0;
  cursor: pointer;
}

nav ul li:hover {
  color: #42b983;
}

.content {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
  height: 100%;
}

/* 自定义滚动条样式 */
.content::-webkit-scrollbar {
  width: 8px;
}

.content::-webkit-scrollbar-track {
  background: #f2f3f5;
  border-radius: 4px;
}

.content::-webkit-scrollbar-thumb {
  background: #c9cdd4;
  border-radius: 4px;
}

.content::-webkit-scrollbar-thumb:hover {
  background: #86909c;
}
</style> 