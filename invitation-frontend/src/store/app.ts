import { defineStore } from 'pinia'

interface AppState {
  sidebarCollapsed: boolean
  darkMode: boolean
  loading: boolean
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    sidebarCollapsed: false,
    darkMode: false,
    loading: false
  }),

  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    toggleDarkMode() {
      this.darkMode = !this.darkMode
      this.applyTheme()
    },

    initTheme() {
      const savedTheme = localStorage.getItem('theme')
      if (savedTheme === 'dark') {
        this.darkMode = true
      } else if (savedTheme === 'light') {
        this.darkMode = false
      } else {
        this.darkMode = window.matchMedia('(prefers-color-scheme: dark)').matches
      }
      this.applyTheme()
    },

    applyTheme() {
      if (this.darkMode) {
        document.documentElement.classList.add('dark')
        localStorage.setItem('theme', 'dark')
      } else {
        document.documentElement.classList.remove('dark')
        localStorage.setItem('theme', 'light')
      }
    },

    setLoading(value: boolean) {
      this.loading = value
    }
  }
})
