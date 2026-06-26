import { ref, onUnmounted } from 'vue'
import { reportEvent } from '@/api/analytics'

export function useAnalytics() {
  const tracking = ref(false)

  async function trackEvent(eventName: string, eventData?: Record<string, any>, invitationId?: number) {
    try {
      await reportEvent({
        eventName,
        eventData: eventData ? JSON.stringify(eventData) : undefined,
        invitationId,
        platform: 'h5'
      })
    } catch {
      // Analytics should not block user actions
    }
  }

  function trackPageView(invitationId?: number) {
    return trackEvent('page_view', {
      url: window.location.href,
      referrer: document.referrer,
      title: document.title
    }, invitationId)
  }

  function trackClick(target: string, invitationId?: number) {
    return trackEvent('click', { target }, invitationId)
  }

  function trackShare(method: string, invitationId?: number) {
    return trackEvent('share', { method }, invitationId)
  }

  let visibilityHandler: (() => void) | null = null

  function startAutoTracking(invitationId?: number) {
    tracking.value = true
    trackPageView(invitationId)

    visibilityHandler = () => {
      if (document.visibilityState === 'visible') {
        trackEvent('resume', {}, invitationId)
      }
    }
    document.addEventListener('visibilitychange', visibilityHandler)
  }

  function stopAutoTracking() {
    tracking.value = false
    if (visibilityHandler) {
      document.removeEventListener('visibilitychange', visibilityHandler)
      visibilityHandler = null
    }
  }

  onUnmounted(() => {
    stopAutoTracking()
  })

  return {
    tracking,
    trackEvent,
    trackPageView,
    trackClick,
    trackShare,
    startAutoTracking,
    stopAutoTracking
  }
}
