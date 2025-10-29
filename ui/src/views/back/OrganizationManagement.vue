<template>
  <div class="org-page">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <!-- 胶囊返回按钮（位于左上角） -->
      <button class="gradient-button" @click="goBack">
        <svg height="20" width="20" viewBox="0 0 24 24">
          <path d="M0 0h24v24H0z" fill="none"></path>
          <path d="M15.41 7.41 14 6l-6 6 6 6 1.41-1.41L10.83 12z" fill="currentColor"></path>
        </svg>
        <span class="text">返回</span>
      </button>

      <!-- 筛选按钮 -->
      <div class="top-bar-actions">
        <button
            @click="gourpSelectType = true"
            :class="{ active: gourpSelectType }"
            class="filter-btn"
        >
          我管理的团队
        </button>
        <button
            @click="gourpSelectType = false"
            :class="{ active: !gourpSelectType }"
            class="filter-btn"
        >
          我参与的团队
        </button>
      </div>
    </div>

    <!-- 中央卡片列表 -->
    <div class="team-card-list">
      <div
          v-for="team in teams"
          :key="team.id"
          class="team-card card"
      >
        <div class="card-header">
          <div class="team-info">
            <img :src= "avatar"  class="team-avatar" alt=""/>
            <div class="team-meta">
              <div class="team-name">{{ team.name }}</div>
              <div class="team-desc">{{ team.description }}</div>
              <div class="member-count"><Users class="inline-icon" /> 成员：{{ team.currentMembers }}</div>
            </div>
          </div>
        </div>

        <div class="card-footer alt">
          <button class="new-action-btn invite"  @click="showInviteModal(team.id)">邀请协作</button>
          <button class="new-action-btn learn">了解更多</button>
          <button class="new-action-btn manage">管理团队</button>
        </div>
      </div>
    </div>

    <!-- 更新后的邀请协作模态框 -->
    <div class="modal" v-if="showModal">
      <form class="form">
        <label class="title">生成团队邀请码</label>

        <!-- 成员角色选择 -->
        <div class="form-group">
          <div class="select-wrapper">
            <UserPlus class="input-icon" />
            <select class="role-select" v-model="selectedRole">
              <option value="OWNER">OWNER</option>
              <option value="ADMIN">ADMIN</option>
              <option value="MEMBER">MEMBER</option>
            </select>
          </div>
        </div>

        <!-- 最大使用次数 -->
        <div class="form-group">
          <div class="input-wrapper">
            <Hash class="input-icon" />
            <input
                class="input"
                type="number"
                placeholder="最大使用次数"
                v-model="maxUsage"
                min="1"
            >
          </div>
        </div>

        <!-- 截止日期 -->
        <div class="form-group">
          <div class="input-wrapper">
            <Clock class="input-icon" />
            <input
                class="input"
                type="datetime-local"
                v-model="expiryDate"
            >
          </div>
        </div>

        <!-- 生成的邀请码和短链接 -->
        <div class="result-container" v-if="inviteCode">
          <div class="invite-code-container">
            <span class="invite-code-label">邀请码:</span>
            <span class="invite-code">{{ inviteCode }}</span>
          </div>

          <div class="short-link-container">
            <span class="short-link-label">短链接:</span>
            <span class="short-link">{{ shortLink }}</span>
          </div>

          <div class="success-message">
            <strong>邀请码已成功生成啦，您可以导出。</strong>
          </div>

          <!-- 二维码预留位置 -->
          <div class="qr-code-placeholder">
            <div class="qr-code-container">
              <!-- 引入二维码组件，v-if控制显示 -->
              <qrcode-vue
                  v-if="qrCodeUrl"
                  :value="qrCodeUrl"
                  :size="140"
                  level="H"
                  background="#ffffff"
                  foreground="#333333"
              />
              <span class="qr-hint" v-else>扫码加入团队</span>
            </div>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="modal--footer">
          <button class="cancel-btn" type="button" @click="closeModal">取消</button>
          <button class="generate-btn" type="button" @click="generateInvite">生成</button>
        </div>

        <button class="export-btn" type="button" v-if="inviteCode" @click="exportToPDF">
          导出邀请码为PDF
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";
import dayjs from "dayjs";
import QrcodeVue from 'qrcode.vue';
import { jsPDF } from 'jspdf';
import html2canvas from 'html2canvas';
import { Users, UserPlus, Hash, Clock } from 'lucide-vue-next'


const router = useRouter()
const goBack = () => router.back()

