export function callPhone(phoneNumber) {
  if (!phoneNumber) {
    uni.showToast({ title: '暂无手机号', icon: 'none' })
    return
  }

  // H5 调试环境无法稳定处理 tel: 协议，复制号码比抛浏览器错误更友好。
  // #ifdef H5
  uni.setClipboardData({
    data: String(phoneNumber),
    success() {
      uni.showToast({ title: '手机号已复制', icon: 'success' })
    }
  })
  return
  // #endif

  uni.makePhoneCall({
    phoneNumber: String(phoneNumber),
    fail(error) {
      console.warn('[phone] make phone call failed', error)
      uni.showToast({ title: '拨号失败，请稍后重试', icon: 'none' })
    }
  })
}
