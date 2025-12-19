<script setup lang="ts">
import { reactive,ref } from 'vue'
import Textarea from '../../components/Textarea.vue'
import api from '../../api/index'
import { useAuth } from '../../composables/useAuth'
import { User, AtSign, FileText, Palette, MailCheck, Globe, Save, Edit3 } from 'lucide-vue-next'
import { Message } from '@arco-design/web-vue'
const { getUserInfo, setUserInfo } = useAuth()
const userInfo = getUserInfo

// 使用 reactive 来管理 user 数据
const user = reactive({
  username: userInfo.value?.username || '',
  email: userInfo.value?.email || '',
  avatar_url: userInfo.value?.avatarUrl || 'https://via.placeholder.com/150',
  bio: userInfo.value?.bio || '这是我的个人简介',
  theme_dark: userInfo.value?.themeDark ? 1 : 0, // 转换为 1（深色） 或 0（浅色）
  email_notifications: 1, // 默认开启邮件通知
  language: userInfo.value?.language || 'zh-CN',
})

// 编辑状态
const editing = ref(false)

// 切换编辑状态
function toggleEdit() {
  editing.value = !editing.value
}

// 更新用户信息（假设为保存的操作）
const saveChanges = async () => {
  try {
    // 调用 updateUserInfo 接口，传递 user 数据
    const response = await api.userApi.updateUserInfo(user);
    console.log('用户信息更新成功', response);

    const updatedUserInfo = { ...userInfo.value, username: user.username, bio: user.bio, language: user.language, themeDark: user.theme_dark }; // 更新 avatarUrl
    setUserInfo(updatedUserInfo);  // 更新 useAuth 中的 userInfo

    // 关闭编辑模式
    editing.value = false;
  } catch (error) {
    console.error('用户信息更新失败', error);
  }
};

// 头像上传处理
const handleAvatarUpload = async (fileList: any[]) => {
  const file = fileList[0]?.file
  if (!file) return
  
  console.log(file); // 打印文件信息，检查文件是否正确传递
  try {
    const response = await api.userApi.uploadAvatar(file);
    console.log('头像上传成功', response);
    if (response.code === 200 && response.data) {
      user.avatar_url = response.data;  // 假设返回的新头像 URL
      // 同步更新 useAuth 中的 userInfo
      const updatedUserInfo = { ...userInfo.value, avatarUrl: user.avatar_url }; // 更新 avatarUrl
      setUserInfo(updatedUserInfo);  // 更新 useAuth 中的 userInfo
      Message.success('头像上传成功')
    } else {
      Message.error(response.message || '头像上传失败')
    }
  } catch (error: any) {
    console.error('头像上传失败', error);
    const errorMessage = error.response?.data?.message || error.message || '头像上传失败'
    Message.error(errorMessage)
  }
};
</script>

