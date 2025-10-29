import {createRouter, createWebHistory} from 'vue-router';
import Layout from '../layout/Layout.vue';
import Login from '../views/Login.vue';
import Documents from '../views/Documents.vue';
import DocumentEditor from '../views/DocumnetEdit.vue'
import BackLayout from "../layout/BackLayout.vue";
import StatsView from "../views/back/StatsView.vue"
import AIPluginsView from "../views/back/AIPluginsView.vue"
import ProfileView from "../views/back/ProfileView.vue"
import NotificationView from "../views/back/NotificationView.vue"
import PersonalLogsView from "../views/back/PersonalLogsView.vue"
import AccountManagementView from "../views/back/AccountManagementView.vue"
import SettingsView from "../views/back/SettingsView.vue"
import HomeView from "../views/back/HomeView.vue"
import RefreshPage from "../components/RefreshPage.vue"
import { useAuth } from '../composables/useAuth'
import Drawio from "../views/Drawio.vue";
import AIChat from "../views/AIChat.vue";
import OrganizationManagement from "../views/back/OrganizationManagement.vue";
import NotFound from "../views/NotFound.vue";
// import OrganizationManagement from "../views/back/OrganizationManagement.vue";



const routes = [
  // router/index.ts 或你定义路由的文件中
  {
    path: '/refresh',
    name: 'RefreshPage',
    component: RefreshPage
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, transition: 'fade' }
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '/',
        name: 'Home',
        component: Documents,
        meta: { requiresAuth: true, transition: 'fade' }
      },
      {
        path: '/documents',
        name: 'Documents',
        component: DocumentEditor,
        meta: { requiresAuth: true, transition: 'fade' }
      },
      {
        path: '/ai/documents',
        name: 'AIPlugins',
        component: AIChat,
        meta: { requiresAuth: true, transition: 'fade' }
      },
      {
        path: '/diagrams',
        name: 'Diagrams',
        component: Drawio,
        meta: { requiresAuth: false, transition: 'fade' }
      }
    ],
  },
  {
    path: '/back',
    component: BackLayout,
    children: [
      {
        path: '',
        component: HomeView,
        transition: 'fade'
      },
      {
        path: 'stats',
        component: StatsView,
        transition: 'fade'
      },
      {
        path: 'ai-plugins',
        component: AIPluginsView
      },
      {
        path: 'profile',
        component: ProfileView
      },
      {
        path: 'notifications',
        component: NotificationView
      },
      {
        path: 'logs',
        component: PersonalLogsView
      },
      {
        path: 'account',
        component: AccountManagementView
      },
      {
        path: 'settings',
        component: SettingsView,
        transition: 'fade'
      },
    ],
    meta: { requiresAuth: false }
  },
  {
    path: '/organization/management',
    component: OrganizationManagement,
    meta: { requiresAuth: true }, // 如果需要认证
    children: [

    ]
  }
  ,
  // 404 兜底路由（必须放最后）
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { requiresAuth: false }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全局导航守卫
router.beforeEach((to, _, next) => {
  const { getToken } = useAuth()

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 如果没有 token，则跳转到登录页
    if (!getToken.value) {
      next('/login')
    } else {
      next() // 继续路由导航
    }
  } else {
    next() // 不需要认证，继续路由导航
  }
})

export default router;