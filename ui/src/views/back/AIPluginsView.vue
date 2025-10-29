<template>
  <div class="plugin-management">
    <div class="header">
      <Bot class="icon" />
      <h1 class="title">AI 插件管理</h1>
    </div>
    <p class="subtitle">管理和配置 AI 插件，扩展平台功能</p>
    
    <!-- 搜索与分类 -->
    <div class="filters">
      <Input
          class="filter-input"
          v-model="searchQuery"
          placeholder="搜索插件..."
      />
      <Select
          class="filter-input"
          v-model="selectedCategory"
          :options="formattedCategoriesOptions"
          placeholder="选择分类"
      />
      <button class="btn open-store-btn" @click="openStore">
        <Store class="btn-icon" />
        打开插件商店
      </button>
    </div>

    <!-- 插件商店弹窗 -->
    <div v-if="showStore" class="store-popup">
      <div class="popup-content">
        <h2>插件商店</h2>
        <div class="plugin-list">
          <div
              v-for="plugin in plugins"
              :key="plugin.id"
              class="plugin-card"
              @click="openPluginDetails(plugin)"
          >
            <div class="plugin-card-header">
              <h3>{{ plugin.pluginName }}</h3>
              <button class="btn install-btn" @click.stop="installPlugin(plugin)">
              <Download class="btn-icon" />
              安装
            </button>
            </div>
            <p>{{ plugin.pluginType }} - {{ plugin.version }}</p>
          </div>
        </div>

        <!-- 分页 -->
        <Pagination
            :currentPage="currentStorePage"
            :totalPages="totalStorePages"
            :total="plugins.length"
            :pageSize="storePageSize"
            :pageSizes="[10, 20, 50, 100]"
            :maxVisible="7"
            @update:page="updateStorePage"
            @update:pageSize="updateStorePageSize"
        />
        <button class="btn close-btn" @click="closeStore">
          <X class="btn-icon" />
          关闭商店
        </button>
      </div>
    </div>

    <!-- 插件详情弹窗 -->
    <div v-if="showPluginDetails && currentPlugin" class="plugin-details-popup">
      <div class="popup-content">
        <h2>{{ currentPlugin.pluginName }} 详情</h2>
        <p>{{ currentPlugin.pluginType }}</p>
        <p>{{ currentPlugin.documentationUrl }}</p>
        <p v-if="currentPlugin.webhookUrl">
          Webhook URL: <a :href="currentPlugin.webhookUrl" target="_blank">{{ currentPlugin.webhookUrl }}</a>
        </p>
        <button class="btn close-btn" @click="closePluginDetails">
          <X class="btn-icon" />
          关闭
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import api from '../../api'; // 引入接口方法
import Pagination from '../../components/Pagination.vue';
import Input from '../../components/Input.vue';
import Select from '../../components/Select.vue';
import { Bot, Store, Download, X } from 'lucide-vue-next';

const categories = ['支付', '分析', 'SEO', '安全', '图像处理'];

const formattedCategoriesOptions = computed(() => [
  { value: null, label: '所有分类' },
  ...categories.map(category => ({ value: category, label: category }))
]);

interface Plugin {
  id: number;
  pluginName: string;
  pluginType: string;
  version: string;
  webhookUrl?: string;
  apiUrl: string;
  documentationUrl: string;
  enabled: boolean;
}

const plugins = ref<Plugin[]>([]); // 插件列表
const searchQuery = ref(''); // 搜索查询
const selectedCategory = ref<string | null>(null); // 选择分类
const showStore = ref(false); // 控制商店弹窗
const showPluginDetails = ref(false); // 控制插件详情弹窗
const currentPlugin = ref<Plugin | null>(null); // 当前选中的插件

// 分页控制
const currentStorePage = ref(1); // 当前商店页码
const storePageSize = ref(10); // 商店每页显示插件数量

