<template>
  <div class="checkin-card">
    <div class="checkin-container">
      <!-- 月份选择 -->
      <div class="month-selector">
        <button class="btn" @click="changeMonth(-1)">← 上月</button>
        <div class="month-picker" @click="toggleMonthPicker">
          {{ currentMonthText }}
          <div class="month-dropdown" v-if="showMonthPicker">
            <div class="year-selector">
              <button @click="changeYear(-1)">←</button>
              <span>{{ currentYear }}</span>
              <button @click="changeYear(1)">→</button>
            </div>
            <div class="months-grid">
              <button
                  v-for="month in 12"
                  :key="month"
                  @click="selectMonth(month)"
                  :class="{ active: month === currentMonthNum }"
              >
                {{ month }}月
              </button>
            </div>
          </div>
        </div>
        <button class="btn" @click="changeMonth(1)">下月 →</button>
      </div>

      <!-- 标题 -->
      <h2 class="title">
        <span class="gradient-text">每日刷题目标</span>
      </h2>

      <!-- 日历主体 -->
      <div class="calendar-wrapper">
        <!-- 周标签 -->
        <div class="week-labels">
          <div v-for="day in ['周一', '周二', '周三', '周四', '周五', '周六', '周日']" :key="day" class="week-label">
            {{ day }}
          </div>
        </div>

        <!-- 日期格子 -->
        <div class="calendar-grid">
          <div
              v-for="n in startDayOffset"
              :key="'empty-' + n"
              class="calendar-day empty"
          ></div>
          <div
              v-for="(day, index) in calendarDays"
              :key="index"
              class="calendar-day"
              :class="{ today: isToday(day.date) }"
              @mouseenter="showTooltip(day, $event)"
              @mouseleave="hideTooltip"
          >
            <div class="dual-water-container">
              <div class="water-column interview">
                <div
                    class="water-level"
                    :style="{
                    '--water-height': `${getWaterLevel(day.interview)}%`,
                    height: `${getWaterLevel(day.interview)}%`
                  }"
                >
                  <div class="water-wave wave-1"></div>
                  <div class="water-wave wave-2"></div>
                </div>
              </div>
              <div class="water-column algorithm">
                <div
                    class="water-level"
                    :style="{
                    '--water-height': `${getWaterLevel(day.algorithm)}%`,
                    height: `${getWaterLevel(day.algorithm)}%`
                  }"
                >
                  <div class="water-wave wave-1"></div>
                  <div class="water-wave wave-2"></div>
                </div>
              </div>
            </div>
            <div class="day-label">{{ day.date.split('-')[2] }}</div>
          </div>
        </div>
      </div>

      <!-- 提示框 -->
      <div
          v-if="activeDay"
          class="calendar-tooltip"
          :style="{
          left: tooltipX + 'px',
          top: tooltipY + 'px'
        }"
      >
        <div class="tooltip-header">
          <span class="icon-book">{{ bookIcon }}</span>
          <span>{{ activeDay.date }}</span>
        </div>
        <div class="tooltip-content">
          <div class="progress-group">
            <div class="progress-item interview">
              <span class="icon-interview">{{ interviewIcon }}</span>
              <div class="circle-progress">
                <svg width="80" height="80" viewBox="0 0 100 100">
                  <circle
                      cx="50" cy="50" r="45"
                      fill="none"
                      stroke="#eee"
                      stroke-width="10"
                  />
                  <circle
                      cx="50" cy="50" r="45"
                      fill="none"
                      :stroke="activeDay.interview >= 20 ? '#52c41a' : '#faad14'"
                      stroke-width="10"
                      stroke-dasharray="283"
                      :stroke-dashoffset="283 - (283 * getWaterLevel(activeDay.interview) / 100)"
                      transform="rotate(-90 50 50)"
                      stroke-linecap="round"
                  />
                  <text
                      x="50" y="55"
                      text-anchor="middle"
                      font-size="16"
                      :fill="activeDay.interview >= 20 ? '#52c41a' : '#faad14'"
                  >
                    {{ activeDay.interview }}题
                  </text>
                </svg>
              </div>
            </div>
            <div class="progress-item algorithm">
              <span class="icon-algorithm">{{ algorithmIcon }}</span>
              <div class="circle-progress">
                <svg width="80" height="80" viewBox="0 0 100 100">
                  <circle
                      cx="50" cy="50" r="45"
                      fill="none"
                      stroke="#eee"
                      stroke-width="10"
                  />
                  <circle
                      cx="50" cy="50" r="45"
                      fill="none"
                      :stroke="activeDay.algorithm >= 20 ? '#52c41a' : '#1890ff'"
                      stroke-width="10"
                      stroke-dasharray="283"
                      :stroke-dashoffset="283 - (283 * getWaterLevel(activeDay.algorithm) / 100)"
                      transform="rotate(-90 50 50)"
                      stroke-linecap="round"
                  />
                  <text
                      x="50" y="55"
                      text-anchor="middle"
                      font-size="16"
                      :fill="activeDay.algorithm >= 20 ? '#52c41a' : '#1890ff'"
                  >
                    {{ activeDay.algorithm }}题
                  </text>
                </svg>
              </div>
            </div>
          </div>
          <div class="tooltip-text">
            <span class="text-secondary">每日目标：20题</span>
            <span :class="activeDay.interview >= 20 ? 'text-success' : 'text-error'">
              {{ activeDay.interview >= 20 ? '已完成' : '未完成' }}
            </span>
          </div>
          <div class="tooltip-text">
            <span class="text-secondary">每日目标：20题</span>
            <span :class="activeDay.algorithm >= 20 ? 'text-success' : 'text-error'">
              {{ activeDay.algorithm >= 20 ? '已完成' : '未完成' }}
            </span>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button
            class="action-btn interview-btn"
            @click="navigateTo('/coding')"
        >
          <span class="icon-btn">{{ interviewIcon }}</span>
          去刷面试题
        </button>

        <button
            class="action-btn algorithm-btn"
            @click="navigateTo('/questions')"
        >
          <span class="icon-btn">{{ algorithmIcon }}</span>
          去刷算法题
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, watch, onMounted, reactive} from 'vue'
import {format, startOfMonth, endOfMonth, eachDayOfInterval} from 'date-fns'
import {useRouter} from 'vue-router'