// 定义团队字段的类型接口（与Java实体类对应）
interface Team {
  id: number; // 主键ID（对应Java的Long，TS中用number）
  name: string; // 组织名称（对应Java的name字段）
  description: string; // 组织描述（对应Java的description字段，前端模板中可用desc显示）
  ownerId: number; // 组织拥有者的用户ID（对应Java的ownerId，Long类型对应TS的number）
  status: string; // 组织状态（如：'active'、'disabled'）
  published: string; // 是否公开发布（如：'yes'/'no' 或 '1'/'0'）
  maxMembers: number; // 最大成员数量限制（对应Java的maxMembers，Integer类型）
  currentMembers: number; // 成员数量
  createdAt: string;      // 创建时间
  updatedAt: string;      // 更新时间
  deleted: boolean;       // 补充（后端是 false，TS 用 boolean）
  // 补充：如果后端返回了头像或当前成员数，可添加以下字段（根据实际接口返回补充）
  // avatar?: string; // 组织头像（可选，若后端有返回则添加）
}

// 接口完整响应类型，包含 code、message、data（团队数组）
interface ApiResponse {
  code: number;
  message: string;
  data: Team[];
}

// 新增：控制生成按钮状态
const isGenerating = ref(false); // 防止重复点击
const teams = ref<Team[]>([]); // 存储全部团队列表
const gourpSelectType = ref(true) // 表示true:我的团队，false:我参与的团队
const showModal = ref(false); // 控制弹窗显示
const currentTeamId = ref<number | null>(null); // 当前团队 ID
const inviteCode = ref(""); // 邀请码
const selectedRole = ref('MEMBER') // userType
const maxUsage = ref(1) // 最大使用次数
const expiryDate = ref('') // 截止日期
const shortLink = ref('') // 短链接
const qrCodeUrl = ref('') // 二维码链接
const avatar = ref('') // 用于mock头像


/**
 * 根据类型调用不同后端接口
 * */
const fetchTeams = async () => {
  try {
    // 拼接接口地址：根据类型调用不同接口（或同一接口传不同参数）
    const url = gourpSelectType.value
        ? "http://localhost:3000/api/organization/organized/manage"  // 我管理的团队接口
        : "http://localhost:3000/api/organization/organized";  // 我参与的团队接口

    const res = await axios.post<ApiResponse>(
        url,
        {},
        {headers: {
            Authorization: localStorage.getItem("token")
          }});
    console.log('获取团队成功,团队数据为: ', res.data);
    teams.value = res.data.data; // 直接存储后端返回的对应类型数据
  } catch (err) {
    console.error('获取团队失败', err);
    teams.value = []; // 实现失败之后也要清理之前的数据残余
  }
};

/**
 * 切换查询状态后的页面更新
 * */
// 1. 页面加载时初始化数据
onMounted(() => {
  fetchTeams();
});

// 2. 监听类型变化，重新请求数据
watch(gourpSelectType, () => {
  console.log("类型切换：", gourpSelectType.value);
  fetchTeams(); // 类型切换时，重新新调用接口
});


/**
 * 点击“邀请协作”时触发
 * 获取团队ID，并打开弹窗
 * @param {Number} teamId - 团队 ID
 */
const showInviteModal = (teamId: number) => {
  currentTeamId.value = teamId; // 保存团队 ID
  showModal.value = true; // 打开弹窗
  console.log("当前团队 ID：", teamId);
};

/**
 * 生成邀请码并调用接口
 */
const generateInvite = async () => {
  // 校验团队 ID 是否存在
  if (!currentTeamId.value) {
    console.error("未获取到团队信息");
    return;
  }

  // 禁用按钮，防止重复提交
  isGenerating.value = true;

  try {
    //序列化前端接收的时间，和后端format保持一致
    const formattedExpiresAt = dayjs(expiryDate.value).format('YYYY-MM-DDTHH:mm:00');
    // 调用后端接口
    const response = await axios.post(
        "http://localhost:3000/api/organization/invite/create",
        {
          organizationId: currentTeamId.value,
          role: selectedRole.value,
          maxUses: maxUsage.value,
          expiresAt: formattedExpiresAt
        },
        {
          headers: {
            Authorization: localStorage.getItem("token")
          }
        }
    );
    console.log("接口响应结果为：", response.data);
    if (response.status != 200) {
      console.error("生成邀请验证码失败: ", response.data)
      return ;
    }

    if (response.data.data != null){
      const {inviteCode: code, shortUrl: slink, qrCodeUrl: qrcodeurl} = response.data.data;
      inviteCode.value = code;
      shortLink.value = slink;
      qrCodeUrl.value = qrcodeurl;

      // 生成成功后，可滚动到结果区域
      document.querySelector('.result-container')?.scrollIntoView({ behavior: 'smooth' });
    }

  } catch (error) {
    console.error("失败：", error);
    alert("生成失败");
  }finally {
    // 释放“生成”按钮的点击许可
    isGenerating.value = false;
  }
};

