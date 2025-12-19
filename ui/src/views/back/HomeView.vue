<!--HomeView.vue-->
<template>
  <div class="home-view">
    <!-- 页面头部 -->
    <a-card class="page-header" :bordered="false">
      <div class="header-content">
        <div class="header-title">
          <Home class="icon" />
          <h1 class="title">欢迎来到管理后台</h1>
        </div>
        <p class="subtitle">这里是您的数据控制中心，您可以在这里快速访问常用功能、查看关键数据统计及贡献记录。</p>
      </div>
    </a-card>

    <!-- 快捷功能卡片 -->
    <a-row :gutter="16" class="quick-links">
      <a-col :xs="24" :sm="12" :md="8" :lg="8">
        <a-card
            class="quick-card"
            :hoverable="true"
            @click="goTo('/back/profile')"
        >
          <div class="card-icon user-icon">
            <Users class="card-icon-svg" />
          </div>
          <h3>个人中心</h3>
          <p>管理个人信息和偏好</p>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="8">
        <a-card
            class="quick-card"
            :hoverable="true"
            @click="goTo('/back/settings')"
        >
          <div class="card-icon settings-icon">
            <Settings class="card-icon-svg" />
          </div>
          <h3>系统设置</h3>
          <p>配置系统参数与权限</p>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="8">
        <a-card
            class="quick-card"
            :hoverable="true"
            @click="goTo('/back/stats')"
        >
          <div class="card-icon stats-icon">
            <BarChart3 class="card-icon-svg" />
          </div>
          <h3>数据统计</h3>
          <p>了解平台实时数据</p>
        </a-card>
      </a-col>
    </a-row>

    <!-- 关键数据卡片 -->
    <a-row :gutter="16" class="stats-cards">
      <a-col :xs="24" :sm="12" :md="8" :lg="8">
        <AnimatedNumberCard
            :target="1284"
            label="总用户数"
            format="number"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="8">
        <AnimatedNumberCard
            :target="36800"
            label="月访问量"
            format="kilo"
        />
      </a-col>
      <a-col :xs="24" :sm="12" :md="8" :lg="8">
        <AnimatedNumberCard
            :target="99.2"
            label="系统可用性"
            format="percent"
        />
      </a-col>
    </a-row>

    <!-- 贡献图组件（核心新增部分） -->
    <ContributionGraph />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
// 导入数字动画卡片组件（原有组件）
import AnimatedNumberCard from '../../components/AnimatedNumberCard.vue';
// 导入贡献图组件（新实现的子组件）
import ContributionGraph from '../../components/ContributionGraph.vue';
import { Home, Users, Settings, BarChart3 } from 'lucide-vue-next';

// 初始化路由实例
const router = useRouter();

/**
 * 路由跳转方法
 * @param path 目标路由路径
 */
function goTo(path: string) {
  router.push(path);
}
</script>

<style scoped>
/* 页面容器样式 */
.home-view {
  padding: 24px;
  background-color: #f2f3f5;
  min-height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 头部区域样式 */
.page-header {
  margin-bottom: 24px;
}

:deep(.page-header .arco-card-body) {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.icon {
  width: 20px;
  height: 20px;
  color: #165dff;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #1d2129;
  margin: 0;
}

.subtitle {
  font-size: 13px;
  color: #86909c;
  max-width: 600px;
  margin: 0;
}

/* 快捷功能卡片容器 */
.quick-links {
  margin-bottom: 24px;
}

.quick-card {
  cursor: pointer;
  text-align: center;
  transition: all 0.2s ease;
}

:deep(.quick-card .arco-card-body) {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 卡片图标容器 */
.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  color: white;
}

.card-icon-svg {
  width: 24px;
  height: 24px;
}

/* 不同卡片的图标背景色 */
.user-icon {
  background: #165dff;
}

.settings-icon {
  background: #ff7d00;
}

.stats-icon {
  background: #00b42a;
}

/* 卡片标题和描述 */
.quick-card h3 {
  font-size: 16px;
  color: #1d2129;
  margin: 0 0 6px 0;
  font-weight: 600;
}

.quick-card p {
  font-size: 13px;
  color: #86909c;
  line-height: 1.4;
  margin: 0;
}

/* 数据统计卡片容器 */
.stats-cards {
  margin-bottom: 24px;
}
</style>