<template>
  <div class="outline-editor">
    <draggable
        v-model="localItems"
        :list="visibleItems"
        item-key="id"
        group="docs"
        @start="onDragStart"
        @end="onDragEnd"
        :move="onMove"
        ghost-class="drag-ghost"
        chosen-class="drag-chosen"
        animation="200"
    >
      <template #item="{ element }">
        <transition name="list" mode="out-in">
          <div
              :key="element.id"
              class="outline-item"
              :style="{ paddingLeft: `${element.level * 1.75}rem` }"
          >
            <template v-if="typeof element.level === 'number' && element.level > 0">
              <div
                  v-for="i in element.level"
                  :key="i"
                  class="level-indicator"
                  :style="{ left: `${(i - 1) * 1.75}rem` }"
              />
            </template>
            <div
                class="item-content"
                @click="() => emit('select', element)"
                @contextmenu.prevent="openContextMenu($event, element)"
            >
              <ChevronRight
                  class="icon arrow"
                  :style="{ transform: collapsedMap[element.id] ? 'rotate(0deg)' : 'rotate(90deg)' }"
                  @click.stop="toggleCollapse(element.id)"
              />
              <FileText class="icon doc-icon" />
              <span class="title">{{ element.title }}</span>
              <button class="add-btn" @click.stop="() => addChild(element)">
                <Plus class="icon plus-icon" />
              </button>
            </div>
          </div>
        </transition>
      </template>
    </draggable>

    <!-- context menu -->
    <div
        v-if="contextMenu.visible"
        class="context-menu"
        :style="{ top: `${contextMenu.y}px`, left: `${contextMenu.x}px` }"
    >
      <ul>
        <li class="menu-item" @click="() => handleMenu('rename')">
          <i class="iconfont icon-edit" /> 重命名
        </li>
        <li class="menu-item" @click="() => handleMenu('copy')">
          <i class="iconfont icon-link" /> 复制链接
        </li>
        <li class="menu-item danger" @click="() => handleMenu('delete')">
          <i class="iconfont icon-delete" /> 删除
        </li>
      </ul>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted, onBeforeUnmount, computed } from 'vue'
import draggable from 'vuedraggable'
import { FileText, ChevronRight, Plus } from 'lucide-vue-next'
import api from '../api/index'
interface DocumentItem {
  id: string
  title: string
  level: number
  parentId: string | null
}
const collapsedMap = ref<Record<string, boolean>>({})

const visibleItems = computed(() => {
  const result: DocumentItem[] = []
  const stack: number[] = []

  for (const item of localItems.value) {
    // 检查 item 是否存在且有 id
    if (!item || !item.id) {
      console.warn('NestedOutlineEditor: 发现无效的 item', item)
      continue
    }

    // 如果上层有节点是折叠的，则跳过当前项
    if (stack.some(level => item.level > level)) {
      continue
    }

    result.push(item)

    // 如果当前项被折叠，则将其 level 压入栈中
    // 确保 id 是字符串类型
    const itemId = String(item.id)
    if (collapsedMap.value[itemId]) {
      stack.push(item.level)
    } else {
      // 如果不是折叠，清理掉比它层级大的栈记录
      while (stack.length > 0 && stack[stack.length - 1] >= item.level) {
        stack.pop()
      }
    }
  }

  return result
})

const toggleCollapse = (id: string | number) => {
  const idStr = String(id)
  collapsedMap.value[idStr] = !collapsedMap.value[idStr]
}

const closeContextMenu = () => {
  contextMenu.value.visible = false
}

onMounted(() => {
  window.addEventListener('click', closeContextMenu)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', closeContextMenu)
})

const handleMenu = (action: 'rename' | 'copy' | 'delete') => {
  const item = contextMenu.value.item
  if (!item) return

  switch (action) {
    case 'rename':
      renameItem(item)
      break
    case 'copy':
      copyLink(item)
      break
    case 'delete':
      deleteItem(item)
      break
  }

  contextMenu.value.visible = false
}

const props = defineProps<{ items: DocumentItem[]; knowledgeBaseId?: number }>()
const emit = defineEmits<{
  (e: 'update:items', value: DocumentItem[]): void
  (e: 'select', doc: DocumentItem): void
}>()

const localItems = ref<DocumentItem[]>([...props.items])

watch(
    () => props.items,
    (newVal) => {
      localItems.value = [...newVal]
    },
    { deep: true }
)

const emitItems = () => {
  emit('update:items', [...localItems.value])
}

const dragStart = ref({ x: 0 })
const currentDragItem = ref<DocumentItem | null>(null)

const onDragStart = (evt: DragEvent) => {
  dragStart.value.x = evt.clientX
  window.addEventListener('mouseup', onMouseUp, { once: true })
}

const onMove = (evt: any) => {
  currentDragItem.value = evt.draggedContext.element
  return true
}

const onDragEnd = () => {
  currentDragItem.value = null
}

