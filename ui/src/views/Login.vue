<template>
  <div class="login-wrapper">
    <canvas ref="bgCanvas" class="bg-canvas"></canvas>
    <transition name="fade">
      <a-card class="login-card" :bordered="false">
        <template #title>
          <h1 class="logo">CoWrite</h1>
        </template>

        <!-- 登录方式切换 -->
        <a-radio-group
            v-model="mode"
            type="button"
            class="tab-selector"
            @change="switchMode"
        >
          <a-radio value="email">
            <MailIcon class="icon" />
            邮箱登录
          </a-radio>
          <a-radio value="account">
            <UserIcon class="icon" />
            账号登录
          </a-radio>
        </a-radio-group>

        <transition name="slide-fade" mode="out-in">
          <a-form
              :key="mode"
              :model="formData"
              @submit="handleLogin"
              layout="vertical"
              class="login-form"
              :required-symbol="false"
          >
            <div v-if="mode === 'email'" class="mode-block">
              <a-form-item
                  field="email"
                  label=""
                  :rules="[
                    { required: true, message: '请输入邮箱' },
                    { type: 'email', message: '请输入有效的邮箱地址' }
                  ]"
                  :validate-trigger="['blur']"
              >
                <a-input
                    v-model="formData.email"
                    placeholder="请输入邮箱"
                    size="large"
                    allow-clear
                >
                  <template #prefix>
                    <MailIcon class="icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item
                  field="code"
                  label=""
                  :rules="[
                    { required: true, message: '请输入验证码' },
                    { length: 6, message: '验证码必须为6位数字' },
                    { match: /^\d{6}$/, message: '验证码只能包含数字' }
                  ]"
                  :validate-trigger="['blur']"
              >
                <a-input-group>
                  <a-input
                      v-model="formData.code"
                      placeholder="请输入验证码"
                      size="large"
                      maxlength="6"
                      allow-clear
                  >
                    <template #prefix>
                      <LockIcon class="icon" />
                    </template>
                  </a-input>
                  <a-button
                      type="primary"
                      size="large"
                      :disabled="codeCooldown > 0 || !formData.email"
                      @click="sendCode"
                      :loading="sendingCode"
                  >
                    <template v-if="codeCooldown > 0">
                      {{ codeCooldown }}s
                    </template>
                    <template v-else>
                      发送验证码
                    </template>
                  </a-button>
                </a-input-group>
              </a-form-item>
            </div>

            <div v-else class="mode-block">
              <a-form-item
                  field="username"
                  label=""
                  :rules="[{ required: true, message: '请输入用户名' }]"
                  :validate-trigger="['blur']"
              >
                <a-input
                    v-model="formData.username"
                    placeholder="请输入用户名"
                    size="large"
                    allow-clear
                >
                  <template #prefix>
                    <UserIcon class="icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item
                  field="password"
                  label=""
                  :rules="[{ required: true, message: '请输入密码' }]"
                  :validate-trigger="['blur']"
              >
                <a-input-password
                    v-model="formData.password"
                    placeholder="请输入密码"
                    size="large"
                    allow-clear
                >
                  <template #prefix>
                    <LockIcon class="icon" />
                  </template>
                </a-input-password>
              </a-form-item>
              <div class="forgot-password">
                <a-link @click="onForgotPassword">忘记密码？</a-link>
              </div>
            </div>

            <a-form-item>
              <a-button
                  type="primary"
                  html-type="submit"
                  size="large"
                  long
                  :loading="loading"
                  class="submit-button"
              >
                <template v-if="!loading">登录</template>
                <template v-else>登录中...</template>
              </a-button>
            </a-form-item>
          </a-form>
        </transition>

        <a-divider>或</a-divider>

        <a-button
            type="outline"
            size="large"
            long
            @click="loginWithGitHub"
            class="github-button"
        >
          <template #icon>
            <GithubIcon class="icon" />
          </template>
          GitHub 登录
        </a-button>
      </a-card>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import {
  MailIcon,
  UserIcon,
  GithubIcon,
  LockIcon,
} from 'lucide-vue-next'
import { Message } from '@arco-design/web-vue'

import api from '../api/index.js';
const router = useRouter()
import { useAuth } from '../composables/useAuth'
const { setToken, setUserInfo } = useAuth()

const mode = ref<'email' | 'account'>('email')
const formData = reactive({
  email: '2148582258@qq.com',
  code: '',
  username: '',
  password: '',
})
const loading = ref(false)
const sendingCode = ref(false)
const codeCooldown = ref(0)
let timer: any = null

const bgCanvas = ref<HTMLCanvasElement | null>(null)
let rafId = 0 as number
let particles: { x: number; y: number; r: number; vx: number; vy: number; color: string }[] = []

const initBackground = () => {
  const canvas = bgCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  resize()

  // 生成柔和渐变气泡
  const colors = ['#e0f2fe', '#e9d5ff', '#dbeafe', '#ffe4e6']
  const count = Math.min(24, Math.floor((canvas.width * canvas.height) / 90000))
  particles = Array.from({ length: count }).map(() => ({
    x: Math.random() * canvas.width,
    y: Math.random() * canvas.height,
    r: 40 + Math.random() * 80,
    vx: (Math.random() - 0.5) * 0.3,
    vy: (Math.random() - 0.5) * 0.3,
    color: colors[Math.floor(Math.random() * colors.length)],
  }))

  const draw = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    // 背景渐变
    const grd = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
    grd.addColorStop(0, '#ffffff')
    grd.addColorStop(1, '#f6f7fb')
    ctx.fillStyle = grd
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    particles.forEach(p => {
      p.x += p.vx
      p.y += p.vy
      if (p.x - p.r < 0 || p.x + p.r > canvas.width) p.vx *= -1
      if (p.y - p.r < 0 || p.y + p.r > canvas.height) p.vy *= -1

      const radial = ctx.createRadialGradient(p.x, p.y, p.r * 0.2, p.x, p.y, p.r)
      radial.addColorStop(0, p.color)
      radial.addColorStop(1, 'rgba(255,255,255,0.6)')
      ctx.fillStyle = radial
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fill()
    })

    rafId = requestAnimationFrame(draw)
  }

  const onResize = () => {
    resize()
  }
  window.addEventListener('resize', onResize)

  draw()

  return () => {
    cancelAnimationFrame(rafId)
    window.removeEventListener('resize', onResize)
  }
}