// 组件属性定义
const props = defineProps({
  interviewData: {
    type: Object,
    default: () => ({})
  },
  algorithmData: {
    type: Object,
    default: () => ({})
  }
})

// 模拟数据
const mockInterviewData = {
  '2024-05-20': 15,
  '2024-05-19': 20,
  '2024-05-18': 8
}

const mockAlgorithmData = {
  '2024-05-20': 18,
  '2024-05-19': 12,
  '2024-05-17': 5
}

// 数据存储
const store = reactive({
  interview: {},
  algorithm: {}
})

// 开发环境下使用模拟数据
const interviewData = import.meta.env.DEV ? mockInterviewData : props.interviewData
const algorithmData = import.meta.env.DEV ? mockAlgorithmData : props.algorithmData

// 月份选择器状态
const currentDate = ref(new Date())
const showMonthPicker = ref(false)
const currentYear = computed(() => currentDate.value.getFullYear())
const currentMonthNum = computed(() => currentDate.value.getMonth() + 1)
const currentMonthText = computed(() => `${currentYear.value}年${currentMonthNum.value}月`)

// 提示框位置
const tooltipX = ref(0)
const tooltipY = ref(0)

// 数据加载状态
const isLoading = ref(false)

// 数据转换
const transformContributions = (data) => {
  const result = {interview: {}, algorithm: {}}

  data.forEach(item => {
    const dateStr = item.day
    result.interview[dateStr] = Math.min(item.interviewContributions, 20)
    result.algorithm[dateStr] = Math.min(item.questionContributions, 20)
  })

  return result
}

// 数据获取
const fetchData = async () => {
  isLoading.value = true
  try {
    const year = currentYear.value
    const month = currentMonthNum.value

    // 实际项目中替换为真实接口调用
    // const response = await authService.getUserContributionsByMonth(year, month)
    // const transformed = transformContributions(response.data)
    // Object.assign(store.interview, transformed.interview)
    // Object.assign(store.algorithm, transformed.algorithm)
  } catch (error) {
    console.error('数据加载失败：', error)
  } finally {
    isLoading.value = false
  }
}

