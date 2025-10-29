<template>
  <div class="plugin-runner">
    <!-- 折叠按钮 -->
    <div class="toggle-button" @click="isOpen = !isOpen">
      插件运行中 ({{ runningPlugins.length }})
      <button class="refresh-btn" @click="refreshRunningPlugins">刷新</button>
    </div>

    <!-- 插件列表面板 -->
    <div v-if="isOpen" class="panel">
      <div
          v-for="plugin in runningPlugins"
          :key="plugin.id"
          class="plugin-item"
      >
        <div class="plugin-info">
          <strong>{{ plugin.pluginName }}</strong>
          <span class="status">运行中</span>
        </div>
        <button class="stop-btn" @click="stopPlugin(plugin)">停止</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface RunningPlugin {
  id: number
  pluginName: string
}

const isOpen = ref(false)
const runningPlugins = ref<RunningPlugin[]>([])

// 示例：从内存中加载运行中的插件（你可能需要和后端或全局状态通信）
const refreshRunningPlugins = async () => {
  try {
    const result = await window.electronAPI.pluginApi?.listRunning()
    runningPlugins.value = result || []
  } catch (err) {
    console.error('获取运行中的插件失败:', err)
    runningPlugins.value = []
  }
}


onMounted(() => {
  refreshRunningPlugins()
})

// 手动终止插件运行
const stopPlugin = async (plugin: RunningPlugin) => {
  try {
    await window.electronAPI.pluginApi?.stopPluginById(plugin.id)
    runningPlugins.value = runningPlugins.value.filter(p => p.id !== plugin.id)
  } catch (err : any) {
    alert(`停止插件失败: ${err.message || err}`)
    console.log(err)
  }
}
</script>

<style scoped>
.plugin-runner {
  position: fixed;
  bottom: 0;
  right: 10px;
  z-index: 999999;
  font-family: sans-serif;
}

/* 折叠按钮样式：极简紧凑型 */
.toggle-button {
  background-color: #06b6d4;
  color: white;
  padding: 4px 10px;
  font-size: 11px;
  height: 22px;
  min-width: 120px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}

/* 刷新按钮微型 */
.refresh-btn {
  background-color: #facc15;
  color: black;
  padding: 2px 6px;
  font-size: 11px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 8px;
}
.refresh-btn:hover {
  background-color: #eab308;
}

.panel {
  background-color: white;
  border: 1px solid #ccc;
  padding: 12px;
  border-radius: 8px;
  margin-top: 6px;
  width: 280px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.plugin-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.plugin-info {
  display: flex;
  flex-direction: column;
}

.status {
  color: green;
  font-size: 11px;
}

.stop-btn {
  background-color: #ff5c5c;
  color: white;
  padding: 4px 8px;
  font-size: 11px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.stop-btn:hover {
  background-color: #ff2b2b;
}
</style>