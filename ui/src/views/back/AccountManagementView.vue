<script setup lang="ts">
import { ref } from 'vue'
import {
  Laptop,
  MapPin,
  ShieldCheck,
  ShieldX,
  Lock,
  Ban,
  User,
  Monitor
} from 'lucide-vue-next'

// 模拟登录记录数据
const loginRecords = ref([
  {
    id: 1,
    ip: '183.128.12.1',
    location: '杭州',
    device: 'Windows 11 / Chrome 120',
    loginTime: '2025-07-24T08:30:12',
    isBlocked: false
  },
  {
    id: 2,
    ip: '171.214.3.9',
    location: '成都',
    device: 'macOS 14 / Safari',
    loginTime: '2025-07-23T22:01:03',
    isBlocked: false
  },
  {
    id: 3,
    ip: '103.41.12.43',
    location: '未知',
    device: 'Android / 微信内置浏览器',
    loginTime: '2025-07-22T19:11:40',
    isBlocked: true
  }
])

const formatDate = (iso: string) => {
  const date = new Date(iso)
  return date.toLocaleString()
}

const blockIp = (id: number) => {
  const record = loginRecords.value.find(r => r.id === id)
  if (record && !record.isBlocked) {
    record.isBlocked = true
  }
}
</script>

<template>
  <div class="device-view">
    <div class="header">
      <User class="icon" />
      <h1 class="title">账号登录设备</h1>
    </div>
    <p class="subtitle">您可查看最近的登录行为，并对可疑 IP 进行封禁。</p>

    <div class="device-list">
      <div class="device-card" v-for="record in loginRecords" :key="record.id">
        <div class="left">
          <div class="device-icon-wrapper">
            <Monitor class="device-icon" />
          </div>
          <div class="info">
            <div class="device-name">{{ record.device }}</div>
            <div class="meta">
              <MapPin class="meta-icon" />
              <span>{{ record.location }} · {{ record.ip }}</span>
            </div>
            <div class="time">{{ formatDate(record.loginTime) }}</div>
          </div>
        </div>
        <div class="actions">
          <div class="status" :class="{ blocked: record.isBlocked, safe: !record.isBlocked }">
            <template v-if="record.isBlocked">
              <ShieldX class="status-icon" /> 已封禁
            </template>
            <template v-else>
              <ShieldCheck class="status-icon" /> 正常
            </template>
          </div>
          <button
              class="block-btn"
              :disabled="record.isBlocked"
              @click="blockIp(record.id)"
          >
            <template v-if="record.isBlocked">
              <Ban class="btn-icon" /> 已封禁
            </template>
            <template v-else>
              <Lock class="btn-icon" /> 封禁
            </template>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.device-view {
  padding: 24px;
  background-color: #ffffff;
  font-family: system-ui, sans-serif;
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
  margin-bottom: 20px;
}

.device-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.device-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 1px 0 rgba(2, 6, 23, 0.04);
  transition: all 0.2s;
}

.device-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.left {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.device-icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.device-icon {
  width: 20px;
  height: 20px;
  color: #2563eb;
}

.info {
  display: flex;
  flex-direction: column;
}

.device-name {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}

.meta {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6b7280;
  gap: 4px;
}

.meta-icon {
  width: 12px;
  height: 12px;
  color: #9ca3af;
}

.time {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}

.actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  min-width: 100px;
}

.status {
  display: flex;
  align-items: center;
  font-weight: 500;
  font-size: 13px;
}

.status-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

.status.safe {
  color: #10b981;
}

.status.blocked {
  color: #ef4444;
}

.block-btn {
  background-color: #f8fafc;
  border: 1px solid #e5e7eb;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 13px;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.block-btn:hover {
  background-color: #eff6ff;
  border-color: #bfdbfe;
  color: #2563eb;
}

.block-btn:disabled {
  background-color: #f1f5f9;
  color: #9ca3af;
  cursor: not-allowed;
  border-color: #e5e7eb;
}

.btn-icon {
  width: 14px;
  height: 14px;
}
</style>
