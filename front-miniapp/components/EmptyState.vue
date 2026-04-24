<template>
  <view class="empty-wrap">
    <text class="empty-icon">{{ icon }}</text>
    <text class="empty-text">{{ text }}</text>
    <button v-if="btnText" class="btn btn-secondary" style="margin-top: 24rpx; font-size: 26rpx; height: 72rpx;" @tap="$emit('action')">
      {{ btnText }}
    </button>
  </view>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '📋' },
  text: { type: String, default: '暂无数据' },
  btnText: { type: String, default: '' }
})
defineEmits(['action'])
</script>

<style lang="scss" scoped>
.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 96rpx 32rpx;

  .empty-icon {
    font-size: 96rpx;
    margin-bottom: 24rpx;
    opacity: 0.4;
  }

  .empty-text {
    font-size: 28rpx;
    color: #9ca3af;
  }
}
</style>
