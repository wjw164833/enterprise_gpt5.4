/**
 * 微信JS-SDK工具
 */

interface WxConfig {
  appId: string
  timestamp: string
  nonceStr: string
  signature: string
}

/** 初始化微信JS-SDK */
export async function initWxJsSdk(config: WxConfig, jsApiList: string[] = []): Promise<void> {
  if (typeof (window as any).wx === 'undefined') {
    console.warn('WeChat JS-SDK not loaded')
    return
  }

  const wx = (window as any).wx
  wx.config({
    debug: false,
    appId: config.appId,
    timestamp: config.timestamp,
    nonceStr: config.nonceStr,
    signature: config.signature,
    jsApiList: [
      'updateAppMessageShareData',
      'updateTimelineShareData',
      'onMenuShareWeibo',
      'chooseImage',
      'previewImage',
      'uploadImage',
      'getLocation',
      'openLocation',
      'scanQRCode',
      ...jsApiList
    ]
  })

  return new Promise((resolve, reject) => {
    wx.ready(() => resolve())
    wx.error((res: any) => reject(res))
  })
}

/** 设置微信分享 */
export function setWxShare(params: {
  title: string
  desc: string
  link: string
  imgUrl: string
}): void {
  const wx = (window as any).wx
  if (!wx) return

  wx.ready(() => {
    wx.updateAppMessageShareData({
      title: params.title,
      desc: params.desc,
      link: params.link,
      imgUrl: params.imgUrl
    })
    wx.updateTimelineShareData({
      title: params.title,
      link: params.link,
      imgUrl: params.imgUrl
    })
  })
}

/** 判断是否在微信浏览器中 */
export function isWechatBrowser(): boolean {
  const ua = navigator.userAgent.toLowerCase()
  return ua.indexOf('micromessenger') !== -1
}

/** 微信授权跳转 */
export function wxOAuthRedirect(appId: string, redirectUri: string, state: string = 'STATE'): void {
  const encoded = encodeURIComponent(redirectUri)
  const url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${encoded}&response_type=code&scope=snsapi_userinfo&state=${state}#wechat_redirect`
  window.location.href = url
}
