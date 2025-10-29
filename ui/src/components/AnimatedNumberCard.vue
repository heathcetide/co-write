<!-- AnimatedNumberCard.vue -->
<template>
  <div class="stat-card">
    <div class="stat-value">{{ formattedValue }}</div>
    <div class="stat-label">{{ label }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue';

const props = defineProps({
  target: {
    type: Number,
    required: true,
  },
  label: {
    type: String,
    required: true
  },
  format: {
    type: String,
    default: 'number',
  },
  duration: {
    type: Number,
    default: 1500
  }
});

const currentValue = ref(0);

const formattedValue = computed(()=>{
  if (props.format === 'kilo') {
    return (currentValue.value/1000).toFixed(1)+'K';
  }
  if (props.format === 'percent') {
    return currentValue.value.toFixed(1)+'%';
  }
  return Math.floor(currentValue.value).toLocaleString();
});

onMounted(()=>{
  nextTick(()=>{
    animateNumber();
  });
});

const animateNumber = ()=>{
  const startValue = 0;
  const endValue = props.target;
  const duration = props.duration;
  const startTime = performance.now();

  const updateNumber = (timestamp: number)=>{
    const elapsed = timestamp-startTime;
    const progress = Math.min(elapsed/duration, 1);

    currentValue.value = startValue + endValue*progress;
    if (progress <1) {
      requestAnimationFrame(updateNumber);
    }
  };
  requestAnimationFrame(updateNumber);
};
</script>

<style>
.stat-card {
  background-color: white;
  border-radius: 12px;
  padding: 24px;
  flex: 1;
  min-width: 200px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid white;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: black;
  margin-bottom: 4px;
  transition: all 0.2s ease;
}

.stat-label {
  font-size: 14px;
  color: black;
}
</style>