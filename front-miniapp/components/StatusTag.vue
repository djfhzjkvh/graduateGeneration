<template>
  <text class="tag" :style="{ color: cfg.color, background: cfg.bg }">{{ cfg.label }}</text>
</template>

<script setup>
import { computed } from 'vue'
import { CUSTOMER_STATUS, INTENT_LEVEL, TASK_STATUS, TASK_PRIORITY } from '../constants/dictionary'

const props = defineProps({
  type: { type: String, required: true }, // customer-status | intent | task-status | priority
  value: { type: String, required: true }
})

const MAP = {
  'customer-status': CUSTOMER_STATUS,
  'intent': INTENT_LEVEL,
  'task-status': TASK_STATUS,
  'priority': TASK_PRIORITY
}

const cfg = computed(() => {
  const dict = MAP[props.type]
  return dict?.[props.value] || { label: props.value, color: '#6b7280', bg: '#f3f4f6' }
})
</script>

<style lang="scss" scoped>
.tag {
  display: inline-block;
  font-size: 20rpx;
  font-weight: 600;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  line-height: 1.6;
}
</style>