/**
 * 导出PDF方法实现
 * */
const exportToPDF = async() => {
  if (!inviteCode.value){
    alert('请先生成邀请码')
    return ;
  }
  // 1. 选择将要导出的DOM区域
  const exportContent = document.querySelector('.result-container')as HTMLElement;

  if(!exportContent){
    console.error('未找到导出区域')
    return ;
  }

  try {
    // 2. 将HTML转换成Canvas高清渲染:
    const canvas = await html2canvas(exportContent, {
      scale: 2,
      useCORS: true,
    }) ;
    // 3. 初始化pdf的展示尺寸
    const pdf = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4',
    });
    // 4. 计算Canvas在PDF当中的占用尺寸:
    const imgWigth = 210 // 基本A4纸张宽度(mm)
    const imgHeight = canvas.height * (imgWigth / canvas.width)
    // 5. 将Canvas内容添加到PDF中
    pdf.addImage(canvas.toDataURL('img/jpeg', 0.95),
        'JPEG',
        10,
        10,
        imgWigth,
        imgHeight
    );
    // 6.保存PDF文件
    pdf.save(`团队邀请二维码pdf文件:_${inviteCode.value}.pdf`);
    alert("PDF导出成功！");
  } catch (error) {
    console.error("PDF导出失败：", error);
    alert("PDF导出失败,请重试...");
  }
};



/**
 * 关闭弹窗
 */
