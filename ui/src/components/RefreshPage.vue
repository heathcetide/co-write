<!-- components/RefreshPage.vue -->
<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { onMounted } from 'vue'

const router = useRouter()
const route = useRoute()

onMounted(() => {
  router.replace({ path: '/documents', query: route.query })
})
</script>

<template>
  <div></div>
</template>