const onMouseUp = (e: MouseEvent) => {
  const deltaX = e.clientX - dragStart.value.x
  const dragged = currentDragItem.value
  if (!dragged) return

  const index = localItems.value.findIndex(i => i.id === dragged.id)
  if (index === -1) return

  const levelOffset = Math.round(deltaX / 28)
  let newLevel = Math.max(0, Math.min(4, dragged.level + levelOffset))

  const prev = localItems.value[index - 1]
  if (prev) {
    newLevel = Math.min(newLevel, prev.level + 1)
  }

  dragged.level = newLevel
  dragged.parentId = null

  for (let i = index - 1; i >= 0; i--) {
    const candidate = localItems.value[i]
    if (candidate.level === newLevel - 1) {
      dragged.parentId = candidate.id
      break
    }
  }

  emitItems()
}

const addChild = async (parent: DocumentItem) => {
  if (!props.knowledgeBaseId) {
    console.warn('缺少 knowledgeBaseId，无法创建文档')
    return
  }

  const newDocPayload = {
    title: '新建子文档',
    level: Math.min(parent.level + 1, 4),
    parentId: parent.id,
    knowledgeBaseId: props.knowledgeBaseId,
    sortOrder: Date.now(), // 假设临时排序字段
  }

  try {
    const response = await api.documentApi.createDocument(newDocPayload)
    const createdDoc = response.data

    // 确保创建的数据格式正确：id 和 parentId 转换为字符串
    const formattedDoc: DocumentItem = {
      id: String(createdDoc.id || createdDoc.documentId || ''),
      title: createdDoc.title || '新建子文档',
      level: createdDoc.level || Math.min(parent.level + 1, 4),
      parentId: createdDoc.parentId ? String(createdDoc.parentId) : String(parent.id)
    }

    const index = localItems.value.findIndex((i) => String(i.id) === String(parent.id))
    if (index !== -1) {
      localItems.value.splice(index + 1, 0, formattedDoc)
    } else {
      localItems.value.push(formattedDoc)
    }
    emitItems()
  } catch (err) {
    console.error('创建文档失败:', err)
  }
}
const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  item: null as DocumentItem | null,
})

const openContextMenu = (event: MouseEvent, item: DocumentItem) => {
  contextMenu.value = {
    visible: true,
    x: event.clientX,
    y: event.clientY,
    item,
  }
}

const renameItem = (item: DocumentItem | null) => {
  if (!item) return
  const newTitle = prompt('请输入新标题', item.title)
  if (newTitle) item.title = newTitle
  contextMenu.value.visible = false
  emitItems()
}

const copyLink = (item: DocumentItem | null) => {
  if (!item) return
  const url = `${window.location.origin}/docs/${item.id}`
  navigator.clipboard.writeText(url)
  contextMenu.value.visible = false
}

const deleteItem = (item: DocumentItem | null) => {
  if (!item) return
  if (confirm(`确定要删除 "${item.title}" 吗？`)) {
    localItems.value = localItems.value.filter((i) => i.id !== item.id)
    emitItems()
  }
  contextMenu.value.visible = false
}
</script>

<style scoped>
.outline-editor {
  position: relative;
  padding: 0.5rem;
  border-radius: 12px;
  height: calc(100vh - 4rem); /* 确保高度适应布局 */
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #d9cdf9 transparent;
}

.outline-editor::-webkit-scrollbar {
  width: 6px;
}

.outline-editor::-webkit-scrollbar-thumb {
  background-color: #d9cdf9;
  border-radius: 3px;
}

.outline-item {
  position: relative;
  user-select: none;
  transition: all 0.2s ease;
  margin-bottom: 2px;
}

.level-indicator {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 1px;
  background-color: #e8e0f5;
  transition: background-color 0.2s ease;
}

.outline-item:hover .level-indicator {
  background-color: #d9cdf9;
}

.item-content {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: white;
  box-shadow: 0 1px 2px rgba(148, 108, 230, 0.05);
  border: 1px solid #f0e9ff;
}

.item-content:hover {
  background-color: #f8f5ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(148, 108, 230, 0.1);
  border-color: #e0d4f9;
}

.icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.arrow {
  color: #c0b1e0;
  transform: rotate(0deg);
}

.item-content:hover .arrow {
  color: #946ce6;
}

.doc-icon {
  color: #946ce6;
}

.title {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.add-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  opacity: 0;
  transition: all 0.2s ease;
}

.outline-item:hover .add-btn {
  opacity: 1;
}

.plus-icon {
  color: #9ca3af;
}

.add-btn:hover .plus-icon {
  color: #946ce6;
}

/* 拖拽样式 */
.drag-ghost {
  opacity: 0.8;
  background-color: #f3eeff;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(148, 108, 230, 0.15);
  border: 1px solid #d9cdf9 !important;
}

.drag-chosen {
  background-color: #f0e9ff;
  box-shadow: 0 4px 12px rgba(148, 108, 230, 0.1);
  border: 1px solid #d9cdf9;
}

.context-menu {
  position: fixed;
  z-index: 9999;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  padding: 8px 0;
  min-width: 140px;
  border: 1px solid #eee;
  animation: fadeIn 0.2s ease-out;
}

.context-menu ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 10px 8px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background-color: #f4f3fe;
  color: #946ce6;
  border-left: 3px solid #946ce6;
}

.menu-item i {
  margin-right: 10px;
  font-size: 16px;
  color: #946ce6;
}

.menu-item.danger:hover {
  color: #f44336;
  background-color: #fff1f0;
  border-left: 3px solid #f44336;
}

.menu-item.danger i {
  color: #f44336;
}

/* 过渡动画 */
.list-enter-active,
.list-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.list-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.list-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>