// 月份选择器方法
const toggleMonthPicker = () => {
  showMonthPicker.value = !showMonthPicker.value
}

const changeYear = (offset) => {
  currentDate.value = new Date(currentYear.value + offset, currentMonthNum.value - 1, 1)
}

const selectMonth = (month) => {
  currentDate.value = new Date(currentYear.value, month - 1, 1)
  showMonthPicker.value = false
  fetchData()
}

const changeMonth = (offset) => {
  currentDate.value = new Date(currentYear.value, currentMonthNum.value - 1 + offset, 1)
  fetchData()
}

watch(currentDate, () => {
  fetchData()
})

// 计算月份起始空白
const startDayOffset = computed(() => {
  const firstDay = startOfMonth(currentDate.value)
  const day = firstDay.getDay() // 0是周日，1是周一...6是周六
  return day === 0 ? 6 : day - 1
})

// 日历数据
const calendarDays = computed(() => {
  const days = eachDayOfInterval({
    start: startOfMonth(currentDate.value),
    end: endOfMonth(currentDate.value)
  })

  return days.map(day => {
    const dateStr = format(day, 'yyyy-MM-dd')
    return {
      date: dateStr,
      interview: Math.min(interviewData[dateStr] || 0, 20),
      algorithm: Math.min(algorithmData[dateStr] || 0, 20)
    }
  })
})

// 提示框状态
const activeDay = ref(null)

const isToday = (dateStr) => {
  return dateStr === format(new Date(), 'yyyy-MM-dd')
}

const getWaterLevel = (count) => {
  return Math.min(count, 20) * 5 // 20题对应100%高度
}

// 提示框位置计算
const showTooltip = (day, event) => {
  activeDay.value = day
  // 计算提示框位置，避免溢出
  const rect = event.target.getBoundingClientRect()
  const containerRect = document.querySelector('.checkin-container').getBoundingClientRect()

  tooltipX.value = rect.left - containerRect.left + 80
  tooltipY.value = rect.top - containerRect.top - 130
}

const hideTooltip = () => {
  activeDay.value = null
}

// 导航方法
const router = useRouter()
const emit = defineEmits(['close'])

const navigateTo = (path) => {
  router.push(path)
  emit('close')
}

onMounted(() => {
  fetchData()
})

// 强制更新方法
const forceUpdate = () => {
  store.interview = {...store.interview}
  store.algorithm = {...store.algorithm}
}
</script>

<style scoped lang="scss">
.checkin-card {
  max-width: 1400px;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  background: white;
}

.checkin-container {
  padding: 32px;
  position: relative;
}

