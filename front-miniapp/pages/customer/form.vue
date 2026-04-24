<template>
  <view class="page-container" style="padding-bottom: 160rpx;">
    <view class="form-group">
      <text class="form-group-title">基础信息</text>
      <view class="form-item">
        <text class="form-label required">客户姓名</text>
        <input class="form-input" v-model="form.name" placeholder="请输入" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input class="form-input" v-model="form.phone" type="number" maxlength="11" placeholder="请输入11位手机号" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item" @tap="showPicker('gender')">
        <text class="form-label">性别</text>
        <text class="form-value">{{ GENDER[form.gender] || '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item">
        <text class="form-label">年龄</text>
        <input class="form-input" v-model="form.age" type="number" placeholder="请输入" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item" @tap="showPicker('source')">
        <text class="form-label">来源</text>
        <text class="form-value">{{ CUSTOMER_SOURCE[form.source] || '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item" @tap="showPicker('status')">
        <text class="form-label">客户状态</text>
        <text class="form-value">{{ CUSTOMER_STATUS[form.status] ? CUSTOMER_STATUS[form.status].label : '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">购房意向</text>
      <view class="form-item" @tap="showPicker('intentLevel')">
        <text class="form-label">意向等级</text>
        <text class="form-value">{{ INTENT_LEVEL[form.intentLevel] ? INTENT_LEVEL[form.intentLevel].label : '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
      <view class="form-item">
        <text class="form-label">预算下限</text>
        <input class="form-input" v-model="form.budgetMin" type="number" placeholder="万元" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item">
        <text class="form-label">预算上限</text>
        <input class="form-input" v-model="form.budgetMax" type="number" placeholder="万元" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item">
        <text class="form-label">关注区域</text>
        <input class="form-input" v-model="form.focusArea" placeholder="请输入" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item">
        <text class="form-label">户型需求</text>
        <input class="form-input" v-model="form.houseType" placeholder="如：三室两厅" placeholder-style="color:#9ca3af" />
      </view>
      <view class="form-item" @tap="showPicker('purpose')">
        <text class="form-label">购房目的</text>
        <text class="form-value">{{ PURCHASE_PURPOSE[form.purpose] || '请选择' }}</text>
        <text class="form-arrow">›</text>
      </view>
    </view>

    <view class="form-group">
      <text class="form-group-title">跟进备注</text>
      <view class="form-item" style="align-items: flex-start; padding-top: 24rpx;">
        <textarea
          class="form-textarea"
          v-model="form.remark"
          placeholder="添加备注信息..."
          placeholder-style="color:#9ca3af"
          maxlength="500"
          auto-height
        />
      </view>
    </view>

    <view style="height: 32rpx;" />

    <!-- 底部操作 -->
    <view class="bottom-bar">
      <button v-if="isEdit" class="btn btn-danger" style="flex:1" @tap="deleteCustomer">删除</button>
      <button class="btn btn-primary" style="flex:2" :disabled="saving" @tap="save">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { GENDER, CUSTOMER_SOURCE, CUSTOMER_STATUS, INTENT_LEVEL, PURCHASE_PURPOSE } from '../../constants/dictionary'

const form = ref({
  name: '', phone: '', gender: '', age: '', source: '', status: 'NEW',
  intentLevel: 'MEDIUM', budgetMin: '', budgetMax: '',
  focusArea: '', houseType: '', purpose: '', remark: ''
})
const saving = ref(false)
const isEdit = ref(false)

onLoad((options) => {
  if (options?.id) {
    isEdit.value = true
    uni.setNavigationBarTitle({ title: '编辑客户' })
    // 原型：填充 mock 数据
    Object.assign(form.value, { name: '张先生', phone: '13812345678', gender: 'MALE', age: 35, source: 'WECHAT', status: 'FOLLOWING', intentLevel: 'HIGH', budgetMin: 150, budgetMax: 200, focusArea: '天府新区', houseType: '三室两厅', purpose: 'SELF_USE', remark: '急需在6月前购房' })
  }
})

const PICKER_OPTIONS = {
  gender: { label: '性别', keys: Object.keys(GENDER), labels: Object.values(GENDER) },
  source: { label: '来源', keys: Object.keys(CUSTOMER_SOURCE), labels: Object.values(CUSTOMER_SOURCE) },
  status: { label: '客户状态', keys: Object.keys(CUSTOMER_STATUS), labels: Object.values(CUSTOMER_STATUS).map(v => v.label) },
  intentLevel: { label: '意向等级', keys: Object.keys(INTENT_LEVEL), labels: Object.values(INTENT_LEVEL).map(v => v.label) },
  purpose: { label: '购房目的', keys: Object.keys(PURCHASE_PURPOSE), labels: Object.values(PURCHASE_PURPOSE) }
}

function showPicker(field) {
  const cfg = PICKER_OPTIONS[field]
  uni.showActionSheet({
    itemList: cfg.labels,
    success(res) {
      form.value[field] = cfg.keys[res.tapIndex]
    }
  })
}

async function save() {
  if (!form.value.name.trim()) {
    uni.showToast({ title: '请输入客户姓名', icon: 'none' })
    return
  }
  saving.value = true
  await new Promise(r => setTimeout(r, 600))
  saving.value = false
  uni.showToast({ title: isEdit.value ? '保存成功' : '添加成功', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 1200)
}

function deleteCustomer() {
  uni.showModal({
    title: '删除客户',
    content: '确认删除该客户？此操作不可恢复。',
    confirmColor: '#c81e1e',
    success(res) {
      if (res.confirm) {
        uni.showToast({ title: '已删除', icon: 'success' })
        setTimeout(() => uni.navigateBack({ delta: 2 }), 1200)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.form-group {
  margin: 24rpx 24rpx 0;
}
</style>
