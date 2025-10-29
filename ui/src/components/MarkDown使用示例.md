<template>
  <MdEditor v-model="text" theme="dark"/>
  <MdPreview :editorId="id" :modelValue="text" />
  <MdCatalog :editorId="id" :scrollElement="scrollElement" />
</template>
<script setup>
import { ref } from 'vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
const text = ref('# Hello md-editor-v3\n> 支持 🎉 **公式**、🖼 **图片上传**、📊 **Mermaid**')
</script>