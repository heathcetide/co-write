<template>
  <div class="ai-chat-container">
    <!-- 标题栏 -->
    <a-card :bordered="false" class="header-card">
      <div class="header">
        <a-space :size="12" align="center">
          <div class="icon-wrapper">
            <Sparkles class="icon" />
          </div>
          <div>
            <a-typography-title :heading="5" style="margin: 0;">AI 对话与协作</a-typography-title>
            <a-typography-text type="secondary" style="font-size: 12px;">
              智能助手帮你写文档、总结、提取要点
            </a-typography-text>
          </div>
        </a-space>
      </div>
    </a-card>

    <!-- 对话框 -->
    <a-card :bordered="false" class="chat-card">
      <div class="message-list" ref="messageListRef">
        <a-empty 
            v-if="messages.length === 0" 
            description="开始与 AI 对话吧"
            style="margin: 40px 0;"
        />
        
        <div
            v-for="(msg, i) in messages"
            :key="i"
            :class="['message-item', msg.role]"
        >
          <div class="message-bubble">
            <a-avatar 
                :size="32" 
                :style="msg.role === 'user' ? userAvatarStyle : assistantAvatarStyle"
            >
              <template v-if="msg.role === 'user'">
                <User class="avatar-icon" />
              </template>
              <template v-else>
                <Bot class="avatar-icon" />
              </template>
            </a-avatar>
            
            <div class="message-content-wrapper">
              <div :class="['message-content', msg.role]">
                <a-typography-paragraph 
                    :style="{ margin: 0, whiteSpace: 'pre-wrap' }"
                >
                  {{ msg.content }}
                </a-typography-paragraph>
              </div>
              <a-typography-text 
                  type="secondary" 
                  style="font-size: 11px; margin-top: 4px; display: block;"
              >
                {{ formatTime(msg.timestamp) }}
              </a-typography-text>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="message-item assistant">
          <div class="message-bubble">
            <a-avatar :size="32" :style="assistantAvatarStyle">
              <Bot class="avatar-icon" />
            </a-avatar>
            <div class="message-content-wrapper">
              <div class="message-content assistant">
                <a-spin :size="16" />
                <a-typography-text type="secondary" style="margin-left: 8px;">
                  AI 正在思考...
                </a-typography-text>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-wrapper">
        <a-input-group>
          <a-textarea
              v-model="input"
              :auto-size="{ minRows: 2, maxRows: 6 }"
              placeholder="输入内容，Ctrl + Enter 发送 / Enter 换行"
              @keydown.ctrl.enter.prevent="sendMessage"
              @keydown.meta.enter.prevent="sendMessage"
              :disabled="loading"
              allow-clear
          />
          <a-button 
              type="primary" 
              @click="sendMessage"
              :loading="loading"
              :disabled="!input.trim() || loading"
              class="send-button"
          >
            <template #icon>
              <Send class="btn-icon" />
            </template>
            发送
          </a-button>
        </a-input-group>
      </div>
    </a-card>

    <!-- 操作栏 -->
    <a-card :bordered="false" class="actions-card">
      <a-space :size="16">
        <a-button 
            type="outline" 
            @click="generateDocument"
            :disabled="messages.length === 0"
        >
          <template #icon>
            <FileText class="btn-icon" />
          </template>
          生成文档
        </a-button>
        <a-button 
            type="outline" 
            @click="extractSummary"
            :disabled="messages.length === 0"
        >
          <template #icon>
            <List class="btn-icon" />
          </template>
          摘要要点
        </a-button>
        <a-button 
            type="outline" 
            @click="clearChat"
            :disabled="messages.length === 0"
        >
          <template #icon>
            <Trash2 class="btn-icon" />
          </template>
          清空对话
        </a-button>
      </a-space>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, computed } from 'vue'
import { Message } from '@arco-design/web-vue'
import { Sparkles, User, Bot, Send, FileText, List, Trash2 } from 'lucide-vue-next'
import dayjs from 'dayjs'

const input = ref('')
const loading = ref(false)
const messageListRef = ref<HTMLElement | null>(null)