// 请求插件数据
const loadPlugins = async () => {
  try {
    const result = await api.pluginApi.fetchPlugins({
      page: currentStorePage.value,
      size: storePageSize.value,
      keyword: searchQuery.value, // 传递搜索关键词
      category: selectedCategory.value, // 传递选择的分类
    });

    // 确保 result.data 和 result.data.records 存在
    if (result.data && result.data.records) {
      plugins.value = result.data.records; // 设置插件数据
    } else {
      console.error("返回的数据格式不正确，缺少 'records' 属性");
      plugins.value = []; // 如果没有 records，设置为空数组
    }
  } catch (error) {
    console.error('Error loading plugins:', error);
  }
};

onMounted(() => {
  loadPlugins(); // 页面加载时加载插件列表
});

// 搜索和分页控制
const updateStorePage = (page: number) => {
  currentStorePage.value = page;
  loadPlugins(); // 更新页面时重新加载插件
};

const updateStorePageSize = (size: number) => {
  storePageSize.value = size;
  currentStorePage.value = 1; // 重置到第一页
  loadPlugins(); // 更新每页数量时重新加载插件
};

// 总页数计算
const totalStorePages = computed(() => {
  return Math.ceil(plugins.value.length / storePageSize.value);
});

// 打开/关闭插件商店
const openStore = () => { showStore.value = true; };
const closeStore = () => { showStore.value = false; };

// 打开插件详情
const openPluginDetails = (plugin: Plugin) => { currentPlugin.value = plugin; showPluginDetails.value = true; };
const closePluginDetails = () => { showPluginDetails.value = false; };

// 安装插件
const installPlugin = async (plugin: Plugin) => {
  try {
    if (!window.electronAPI.pluginApi?.downloadAndRunPluginById) {
      alert('当前运行环境不支持插件安装，请在 Electron 中运行')
      return
    }

    await window.electronAPI.pluginApi.downloadAndRunPluginById(plugin.id, plugin.pluginName)
    alert(`插件 "${plugin.pluginName}" 已成功安装并启动`)
  } catch (err) {
    console.error('安装插件失败:', err)
    alert('插件安装失败: ' + err.message || err)
  }
}

</script>

<style scoped>
.plugin-management {
  padding: 24px;
  background: #ffffff;
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.icon {
  width: 20px;
  height: 20px;
  color: #2563eb;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}

.subtitle {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 24px;
}

.filters {
  display: flex;
  gap: 12px;
  justify-content: space-between;
  margin-bottom: 24px;
  align-items: center;
}

.filter-input {
  width: 48%;
  max-width: 300px;
}

.open-store-btn {
  background-color: #2563eb;
  color: #ffffff;
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
}

.open-store-btn:hover {
  background-color: #1d4ed8;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #0f172a;
}

.plugin-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.plugin-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
  transition: all 0.2s;
  box-shadow: 0 1px 0 rgba(2, 6, 23, 0.04);
}

.plugin-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #bfdbfe;
}

.plugin-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.plugin-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.plugin-card p {
  font-size: 13px;
  color: #64748b;
}

.plugin-card button {
  padding: 6px 10px;
  border-radius: 8px;
  background-color: #2563eb;
  color: #ffffff;
  border: none;
  cursor: pointer;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
}

.plugin-card button:hover {
  background-color: #1d4ed8;
}
/* 限制插件商店和插件详情弹窗的最大高度 */
.store-popup, .plugin-details-popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #ffffff;
  color: #0f172a;
  padding: 24px;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  border: 1px solid #e5e7eb;
  max-width: 800px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
}

/* 如果需要进一步限制内容区域的最大高度，也可以设置 */
.plugin-list {
  max-height: 60vh;
  overflow-y: auto;
}

/* 样式保持不变，其它部分 */
.store-popup h2, .plugin-details-popup h2 {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 16px;
}

.close-btn {
  background-color: #ef4444;
  color: #ffffff;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: #dc2626;
}

</style>
