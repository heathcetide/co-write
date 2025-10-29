<!--HomeView.vue-->
<template>
  <div class="home-view">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="header-title">
          <Home class="icon" />
          <h1 class="title">欢迎来到管理后台</h1>
        </div>
        <p class="subtitle">这里是您的数据控制中心，您可以在这里快速访问常用功能、查看关键数据统计及贡献记录。</p>
      </div>
    </header>

    <!-- 快捷功能卡片 -->
    <div class="quick-links">
      <div class="card" @click="goTo('/back/profile')">
        <div class="card-icon user-icon">
          <Users class="card-icon-svg" />
        </div>
        <h3>个人中心</h3>
        <p>管理个人信息和偏好</p>
      </div>

      <div class="card" @click="goTo('/back/settings')">
        <div class="card-icon settings-icon">
          <Settings class="card-icon-svg" />
        </div>
        <h3>系统设置</h3>
        <p>配置系统参数与权限</p>
      </div>

      <div class="card" @click="goTo('/back/stats')">
        <div class="card-icon stats-icon">
          <BarChart3 class="card-icon-svg" />
        </div>
        <h3>数据统计</h3>
        <p>了解平台实时数据</p>
      </div>
    </div>

    <!-- 关键数据卡片 -->
    <div class="stats-cards">
      <AnimatedNumberCard
          :target="1284"
          label="总用户数"
          format="number"
      />
      <AnimatedNumberCard
          :target="36800"
          label="月访问量"
          format="kilo"
      />
      <AnimatedNumberCard
          :target="99.2"
          label="系统可用性"
          format="percent"
      />
    </div>

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
  background-color: #ffffff;
  min-height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 头部区域样式 */
.page-header {
  border-radius: 10px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 0 rgba(2, 6, 23, 0.04);
  background: #f8fafc;
  color: #0f172a;
  position: relative;
  overflow: hidden;
  border: 1px solid #e5e7eb;
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
  max-width: 600px;
}

/* 快捷功能卡片容器 */
.quick-links {
  display: flex;
  gap: 16px;
  flex-wrap: nowrap;
  margin-bottom: 24px;
  justify-content: center;
}

/* 功能卡片样式 */
.card {
  background-color: #ffffff;
  border-radius: 10px;
  padding: 20px;
  width: 240px;
  cursor: pointer;
  box-shadow: 0 1px 0 rgba(2, 6, 23, 0.04);
  transition: all 0.2s ease;
  border: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

/* 卡片悬停效果 */
.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #bfdbfe;
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
  background: #2563eb;
}

.settings-icon {
  background: #f59e0b;
}

.stats-icon {
  background: #10b981;
}

/* 卡片标题和描述 */
.card h3 {
  font-size: 16px;
  color: #0f172a;
  margin-bottom: 6px;
  font-weight: 600;
}

.card p {
  font-size: 13px;
  color: #64748b;
  line-height: 1.4;
}

/* 数据统计卡片容器 */
.stats-cards {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  justify-content: center;
  margin-bottom: 24px;
}

/* 响应式布局适配 */
@media (max-width: 768px) {
  .home-view {
    padding: 16px;
  }

  .page-header {
    padding: 20px;
  }

  .title {
    font-size: 20px;
  }

  /* 小屏幕下卡片全屏宽度 */
  .quick-links {
    flex-direction: column;
    align-items: stretch;
  }

  .card, .stats-cards > div {
    width: 100%;
  }

  /* 调整数据卡片最小宽度 */
  .stats-cards {
    gap: 12px;
  }
}
</style>