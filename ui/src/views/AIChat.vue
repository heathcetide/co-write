<template>
  <div class="ai-chat-container">
    <!-- 标题栏 -->
    <div class="header">
      <Sparkles class="icon" />
      <h1>AI 对话与协作</h1>
    </div>

    <!-- 对话框 -->
    <div class="chat-box">
      <div class="message-list">
        <div
            v-for="(msg, i) in messages"
            :key="i"
            :class="['message', msg.role]"
        >
          <div class="bubble">
            <component :is="msg.role === 'user' ? User : Bot" class="msg-icon" />
            <div class="msg-content">{{ msg.content }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <div class="input-area">
    <textarea
        v-model="input"
        @keydown.ctrl.enter.prevent="sendMessage"
        placeholder="输入内容，Ctrl + Enter 发送 / Enter换行"
    />
        </div>
        <button class="send-btn" @click="sendMessage">
          <Send class="btn-icon" />
        </button>
      </div>

    </div>

    <!-- 操作栏 -->
    <div class="chat-actions">
      <button @click="generateDocument"><FileText class="btn-icon" />生成文档</button>
      <button @click="extractSummary"><List class="btn-icon" />摘要要点</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// lucide icons
import { Sparkles, User, Bot, Send, FileText, List } from 'lucide-vue-next'

const input = ref('')
const messages = ref([
  { role: 'assistant', content: '你好，我是你的 AI 助手，可以帮你写文档、总结、提取要点！' }
])

const sendMessage = () => {
  if (!input.value.trim()) return

  messages.value.push({ role: 'user', content: input.value })

  setTimeout(() => {
    messages.value.push({
      role: 'assistant',
      content: `这是对你的问题的回应：“${input.value}”`
    })
  }, 600)

  input.value = ''
}

const generateDocument = () => {
  console.log('生成文档', messages.value)
}
const extractSummary = () => {
  console.log('提取要点', messages.value)
}
</script>

<style scoped>
.ai-chat-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 32px 24px;
  font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  color: #1a1a1a;
}

.header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}
.header .icon {
  width: 28px;
  height: 28px;
  color: #6a3dc8;
}
.header h1 {
  font-size: 24px;
  font-weight: 600;
}

.chat-box {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(106, 61, 200, 0.06);
  display: flex;
  flex-direction: column;
  height: 740px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 12px;
  padding-right: 4px;
}
.message {
  margin-bottom: 12px;
}
.message .bubble {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.message .msg-icon {
  width: 20px;
  height: 20px;
  margin-top: 3px;
  color: #6a3dc8;
}
.message .msg-content {
  background-color: #f9f7fd;
  padding: 10px 14px;
  border-radius: 12px;
  max-width: 80%;
  font-size: 14px;
  line-height: 1.5;
}
.message.user .msg-content {
  background-color: #e6f0ff;
  align-self: flex-end;
}
.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: #faf9fe;
  border-radius: 20px;
  padding: 12px;
  border: 1px solid #e3d8f8;
  box-shadow: inset 0 1px 2px rgba(106, 61, 200, 0.05);
}

.input-area {
  flex: 1;
}

.input-area textarea {
  width: 100%;
  height: 68px;
  resize: none;
  padding: 10px 14px;
  font-size: 14px;
  border: none;
  outline: none;
  border-radius: 16px;
  background: transparent;
  font-family: inherit;
  color: #333;
}

.input-area textarea::placeholder {
  color: #aaa;
}

.send-btn {
  background-color: #6a3dc8;
  border: none;
  color: white;
  padding: 10px 12px;
  border-radius: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background 0.25s ease;
}

.send-btn:hover {
  background-color: #5430ad;
}

.send-btn .btn-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.2s ease;
}

.send-btn:hover .btn-icon {
  transform: scale(1.2);
}

.btn-icon {
  width: 18px;
  height: 18px;
  vertical-align: middle;
}

.chat-actions {
  display: flex;
  justify-content: flex-start;
  gap: 16px;
  margin-top: 20px;
}
.chat-actions button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  font-size: 14px;
  border-radius: 8px;
  border: 1px solid #e0d8f0;
  background: #f6f3fe;
  color: #6a3dc8;
  cursor: pointer;
  transition: 0.2s;
}
.chat-actions button:hover {
  background: #e9e1fd;
}
</style>