onMounted(() => {
  const cleanup = initBackground()
  // 存在时在卸载清理
  onUnmounted(() => {
    if (cleanup) cleanup()
  })
})

const switchMode = (value: string) => {
  mode.value = value as 'email' | 'account'
}

// 发送验证码
const sendCode = async () => {
  if (!formData.email) {
    Message.warning('请输入邮箱')
    return
  }
  try {
    sendingCode.value = true
    await api.userApi.sendVerificationCode(formData.email)

    codeCooldown.value = 60
    timer = setInterval(() => {
      if (codeCooldown.value > 0) {
        codeCooldown.value--
      } else {
        clearInterval(timer)
      }
    }, 1000)
    Message.success('验证码已发送')
  } catch (e: any) {
    Message.error(e.message || '发送失败')
  } finally {
    sendingCode.value = false
  }
}

const handleLogin = async (data: { values: any; errors: any }) => {
  if (data.errors) {
    return
  }

  loading.value = true
  try {
    const payload =
        mode.value === 'email'
            ? { email: formData.email, code: formData.code }
            : { username: formData.username, password: formData.password }

    const response =
        mode.value === 'email'
            ? await api.userApi.login(payload)
            : await api.userApi.register(payload)

    // 登录失败时，尝试自动注册
    if (response.code === 500 && response.message === '邮箱未注册') {
      Message.info({
        content: '检测到您还未注册账号，正在为您自动注册...',
        duration: 3000
      })
      try {
        // 自动注册
        const registerData = await api.userApi.register({
          email: formData.email,
          code: formData.code
        })

        if (registerData.data?.token) {
          // 注册成功后，保存 token 和用户信息
          setToken(registerData.data.token)
          setUserInfo({
            username: registerData.data.username,
            email: registerData.data.email,
            avatarUrl: registerData.data.avatarUrl,
            bio: registerData.data.bio,
            language: registerData.data.language,
            themeDark: registerData.data.themeDark,
            status: registerData.data.status
          })
          Message.success('注册成功，正在跳转...')
          router.push('/')
        } else {
          Message.error('注册失败')
        }
      } catch (regErr: any) {
        Message.error(regErr.message || '注册失败')
      }
    } else if (response.data?.token) {
      // 登录成功，保存 token 和用户信息
      setToken(response.data.token)
      setUserInfo({
        username: response.data.username,
        email: response.data.email,
        avatarUrl: response.data.avatarUrl,
        bio: response.data.bio,
        language: response.data.language,
        themeDark: response.data.themeDark,
        status: response.data.status
      })
      Message.success('登录成功，正在跳转...')
      router.push('/')
    } else {
      Message.error(response.message || '登录失败')
    }
  } catch (err: any) {
    Message.error(err.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// GitHub 登录
const loginWithGitHub = () => {
  window.location.href =
      'https://github.com/login/oauth/authorize?client_id=YOUR_CLIENT_ID'
}

// 忘记密码处理
const onForgotPassword = () => {
  Message.info({
    content: '请联系管理员重置密码或前往找回页面',
    duration: 3000
  })
}
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  backdrop-filter: blur(8px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 20px 60px rgba(16, 24, 40, 0.12);
  animation: scaleIn 0.4s ease;
}

:deep(.arco-card-body) {
  padding: 2.2rem;
}

:deep(.arco-card-header) {
  padding: 0 0 1.2rem 0;
  border-bottom: none;
}

@keyframes scaleIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.logo {
  font-size: 30px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  text-align: center;
}

.tab-selector {
  margin-bottom: 1.5rem;
  width: 100%;
}

:deep(.tab-selector .arco-radio-group) {
  width: 100%;
  display: flex;
  gap: 8px;
}

:deep(.tab-selector .arco-radio-button) {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

:deep(.tab-selector .arco-radio-button .icon) {
  width: 16px;
  height: 16px;
}

.mode-block {
  transition: all 0.3s ease;
}

.login-form {
  margin-top: 1rem;
}

:deep(.login-form .arco-form-item) {
  margin-bottom: 1.2rem;
}

:deep(.login-form .arco-form-item-label) {
  display: none !important;
}

:deep(.login-form .arco-form-item-label-required) {
  display: none !important;
}

:deep(.login-form .arco-input-wrapper) {
  border-radius: 8px;
}

:deep(.login-form .arco-input-group) {
  display: flex;
  gap: 8px;
}

:deep(.login-form .arco-input-group .arco-input-wrapper) {
  flex: 1;
}

.forgot-password {
  text-align: right;
  margin-top: -0.8rem;
  margin-bottom: 0.5rem;
}

.submit-button {
  margin-top: 0.5rem;
  border-radius: 8px;
  font-weight: 600;
}

.github-button {
  border-radius: 8px;
  font-weight: 500;
}

.github-button .icon {
  width: 18px;
  height: 18px;
}

:deep(.arco-divider-text) {
  color: #86909c;
  font-size: 13px;
}

</style>
