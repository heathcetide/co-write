<template>
  <div class="container">
    <iframe src="/drawio_demo.html" class="drawio-frame"></iframe>
  </div>
</template>

<script setup>
// 无逻辑，留空即可
</script>

<style scoped>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

.container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh; /* 满屏高度 */
  overflow: hidden;
  margin: 0;
  padding: 0;
  background-color: #f0f0f0; /* 可选：设置背景色 */
}

.drawio-frame {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
