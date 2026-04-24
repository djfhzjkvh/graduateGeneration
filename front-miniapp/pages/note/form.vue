<template>
  <view class="page-container" style="padding-bottom: 150rpx;">
    <view class="form-group" style="margin-top: 24rpx;">
      <text class="form-group-title">复盘笔记</text>
      <view class="form-item">
        <text class="form-label required">标题</text>
        <input class="form-input" v-model="form.title" placeholder="请输入标题" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item textarea-item">
        <text class="form-label required">摘要</text>
        <textarea class="form-textarea" v-model="form.summary" placeholder="记录客户核心诉求、沟通进展" placeholder-style="color:#9ca3af" maxlength="500" auto-height />
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">下次沟通准备</text>
      <view class="form-item textarea-item">
        <text class="form-label">主要异议</text>
        <textarea class="form-textarea" v-model="form.objectionTop3" placeholder="例如：总价偏高；距离地铁远；交付时间晚" placeholder-style="color:#9ca3af" maxlength="300" auto-height />
      </view>
      <view class="form-item textarea-item">
        <text class="form-label">下次主题</text>
        <textarea class="form-textarea" v-model="form.nextTopic" placeholder="下次跟进建议切入点" placeholder-style="color:#9ca3af" maxlength="300" auto-height />
      </view>
    </view>

    <view class="delete-wrap" v-if="noteId">
      <button class="delete-btn" @tap="removeNote">删除笔记</button>
    </view>

    <view class="bottom-bar">
      <button class="btn btn-primary btn-block" :disabled="saving" @tap="save">
        {{ saving ? '保存中...' : '保存笔记' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { noteApi } from '../../api/note'
import { useUserStore } from '../../stores/user'

const { userInfo, loadUser } = useUserStore()
const customerId = ref(null)
const noteId = ref(null)
const saving = ref(false)
const form = ref({
  title: '',
  summary: '',
  objectionTop3: '',
  nextTopic: ''
})

onLoad(async (options) => {
  customerId.value = options?.customerId || null
  noteId.value = options?.id || null
  loadUser()
  if (noteId.value) {
    const data = await noteApi.detail(noteId.value)
    Object.assign(form.value, {
      title: data?.title || '',
      summary: data?.summary || '',
      objectionTop3: data?.objectionTop3 || '',
      nextTopic: data?.nextTopic || ''
    })
  }
})

async function save() {
  if (!form.value.title.trim()) {
    uni.showToast({ title: '请填写标题', icon: 'none' })
    return
  }
  if (!form.value.summary.trim()) {
    uni.showToast({ title: '请填写摘要', icon: 'none' })
    return
  }
  saving.value = true
  try {
    if (noteId.value) {
      await noteApi.update(noteId.value, form.value)
    } else {
      await noteApi.create({
        ...form.value,
        customerId: Number(customerId.value),
        noteType: 'MANUAL',
        createdBy: userInfo.value?.id
      })
    }
    uni.showToast({ title: '已保存', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 800)
  } finally {
    saving.value = false
  }
}

function removeNote() {
  uni.showModal({
    title: '删除笔记',
    content: '删除后不可恢复，确定删除吗？',
    confirmText: '删除',
    confirmColor: '#c81e1e',
    success: async (res) => {
      if (!res.confirm) return
      await noteApi.remove(noteId.value)
      uni.showToast({ title: '已删除', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 800)
    }
  })
}
</script>

<style lang="scss" scoped>
.textarea-item {
  align-items: flex-start;
  padding-top: 20rpx;
}

.delete-wrap {
  padding: 0 24rpx;
}

.delete-btn {
  height: 80rpx;
  line-height: 80rpx;
  background: #ffffff;
  color: #c81e1e;
  border: 1rpx solid #fca5a5;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.delete-btn::after { border: none; }
</style>