const closeModal = () => {
  showModal.value = false;
  inviteCode.value = "";
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap');

.org-page {
  min-height: 100vh;
  padding: 32px;
  background: linear-gradient(to right, #f5f0ff, #ece6ff);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.top-bar {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 20px;
}

.gradient-button {
  display: flex;
  align-items: center;
  font-size: 14px;
  padding: 0.5em 1.1em;
  color: white;
  background: #2563eb;
  border: none;
  border-radius: 999em;
  box-shadow: 0 2px 8px rgba(2, 6, 23, 0.08);
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
}
.gradient-button svg {
  margin-right: 6px;
}
.gradient-button .text {
  margin-left: 2px;
}

.team-card-list {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  justify-content: center;
  max-width: 1400px;
  width: 100%;
}

.team-card {
  width: 340px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(2, 6, 23, 0.06);
  padding: 24px;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}
.team-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 16px 32px rgba(2, 6, 23, 0.12);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.team-info {
  display: flex;
  align-items: center;
}
.team-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  margin-right: 14px;
  object-fit: cover;
}
.team-meta {
  display: flex;
  flex-direction: column;
}
.team-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.team-desc {
  font-size: 13px;
  color: #777;
  margin-top: 4px;
}
.member-count {
  font-size: 13px;
  color: #666;
  margin-top: 6px;
}

.card-footer.alt {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-top: 16px;
}

.new-action-btn {
  flex: 1;
  padding: 12px 16px;
  border-radius: 50px;
  cursor: pointer;
  border: 0;
  background-color: #eff6ff;
  color: #1d4ed8;
  box-shadow: rgb(0 0 0 / 5%) 0 0 8px;
  letter-spacing: 1px;
  text-transform: uppercase;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.invite:hover {
  letter-spacing: 2px;
  background-color: #2563eb;
  color: white;
  box-shadow: rgba(2, 6, 23, 0.12) 7px 29px ;
}
.learn:hover {
  letter-spacing: 2px;
  background-color: #1d4ed8;
  color: white;
  box-shadow: rgba(2, 6, 23, 0.12) 7px 29px ;
}
.manage:hover {
  letter-spacing: 2px;
  background-color: #93c5fd;
  color: #1d4ed8;
  box-shadow: rgba(2, 6, 23, 0.12) 7px 29px ;
}

.new-action-btn:active {
  transform: translateY(4px);
  transition: 100ms;
  box-shadow: none;
}

/* 更新后的模态框样式 */
.modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 450px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 32px rgba(124, 100, 255, 0.2);
  padding: 24px;
  z-index: 9999;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #4e3ca9;
  text-align: center;
  margin-bottom: 10px;
}

.form-group {
  margin-bottom: 16px;
}

.select-wrapper,
.input-wrapper {
  display: flex;
  align-items: center;
  position: relative;
  width: 100%;
}

.role-select,
.input {
  width: 100%;
  height: 48px;
  padding: 0 1.5rem;
  padding-left: 3rem;
  border: 2px solid #f0ebff;
  border-radius: 10px;
  outline: none;
  background-color: #f8fafc;
  color: #0d0c22;
  font-size: 14px;
  transition: all 0.3s ease;
}

.role-select {
  appearance: none;
  cursor: pointer;
}

.input::placeholder {
  color: #94a3b8;
}

.input:focus,
.input:hover,
.role-select:focus,
.role-select:hover {
  outline: none;
  border-color: #7e68ff;
  background-color: #fff;
  box-shadow: 0 0 0 4px rgba(126, 104, 255, 0.1);
}

.result-container {
  margin-top: 16px;
}

.invite-code-container,
.short-link-container {
  background: #f5f0ff;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 8px;
}

.invite-code-label,
.short-link-label {
  font-size: 14px;
  font-weight: 600;
  color: #4e3ca9;
  margin-right: 8px;
}

.invite-code {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.short-link {
  font-size: 14px; /* 适当放大字体提升可读性 */
  font-weight: 600; /* 加粗字体增强视觉突出度 */
  color: #333333; /* 深黑色，比纯黑更柔和不刺眼 */
  word-break: break-all; /* 保留原有换行功能 */
}
.success-message {
  font-size: 13px;
  color: #666;
  text-align: center;
  margin: 16px 0;
}

.qr-code-placeholder {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.qr-code-container {
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f0ff;
  border-radius: 12px;
  padding: 16px;
}

.qr-code {
  width: 100%;
  height: 100%;
  background: white;
  border: 1px dashed #c3baff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7e68ff;
  font-size: 14px;
}

.qr-hint {
  text-align: center;
  color: #7e68ff;
  font-weight: 500;
}

.modal--footer {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 20px;
}

.cancel-btn,
.generate-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn {
  background: #f0ebff;
  color: #4e3ca9;
  border: none;
}

.cancel-btn:hover {
  background: #e0d6ff;
}

.generate-btn {
  background: linear-gradient(90deg, #7e68ff, #c3baff);
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(126, 104, 255, 0.3);
}

.generate-btn:hover {
  background: linear-gradient(90deg, #6d58e8, #b2a5ff);
}

.export-btn {
  width: 100%;
  padding: 12px;
  margin-top: 16px;
  background: transparent;
  border: 1px solid #7e68ff;
  color: #7e68ff;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.export-btn:hover {
  background: rgba(126, 104, 255, 0.1);
}

.emoji-icon {
  position: absolute;
  left: 16px;
  z-index: 10;
  font-size: 18px;
}

/* Adjust padding for inputs with emoji */
.select-wrapper,
.input-wrapper {
  position: relative;
}

.role-select,
.input {
  padding-left: 46px !important; /* Make room for emoji */
}

/*选择团队查看方式的样式内容: */
.top-bar {
  width: 100%;
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  justify-content: flex-start; /* 整体靠左 */
  gap: 24px; /* 返回按钮与筛选按钮组的间距 */
  margin-bottom: 20px;
}

/* 新增：筛选按钮容器，用 Flex 控制按钮水平排列 */
.top-bar-actions {
  display: flex;
  align-items: center;
  gap: 16px; /* 按钮之间的间距 */
}

/* 筛选按钮基础样式：继承整体配色，简洁大气 */
.filter-btn {
  background: linear-gradient(90deg, #7e68ff, #c3baff);
  color: white;
  font-size: 14px;
  padding: 0.5em 1em;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(126, 104, 255, 0.3);
}

/* 激活态样式：保持原有逻辑，可微调 */
.filter-btn.active {
  background: linear-gradient(90deg, #6d58e8, #b2a5ff);
  box-shadow: 0 4px 12px rgba(126, 104, 255, 0.4);
  transform: translateY(-2px);
}

/*  hover 动画：与整体风格统一，轻量位移+阴影 */
.filter-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(126, 104, 255, 0.4);
}

/* 点击动画：还原位移，保持简洁 */
.filter-btn:active {
  transform: translateY(1px);
  box-shadow: 0 2px 6px rgba(126, 104, 255, 0.3);
}
</style>