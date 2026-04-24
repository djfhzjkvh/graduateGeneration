<script>
import { authApi } from './api/auth'
import { useUserStore } from './stores/user'
import { mapUser } from './utils/adapters'

export default {
  async onLaunch() {
    const token = uni.getStorageSync('token')
    if (token) {
      const { setUser, logout } = useUserStore()
      try {
        const data = await authApi.profile()
        const profile = data?.userInfo || data
        setUser(mapUser(profile || {}))
        console.info('[app] profile initialized')
      } catch (error) {
        console.warn('[app] profile initialize failed', error)
        logout()
        uni.reLaunch({ url: '/pages/login/login' })
        return
      }
      uni.switchTab({ url: '/pages/home/index' })
    } else {
      uni.reLaunch({ url: '/pages/login/login' })
    }
  },
  onShow() {},
  onHide() {}
}
</script>

<style lang="scss">
@import './uni.scss';

page {
  background-color: #f3f4f6;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', sans-serif;
  font-size: 28rpx;
  color: #111827;
  line-height: 1.5;
}

.page-container {
  min-height: 100vh;
  background-color: #f3f4f6;
  padding-bottom: 32rpx;
}

/* 通用卡片 */
.card {
  background: #ffffff;
  border: 1rpx solid #e5e7eb;
  border-radius: 12rpx;
  padding: 32rpx;
}

/* 通用按钮 */
.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8rpx;
  font-size: 28rpx;
  font-weight: 600;
  padding: 0 32rpx;
  height: 80rpx;
  border: none;

  &::after {
    border: none;
  }

  &-primary {
    background: #1a56db;
    color: #ffffff;
  }

  &-secondary {
    background: #ffffff;
    color: #374151;
    border: 1rpx solid #d1d5db;
  }

  &-danger {
    background: #ffffff;
    color: #c81e1e;
    border: 1rpx solid #fca5a5;
  }

  &-block {
    width: 100%;
  }

  &[disabled] {
    opacity: 0.5;
  }
}

/* 表单 */
.form-group {
  background: #ffffff;
  border-radius: 12rpx;
  border: 1rpx solid #e5e7eb;
  margin-bottom: 24rpx;
  overflow: hidden;

  .form-group-title {
    font-size: 24rpx;
    font-weight: 600;
    color: #6b7280;
    padding: 24rpx 32rpx 16rpx;
    border-bottom: 1rpx solid #f3f4f6;
    background: #f9fafb;
  }

  .form-item {
    display: flex;
    align-items: center;
    padding: 28rpx 32rpx;
    border-bottom: 1rpx solid #f3f4f6;

    &:last-child {
      border-bottom: none;
    }

    .form-label {
      width: 160rpx;
      font-size: 28rpx;
      color: #374151;
      flex-shrink: 0;

      &.required::after {
        content: ' *';
        color: #c81e1e;
      }
    }

    .form-input {
      flex: 1;
      font-size: 28rpx;
      color: #111827;
      text-align: right;
    }

    .form-textarea {
      flex: 1;
      font-size: 28rpx;
      color: #111827;
      min-height: 120rpx;
    }

    .form-value {
      flex: 1;
      font-size: 28rpx;
      color: #111827;
      text-align: right;
    }

    .form-arrow {
      font-size: 24rpx;
      color: #9ca3af;
      margin-left: 8rpx;
    }
  }
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-top: 1rpx solid #e5e7eb;
  padding: 16rpx 32rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  display: flex;
  gap: 16rpx;
  z-index: 100;
}

/* 分割线 */
.divider {
  height: 1rpx;
  background: #e5e7eb;
  margin: 0 32rpx;
}

/* 列表为空 */
.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 96rpx 32rpx;

  .empty-icon {
    font-size: 96rpx;
    margin-bottom: 24rpx;
    opacity: 0.3;
  }

  .empty-text {
    font-size: 28rpx;
    color: #9ca3af;
  }
}
</style>
