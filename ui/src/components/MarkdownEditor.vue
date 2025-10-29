<style scoped lang="scss">
//特殊样式，而不通用请在这里写样式
.cherry-container {
  position: absolute;
  top: 0px;
  right: 0px;
  bottom: 0px;
}

.toc-container {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 260px;
  background-color: #fff;

  .toc-header {
    height: 48px;
    line-height: 48px;
    background-color: #20304b;
    text-align: center;
    font-size: 16px;
    color: #fff;
  }

  .toc-list {
    position: absolute;
    top: 60px;
    right: 0px;
    bottom: 0px;
    left: 0px;
    overflow: auto;

    &::-webkit-scrollbar
    {
      width:5px;
      height: 5px;
    }
    &::-webkit-scrollbar-thumb {
      background-color: #607d8b;
      border-radius: 5px;
    }
    &::-webkit-scrollbar-thumb:hover {
      background: #40a0ff;
    }

    .toc-list-container {
      padding-top: 6px;
      cursor: pointer;
    }

    .toc-list-container:hover {
      color: red;
    }
  }
}
.top-contrl {
  position: absolute;
  top: 10px;
  right: 8px;
  heigth: 30px;
  padding-right:5px;
  color: red;
  font-weight: bold;
  font-size: 20px;
  z-index: 2;
}
</style>
<template>
  <div @click.prevent.stop style="position: relative; width: 100%; height: 100%; z-index: 99;">

    <!-- ------- markdown组件容器 --------- -->
    <div :ref="mdId" :id="mdId" class="cherry-container scroll"
         :style="{'left': toc_visiable ? '262px': '0px'}"></div>

    <!-- ------- 显示目录 --------- -->
    <div class="toc-container" v-show="toc_visiable">
      <div class="toc-header">目录</div>
      <div class="toc-list">
        <div v-for="(link,index) in tocList" :key="link.id" v-html="link.text" class="toc-list-container"
             :style="{'padding-left': link.level * 20 + 'px'}" @click="jump(index)">
        </div>
      </div>
    </div>
    <div class="top-contrl">
      <i class="el-icon-view" v-if="!toc_visiable" @click="showToc(true)" title="显示目录"></i>
      <i class="el-icon-view" v-else @click="showToc(false)" title="隐藏目录"></i>
    </div>
  </div>
</template>

<script>
import Cherry from 'cherry-markdown'
import 'cherry-markdown/dist/cherry-markdown.min.css'
import { nextTick } from 'vue'
import api from '../api/index.js'