<template>
  <div class="profile-page">
    <!-- 用户个人信息展示 -->
    <div class="profile-header">
      <div class="profile-avatar">
        <a-upload
            :auto-upload="false"
            :show-file-list="false"
            @change="handleAvatarUpload"
            accept="image/*"
        >
          <template #upload-button>
            <a-avatar
                :size="80"
                :src="user.avatar_url"
                :style="{ cursor: 'pointer' }"
            >
              {{ user.username?.[0]?.toUpperCase() || 'U' }}
            </a-avatar>
          </template>
        </a-upload>
        <a-typography-text type="secondary" style="display: block; margin-top: 8px; font-size: 12px; text-align: center;">
          点击上传头像
        </a-typography-text>
      </div>
      <div class="profile-info">
        <h1 class="title"><User class="t-icon" /> {{ editing ? '编辑个人资料' : '个人资料' }}</h1>
        <p class="subtitle">在这里查看和编辑您的个人信息</p>
      </div>
    </div>

    <!-- 个人信息表单 -->
    <div class="profile-form">
      <!-- 用户名 -->
      <div class="form-group card">
        <label for="username"><AtSign class="l-icon" /> 用户名</label>
        <input
            v-if="editing"
            v-model="user.username"
            id="username"
            type="text"
            placeholder="请输入用户名"
        />
        <p v-else>{{ user.username }}</p>
      </div>

      <!-- 邮箱 -->
      <div class="form-group card">
        <label for="email"><MailCheck class="l-icon" /> 邮箱</label>
        <input disabled v-if="editing" v-model="user.email" id="email" type="email" placeholder="请输入邮箱" />
        <p v-else>{{ user.email }}</p>
      </div>

      <!-- 个人简介 -->
      <div class="form-group card">
        <label for="bio"><FileText class="l-icon" /> 个人简介</label>
        <Textarea
            v-if="editing"
            v-model="user.bio"
            :maxlength="200"
            :showCount="true"
            placeholder="请输入个人简介"
        />
        <p v-else>{{ user.bio }}</p>
      </div>

      <!-- 主题 -->
      <div class="form-group card">
        <label><Palette class="l-icon" /> 主题模式</label>
        <select v-if="editing" v-model="user.theme_dark">
          <option :value="0">浅色模式</option>
          <option :value="1">深色模式</option>
        </select>
        <p v-else>{{ user.theme_dark === 0 ? '浅色模式' : '深色模式' }}</p>
      </div>

      <!-- 邮件通知设置 -->
      <div class="form-group card">
        <label><MailCheck class="l-icon" /> 邮件通知</label>
        <select v-if="editing" v-model="user.email_notifications">
          <option :value="1">开启</option>
          <option :value="0">关闭</option>
        </select>
        <p v-else>{{ user.email_notifications === 1 ? '开启' : '关闭' }}</p>
      </div>

      <!-- 语言 -->
      <div class="form-group card">
        <label><Globe class="l-icon" /> 语言</label>
        <select v-if="editing" v-model="user.language">
          <option :value="'EN'">英语</option>
          <option :value="'ZH'">中文</option>
        </select>
        <p v-else>{{ user.language === 'EN' ? '英语' : '中文' }}</p>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <button v-if="editing" class="primary" @click="saveChanges"><Save class="btn-icon" /> 保存</button>
      <button class="secondary" @click="toggleEdit"><Edit3 class="btn-icon" /> {{ editing ? '取消' : '编辑' }}</button>
    </div>
  </div>
</template>

<style scoped>
.profile-page { padding: 24px; background-color: #ffffff; }

/* Profile Header */
.profile-header {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 32px;
}

.profile-avatar {
  position: relative;
  width: 85px;
  height: 85px;
  border-radius: 50%;
}

.profile-info {
  flex-grow: 1;
}

.title { font-size: 22px; font-weight: 700; margin-bottom: 6px; color: #0f172a; }
.t-icon { width: 18px; height: 18px; color: #2563eb; margin-right: 8px; vertical-align: -3px; }

.subtitle { font-size: 13px; color: #64748b; margin-bottom: 12px; }

.profile-form {
  margin-bottom: 20px;
}

/* Card style for each form field */
.card { background: #ffffff; padding: 14px; border-radius: 10px; border: 1px solid #e5e7eb; box-shadow: 0 1px 0 rgba(2, 6, 23, 0.04); margin-bottom: 14px; }

.form-group {
  margin-bottom: 20px;
}

label { font-weight: 600; display: flex; align-items: center; gap: 6px; margin-bottom: 8px; color: #0f172a; }
.l-icon { width: 16px; height: 16px; color: #2563eb; }

input,
textarea,
select { width: 100%; padding: 8px 10px; border: 1px solid #e5e7eb; border-radius: 8px; font-size: 13px; color: #0f172a; }

textarea {
  min-height: 100px;
}

.action-buttons { display: flex; gap: 10px; }

button { padding: 8px 12px; border: none; border-radius: 8px; cursor: pointer; font-size: 13px; display: inline-flex; align-items: center; gap: 6px; }
button .btn-icon { width: 16px; height: 16px; }
button.primary { background-color: #2563eb; color: #ffffff; }
button.primary:hover { background-color: #1d4ed8; }
button.secondary { background-color: #f8fafc; color: #0f172a; border: 1px solid #e5e7eb; }
button.secondary:hover { background-color: #eff6ff; color: #2563eb; border-color: #bfdbfe; }
</style>
