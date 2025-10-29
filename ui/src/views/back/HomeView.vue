<!--HomeView.vue-->
<template>
  <div class="home-view">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <h1 class="title">欢迎来到管理后台</h1>
        <p class="subtitle">这里是您的数据控制中心，您可以在这里快速访问常用功能、查看关键数据统计及贡献记录。</p>
      </div>
    </header>

    <!-- 快捷功能卡片 -->
    <div class="quick-links">
      <div class="card" @click="goTo('/back/users')">
        <div class="card-icon user-icon">
          <i class="fas fa-users"></i>
        </div>
        <h3>用户管理</h3>
        <p>查看和管理平台用户</p>
      </div>

      <div class="card" @click="goTo('/back/settings')">
        <div class="card-icon settings-icon">
          <i class="fas fa-cog"></i>
        </div>
        <h3>系统设置</h3>
        <p>配置系统参数与权限</p>
      </div>

      <div class="card" @click="goTo('/back/stats')">
        <div class="card-icon stats-icon">
          <i class="fas fa-chart-line"></i>
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
  padding: 32px;
  background-color: #fafafa; /* 浅灰背景提升层次感 */
  min-height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 头部区域样式 */
.page-header {
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 32px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  color: #1e293b;
  position: relative;
  overflow: hidden;
}

/* 头部装饰元素 */
.page-header::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 200px;
  background: rgba(96, 165, 250, 0.1); /* 淡蓝色装饰圆 */
  border-radius: 50%;
  transform: translate(50%, -50%);
  z-index: 0;
}

.header-content {
  position: relative; /* 确保内容在装饰元素上方 */
  z-index: 1;
}

.title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #0f172a;
}

.subtitle {
  font-size: 16px;
  opacity: 0.9;
  max-width: 600px;
  color: #475569;
}

/* 快捷功能卡片容器 */
.quick-links {
  display: flex;
  gap: 24px;
  flex-wrap: nowrap; /* 支持响应式换行 */
  margin-bottom: 32px;
  justify-content: center; /* 卡片居中排列 */
}

/* 功能卡片样式 */
.card {
  background-color: white;
  border-radius: 12px;
  padding: 28px;
  width: 280px;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

/* 卡片悬停效果 */
.card:hover {
  transform: translateY(-6px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
  border-color: #e2e8f0;
}

/* 卡片图标容器 */
.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: white;
  font-size: 24px;
}

/* 不同卡片的图标背景色 */
.user-icon {
  background: linear-gradient(135deg, #3b82f6, #60a5fa); /* 蓝色系 */
}

.settings-icon {
  background: linear-gradient(135deg, #f59e0b, #fbbf24); /* 黄色系 */
}

.stats-icon {
  background: linear-gradient(135deg, #10b981, #34d399); /* 绿色系 */
}

/* 卡片标题和描述 */
.card h3 {
  font-size: 18px;
  color: #0f172a;
  margin-bottom: 8px;
  font-weight: 600;
}

.card p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}

/* 数据统计卡片容器 */
.stats-cards {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  justify-content: center;
  margin-bottom: 32px; /* 与下方贡献图保持间距 */
}

/* 响应式布局适配 */
@media (max-width: 768px) {
  .home-view {
    padding: 24px 16px;
  }

  .page-header {
    padding: 32px 24px;
  }

  .title {
    font-size: 28px;
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
    gap: 16px;
  }
}
</style>