export default {
  name: "sy-cherry-markdown",
  props: {
    // markdown文本
    modelValue: { type: String, default: '' },
    // markdown组件容器ID
    mdId: {
      type: String,
      default: 'markdown-container'
    },
    // 用来控制目录显示或隐藏
    tocVisiable:{
      type: Boolean,
      default: false
    },
    // 编辑器的显示模式 view|edit
    display: {
      type: String,
      default: 'view'
    }
  },
  watch: {
    modelValue(newVal) {
      if (!this.cherrInstance) return;

      // 只在父组件给来的值 和 Cherry 里当前的 markdown 不一致时才更新
      const current = this.cherrInstance.getMarkdown();
      if (newVal !== current) {
        this.cherrInstance.setMarkdown(newVal, false);
      }
    }
  },
  data() {
    return {
      // 编辑器的显示模式
      displayModal: this.display,

      // 用来控制目录显示或隐藏
      toc_visiable: this.tocVisiable,

      // 目录列表数组
      tocList: [],

      // markdown编辑器对象
      cherrInstance: null,

      curTab: 0,
    };
  },
  mounted() {
    // 初始化编辑器
    this.initCherryMD(this.modelValue);
    this.showToc(this.toc_visiable)
    this.setDisplay(this.display)
  },
  beforeDestroy() {
    while (this.$refs[this.mdId].firstChild) {
      this.$refs[this.mdId].removeChild(this.$refs[this.mdId].firstChild);
    }
    this.cherrInstance = null
  },
  methods: {

    // 显示目录栏
    showToc(val) {
      if (val){
        this.getTitles();
      }
      this.toc_visiable = val;
    },

    //****************************************

    /**
     * 设置markdown编辑器是浏览还是编辑状态
     * @param {Object} val {edit, view}
     */
    setDisplay(val) {
      this.displayModal = val;
      while (this.$refs[this.mdId]?.firstChild) {
        this.$refs[this.mdId].removeChild(this.$refs[this.mdId].firstChild);
      }
      this.cherrInstance = null;
      this.initCherryMD();

      nextTick(() => {
        if (val === 'edit') {
          this.switchModel('editOnly');
        } else if (val === 'all') {
          this.switchModel('edit&preview');
        } else {
          this.switchModel('previewOnly');
        }
      });
    },

    /**
     * 获取文档内容中的标题目录
     */
    getTitles() {
      if (!this.cherrInstance || typeof this.cherrInstance.getToc !== 'function') {
        this.tocList = []
        return
      }
      const toc = this.cherrInstance.getToc()
      this.tocList = Array.isArray(toc) ? toc : []
    },

    //在某一块区域内滑动不是整个页面滑动：
    jump(index) {
      this.curTab = index;
      let anchorName = this.tocList[index].id;
      let anchorElem = document.getElementById(anchorName);
      let firstElem = document.getElementById(this.tocList[0].id);

      this.totalY = anchorElem.offsetTop - firstElem.offsetTop;
      this.distance = document.querySelector('.cherry-previewer').scrollTop;
      this.step = this.totalY / 50;
      if (this.totalY > this.distance) {
        this.smoothDown(document.querySelector('.cherry-previewer'));
      } else {
        let newTotal = this.distance - this.totalY;
        this.step = newTotal / 50;
        this.smoothUp(document.querySelector('.cherry-previewer'));
      }
    },

    // 参数element为滚动区域
    smoothDown(element) {
      if (this.distance < this.totalY) {
        this.distance += this.step;
        element.scrollTop = this.distance;
        setTimeout(this.smoothDown.bind(this, element), 2);
      } else {
        element.scrollTop = this.totalY;
      }
    },
    // 参数element为滚动区域
    smoothUp(element) {
      if (this.distance > this.totalY) {
        this.distance -= this.step;
        element.scrollTop = this.distance;
        setTimeout(this.smoothUp.bind(this, element), 2);
      } else {
        element.scrollTop = this.totalY;
      }
    },

    onScroll(e) {
      this.getTitles();
      if (!this.tocList || this.tocList.length < 1) return;

      let firstElem = document.getElementById(this.tocList[0].id);

      for (let i = this.tocList.length - 1; i >= 0; i--) {

        let anchorElem = document.getElementById(this.tocList[i].id);

        // 判断滚动条滚动距离是否大于当前滚动项可滚动距离
        let judge = e.target.scrollTop >= anchorElem.offsetTop - firstElem.offsetTop;

        if (judge) {
          this.curTab = i;
          break;
        }
      }
    },

    // 初始化编辑器
    initCherryMD(value = this.modelValue) {
      const {
        afterChange,
        afterInit,
        beforeImageMounted,
        fileUpload,
        mdId
      } = this;
      const defaultValue = value;
      this.cherrInstance = new Cherry({
        id: mdId,
        value: defaultValue,
        fileUpload: fileUpload,
        callback: {
          afterChange: afterChange,
          afterInit: afterInit,
          beforeImageMounted: beforeImageMounted
        },
        toolbars: {
          showToolbar: this.displayModal === 'edit',
          sidebar: ['theme', 'mobilePreview', 'export']
        },
        customImageStyle: 'max-width:100%;height:auto;border-radius:6px;',
      })
    },

    /**
     * 编辑器中上传文件操作处理方法（如上传附件、粘贴截图等）
     * @param {Object} file
     */
    async fileUpload(file) {
      const response = await api.commonApi.uploadFile(file)
      if (response.code === 200) {
        let imgMdStr = `![${response.data.filename}](${response.data.refUrl})`;
        this.cherrInstance.insert(imgMdStr)
      }
    },

    /**
     * 编辑器内容变更事件回调
     * @param {Object} e
     */
    afterChange(e) {
      this.content = e
      this.getTitles()
      const mdHtml = this.getCherryHtml()
      const mdTxt = e
      const mdContent = this.getCherryContent()
      this.$emit('input', mdContent)
      //console.log('input:', mdHtml)
      this.$emit('md-change', mdHtml, mdTxt, mdContent)
      //console.log('md-change:', mdHtml, mdTxt, mdContent)
      this.$emit('update:modelValue', e)
      // console.log('update:modelValue', e)
    },

    // 初始化事件回调
    afterInit(e) {
      //console.log(e)
    },

    // 图片加载回调
    beforeImageMounted(e, src) {
      //console.log('bfImageMt', e, src)
      return {
        [e]: src
      }
    },

    /**
     * 设置markdown编辑器内容，全部覆盖
     * @param {Object} content 要设置的内容
     * @param {Object} keepCursor 自动设置焦点到内容
     */
    setMarkdown(content, keepCursor) {
      if (!this.cherrInstance) { // 未加载则重新初始化
        this.initCherryMD(content)
        return
      }
      this.cherrInstance.setMarkdown(content, keepCursor)
    },

    getCherryContent() {
      const result = this.cherrInstance.getMarkdown() // 获取markdown内容
      return result
    },
    getCherryHtml() {
      const result = this.cherrInstance.getHtml()
      return result
    },
    getData() {
      const result = this.cherrInstance.getHtml()
      return result
    },

    /**
     * type：{'pdf'|'img'}
     */
    exportMD(type = 'pdf') {
      this.cherrInstance.export(type)
    },

    /**
     * model{'edit&preview'|'editOnly'|'previewOnly'}
     */
    switchModel(model) {
      if (this.isInit()) {
        this.cherrInstance.switchModel(model)
      }
    },

    insert(content, isSelect = false, anchor = [], focus = true) {
      this.cherrInstance.insert(content, isSelect, anchor, focus)
    },

    isInit() {
      if (this.cherrInstance) {
        return true
      }
      this.$message.warning('编辑器未初始化，请检查')
      return false
    }
  },
};
</script>