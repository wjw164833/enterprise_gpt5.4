/**
 * 环境配置
 * 根据不同环境切换 baseUrl
 * P1-10: 添加环境判断逻辑，根据构建配置自动选择环境
 */
const env = (() => {
  // 优先使用构建时注入的环境变量
  if (typeof __wxConfig !== 'undefined') {
    // 微信开发者工具中可通过 envVersion 判断
    // develop: 开发版, trial: 体验版, release: 正式版
    const envVersion = __wxConfig.envVersion || 'develop'
    if (envVersion === 'release') return 'prod'
    if (envVersion === 'trial') return 'test'
    return 'dev'
  }
  // 回退：可通过全局变量覆盖
  if (typeof globalThis.__APP_ENV__ !== 'undefined') {
    return globalThis.__APP_ENV__
  }
  // 默认开发环境（构建脚本会替换此值）
  return 'dev'
})()

const config = {
  dev: {
    baseUrl: 'https://dev-api.invitation.com/api/v1',
    appId: 'wx1234567890abcdef',
    uploadUrl: 'https://dev-api.invitation.com/api/v1/upload/image',
    wsUrl: 'wss://dev-api.invitation.com/ws'
  },
  test: {
    baseUrl: 'https://test-api.invitation.com/api/v1',
    appId: 'wx1234567890abcdef',
    uploadUrl: 'https://test-api.invitation.com/api/v1/upload/image',
    wsUrl: 'wss://test-api.invitation.com/ws'
  },
  prod: {
    baseUrl: 'https://api.invitation.com/api/v1',
    appId: 'wx1234567890abcdef',
    uploadUrl: 'https://api.invitation.com/api/v1/upload/image',
    wsUrl: 'wss://api.invitation.com/ws'
  }
}

module.exports = config[env]