.title {
  text-align: center;
  margin-bottom: 32px;
  font-size: 24px;
  font-weight: 600;

  .gradient-text {
    background: linear-gradient(90deg, #52c41a, #1890ff);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }
}

.calendar-wrapper {
  display: grid;
  grid-template-columns: 50px 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.week-labels {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(60px * 7);
  padding: 3px 0;

  .week-label {
    font-size: 18px;
    color: #666;
    height: 60px;
    display: flex;
    align-items: center;
  }
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: 60px;
  gap: 8px;
}

.calendar-day {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 8px;
  background: #f0f6ff;
  border: 1px solid #cce0ff;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    transform: scale(1.15);
    z-index: 2;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  &.today {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }

  .dual-water-container {
    display: flex;
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    top: 0;
    gap: 1px;
    background: rgba(0, 0, 0, 0.1);

    .water-column {
      flex: 1;
      position: relative;
      overflow: hidden;

      &.interview {
        .water-level {
          position: absolute;
          bottom: 0;
          width: 100%;
          background: linear-gradient(to bottom, #fff3cd, #ffe69c);
          height: var(--water-height);
          transition: height 0.5s ease;
        }

        .water-wave {
          background: linear-gradient(
                  90deg,
                  rgba(255, 255, 255, 0.1) 20%,
                  rgba(255, 255, 255, 0.2) 50%,
                  rgba(255, 255, 255, 0) 80%
          );
        }
      }

      &.algorithm {
        .water-level {
          position: absolute;
          bottom: 0;
          width: 100%;
          background: linear-gradient(to bottom, #d1f2eb, #a3e4d7);
          height: var(--water-height);
          transition: height 0.5s ease;
        }

        .water-wave {
          background: linear-gradient(
                  90deg,
                  rgba(255, 255, 255, 0.05) 20%,
                  rgba(255, 255, 255, 0.15) 50%,
                  rgba(255, 255, 255, 0) 80%
          );
        }
      }
    }
  }

  .water-wave {
    position: absolute;
    bottom: 0;
    left: -50%;
    width: 200%;
    height: 150%;
    animation: wave 2s linear infinite;

    &.wave-2 {
      animation-delay: -0.5s;
    }
  }

  .day-label {
    position: absolute;
    bottom: 6px;
    right: 6px;
    font-size: 16px;
    color: #409eff;
    font-weight: bold;
    padding: 0 2px;
    border-radius: 2px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    z-index: 1;
  }
}

@keyframes wave {
  0% {
    transform: translateX(-30%) translateY(10%);
  }
  50% {
    transform: translateX(30%) translateY(-5%);
  }
  100% {
    transform: translateX(-30%) translateY(10%);
  }
}

.calendar-tooltip {
  position: absolute;
  background: white;
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  min-width: 360px;

  .tooltip-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    font-size: 18px;
    color: #409eff;

    .icon-book {
      display: inline-block;
    }
  }

  .tooltip-content {
    .progress-group {
      display: flex;
      gap: 24px;
      justify-content: center;

      .progress-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;

        .icon-interview, .icon-algorithm {
          display: inline-block;
        }

        &.interview {
          color: #ff9800;
        }

        &.algorithm {
          color: #0097a7;
        }
      }
    }
  }

  .tooltip-text {
    display: flex;
    justify-content: space-between;
    margin-top: 12px;
    padding: 4px 0;

    .text-secondary {
      color: #888;
    }

    .text-success {
      color: #52c41a;
    }

    .text-error {
      color: #f5222d;
    }
  }
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  width: 100%;
  margin-top: 32px;
}

.action-btn {
  height: 72px;
  font-size: 24px;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: white;
  font-weight: 500;

  .icon-btn {
    display: inline-block;
  }

  &.interview-btn {
    background: linear-gradient(145deg, #ffb347, #ffcc33);

    &:hover {
      box-shadow: 0 4px 12px rgba(255, 179, 71, 0.3);
    }
  }

  &.algorithm-btn {
    background: linear-gradient(145deg, #00ced1, #00bcd4);

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 206, 209, 0.3);
    }
  }
}

.month-selector {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  justify-content: center;

  .btn {
    padding: 8px 16px;
    border-radius: 4px;
    border: 1px solid #ddd;
    background: white;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: #f5f5f5;
      border-color: #ccc;
    }
  }

  .month-picker {
    padding: 8px 16px;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    position: relative;
    min-width: 120px;
    text-align: center;

    &:hover {
      border-color: #409eff;
    }

    .month-dropdown {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background: white;
      border: 1px solid #ddd;
      border-radius: 4px;
      margin-top: 4px;
      padding: 12px;
      z-index: 100;

      .year-selector {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        margin-bottom: 12px;

        button {
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          border: none;
          background: #f5f5f5;
          border-radius: 4px;
          cursor: pointer;
        }
      }

      .months-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 8px;

        button {
          padding: 6px;
          border: 1px solid #ddd;
          border-radius: 4px;
          background: white;
          cursor: pointer;

          &.active {
            background: #e6f7ff;
            border-color: #91d5ff;
            color: #1890ff;
          }

          &:hover {
            border-color: #409eff;
          }
        }
      }
    }
  }
}

.calendar-day.empty {
  background: transparent;
  border: 1px dashed #eee;
  cursor: default;

  &:hover {
    transform: none;
    box-shadow: none;
  }
}

.water-level {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: var(--water-height);
  transition: height 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

.circle-progress {
  width: 80px;
  height: 80px;
  position: relative;
}

svg {
  transform: rotate(-90deg);
}

circle {
  transition: stroke-dashoffset 0.8s ease;
}
</style>