<template>
  <div class="not-found">
    <div class="content">
      <div class="icon-wrap">
        <SearchX class="icon" />
      </div>
      <h1 class="title">页面未找到</h1>
      <p class="desc">抱歉，您访问的页面不存在或已被移动。</p>
      <div class="actions">
        <button class="btn primary" @click="goHome"><Home class="btn-icon" /> 返回首页</button>
        <button class="btn secondary" @click="goConsole"><LayoutDashboard class="btn-icon" /> 返回控制台</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { SearchX, Home, LayoutDashboard } from 'lucide-vue-next'

const router = useRouter()
function goHome() { router.push('/') }
function goConsole() { router.push('/back') }
</script>

<style scoped>
.not-found { display: flex; align-items: center; justify-content: center; min-height: 100vh; background: #ffffff; padding: 24px; }
.content { text-align: center; max-width: 480px; }
.icon-wrap { width: 64px; height: 64px; border-radius: 9999px; background: #eff6ff; display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; }
.icon { width: 28px; height: 28px; color: #2563eb; }
.title { font-size: 22px; font-weight: 700; color: #0f172a; margin: 8px 0; }
.desc { font-size: 13px; color: #64748b; margin-bottom: 16px; }
.actions { display: flex; gap: 10px; justify-content: center; }
.btn { padding: 8px 12px; border-radius: 8px; border: 1px solid #e5e7eb; cursor: pointer; font-size: 13px; display: inline-flex; align-items: center; gap: 6px; }
.btn-icon { width: 16px; height: 16px; }
.btn.primary { background: #2563eb; color: #ffffff; border-color: transparent; }
.btn.primary:hover { background: #1d4ed8; }
.btn.secondary { background: #f8fafc; color: #0f172a; }
.btn.secondary:hover { background: #eff6ff; color: #2563eb; border-color: #bfdbfe; }
</style>

