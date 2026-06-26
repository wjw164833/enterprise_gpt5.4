import { ref } from 'vue'

interface PosterOptions {
  width: number
  height: number
  backgroundColor: string
  coverImage: string
  title: string
  date: string
  address: string
  qrcodeUrl?: string
}

export function usePoster() {
  const generating = ref(false)
  const posterUrl = ref('')

  async function generatePoster(options: PosterOptions): Promise<string> {
    generating.value = true
    try {
      const canvas = document.createElement('canvas')
      const dpr = window.devicePixelRatio || 2
      canvas.width = options.width * dpr
      canvas.height = options.height * dpr
      const ctx = canvas.getContext('2d')!
      ctx.scale(dpr, dpr)

      // Background
      ctx.fillStyle = options.backgroundColor
      ctx.fillRect(0, 0, options.width, options.height)

      // Cover image
      if (options.coverImage) {
        try {
          const img = await loadImage(options.coverImage)
          const imgRatio = img.width / img.height
          const canvasRatio = options.width / (options.height * 0.6)
          let sx = 0, sy = 0, sw = img.width, sh = img.height
          if (imgRatio > canvasRatio) {
            sw = img.height * canvasRatio
            sx = (img.width - sw) / 2
          } else {
            sh = img.width / canvasRatio
            sy = (img.height - sh) / 2
          }
          ctx.drawImage(img, sx, sy, sw, sh, 0, 0, options.width, options.height * 0.6)
        } catch {
          // Draw placeholder
          ctx.fillStyle = '#e0e0e0'
          ctx.fillRect(0, 0, options.width, options.height * 0.6)
        }
      }

      // Gradient overlay
      const gradient = ctx.createLinearGradient(0, options.height * 0.4, 0, options.height * 0.6)
      gradient.addColorStop(0, 'rgba(0,0,0,0)')
      gradient.addColorStop(1, 'rgba(0,0,0,0.7)')
      ctx.fillStyle = gradient
      ctx.fillRect(0, options.height * 0.4, options.width, options.height * 0.2)

      // Title
      ctx.fillStyle = '#ffffff'
      ctx.font = 'bold 24px "Noto Serif SC", serif'
      ctx.textAlign = 'center'
      ctx.fillText(options.title, options.width / 2, options.height * 0.72)

      // Date
      ctx.font = '16px "Noto Sans SC", sans-serif'
      ctx.fillStyle = 'rgba(255,255,255,0.9)'
      ctx.fillText(options.date, options.width / 2, options.height * 0.78)

      // Address
      ctx.font = '14px "Noto Sans SC", sans-serif'
      ctx.fillStyle = 'rgba(255,255,255,0.7)'
      ctx.fillText(options.address, options.width / 2, options.height * 0.83)

      // QR code
      if (options.qrcodeUrl) {
        try {
          const qrImg = await loadImage(options.qrcodeUrl)
          const qrSize = 80
          ctx.drawImage(qrImg, (options.width - qrSize) / 2, options.height * 0.87, qrSize, qrSize)
        } catch {
          // Skip QR
        }
      }

      posterUrl.value = canvas.toDataURL('image/png')
      return posterUrl.value
    } finally {
      generating.value = false
    }
  }

  function loadImage(src: string): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.crossOrigin = 'anonymous'
      img.onload = () => resolve(img)
      img.onerror = reject
      img.src = src
    })
  }

  async function downloadPoster(filename: string = 'invitation-poster.png') {
    if (!posterUrl.value) return
    const link = document.createElement('a')
    link.href = posterUrl.value
    link.download = filename
    link.click()
  }

  return {
    generating,
    posterUrl,
    generatePoster,
    downloadPoster
  }
}