const messages = ref<Array<{
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
}>>([
  { 
    role: 'assistant', 
    content: '你好！我是你的 AI 助手，可以帮你：\n\n✨ 写文档、文章\n📝 总结内容\n🔍 提取要点\n💡 回答问题\n\n有什么我可以帮你的吗？',
    timestamp: new Date()
  }
])

const userAvatarStyle = computed(() => ({
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: 'white'
}))

const assistantAvatarStyle = computed(() => ({
  background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  color: 'white'
}))

const formatTime = (date: Date) => {
  return dayjs(date).format('HH:mm')
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!input.value.trim() || loading.value) return

  const userMessage = input.value.trim()
  input.value = ''
  
  // 添加用户消息
  messages.value.push({ 
    role: 'user', 
    content: userMessage,
    timestamp: new Date()
  })
  
  scrollToBottom()
  
  // 模拟AI回复
  loading.value = true
  try {
    // TODO: 这里对接真实的AI API
    await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 1000))
    
    messages.value.push({
      role: 'assistant',
      content: `我理解你的问题："${userMessage}"\n\n这是一个模拟回复。在实际应用中，这里会调用真实的AI API来生成回复。`,
      timestamp: new Date()
    })
    
    scrollToBottom()
  } catch (error) {
    Message.error('发送消息失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const generateDocument = () => {
  if (messages.value.length === 0) {
    Message.warning('请先进行对话')
    return
  }
  Message.info('生成文档功能开发中...')
  console.log('生成文档', messages.value)
}

const extractSummary = () => {
  if (messages.value.length === 0) {
    Message.warning('请先进行对话')
    return
  }
  Message.info('摘要要点功能开发中...')
  console.log('提取要点', messages.value)
}

const clearChat = () => {
  if (messages.value.length === 0) return
  
  // 保留欢迎消息
  messages.value = [
    { 
      role: 'assistant', 
      content: '你好！我是你的 AI 助手，可以帮你：\n\n✨ 写文档、文章\n📝 总结内容\n🔍 提取要点\n💡 回答问题\n\n有什么我可以帮你的吗？',
      timestamp: new Date()
    }
  ]
  Message.success('对话已清空')
}
</script>

<style scoped>
.ai-chat-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
  height: calc(100vh - 48px);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 标题栏 */
.header-card {
  flex-shrink: 0;
}

:deep(.header-card .arco-card-body) {
  padding: 16px 20px;
}

.header {
  display: flex;
  align-items: center;
}

.icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  width: 24px;
  height: 24px;
  color: white;
}

/* 聊天卡片 */
.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

:deep(.chat-card .arco-card-body) {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
  padding-right: 8px;
}

/* 自定义滚动条 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 消息项 */
.message-item {
  margin-bottom: 20px;
}

.message-item.user {
  display: flex;
  justify-content: flex-end;
}

.message-item.assistant {
  display: flex;
  justify-content: flex-start;
}

.message-bubble {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  max-width: 75%;
}

.message-item.user .message-bubble {
  flex-direction: row-reverse;
}

.avatar-icon {
  width: 18px;
  height: 18px;
}

.message-content-wrapper {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  line-height: 1.6;
}

.message-content.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-content.assistant {
  background: #f7f8fa;
  color: #1d2129;
  border-bottom-left-radius: 4px;
  display: flex;
  align-items: center;
}

/* 输入区域 */
.chat-input-wrapper {
  flex-shrink: 0;
  border-top: 1px solid #f2f3f5;
  padding-top: 16px;
}

:deep(.chat-input-wrapper .arco-input-group) {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

:deep(.chat-input-wrapper .arco-textarea) {
  flex: 1;
}

.send-button {
  height: auto;
  padding: 8px 20px;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

/* 操作栏 */
.actions-card {
  flex-shrink: 0;
}

:deep(.actions-card .arco-card-body) {
  padding: 12px 20px;
}

/* 响应式 */
@media (max-width: 768px) {
  .ai-chat-container {
    padding: 16px;
  }
  
  .message-bubble {
    max-width: 85%;
  }
}
</style>
