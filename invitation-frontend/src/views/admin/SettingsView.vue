<template>
  <div class="settings-page page-container">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="card-shadow settings-section">
          <h3>个人信息</h3>
          <el-form :model="profileForm" label-width="80px" @submit.prevent="handleUpdateProfile">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="handleAvatarUpload"
                accept="image/*"
              >
                <el-avatar :size="64" :src="profileForm.avatar || undefined">
                  {{ profileForm.nickname?.charAt(0) }}
                </el-avatar>
              </el-upload>
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input :value="formatPhone(profileForm.phone)" disabled />
            </el-form-item>
            <el-form-item v-if="userStore.isEnterprise" label="企业名称">
              <el-input v-model="profileForm.enterpriseName" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card-shadow settings-section">
          <h3>外观设置</h3>
          <el-form label-width="80px">
            <el-form-item label="暗黑模式">
              <el-switch v-model="isDarkMode" @change="appStore.toggleDarkMode()" />
            </el-form-item>
            <el-form-item label="侧边栏">
              <el-switch v-model="isCollapsed" @change="appStore.toggleSidebar()" />
            </el-form-item>
          </el-form>
        </div>

        <div class="card-shadow settings-section">
          <h3>安全设置</h3>
          <el-form label-width="80px">
            <el-form-item label="绑定手机">
              <el-button size="small" @click="showBindPhone = true">更换手机号</el-button>
            </el-form-item>
            <el-form-item label="微信绑定">
              <el-button size="small">绑定微信</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="showBindPhone" title="更换手机号" width="400px">
      <el-form :model="bindPhoneForm" label-width="80px">
        <el-form-item label="新手机号">
          <el-input v-model="bindPhoneForm.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="flex gap-2">
            <el-input v-model="bindPhoneForm.code" maxlength="6" />
            <el-button>获取验证码</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindPhone = false">取消</el-button>
        <el-button type="primary">确认绑定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'
import { updateUserInfo } from '@/api/user'
import { uploadImage } from '@/api/upload'
import { formatPhone } from '@/utils/format'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const appStore = useAppStore()
const saving = ref(false)
const showBindPhone = ref(false)

const isDarkMode = computed({
  get: () => appStore.darkMode,
  set: () => {}
})

const isCollapsed = computed({
  get: () => appStore.sidebarCollapsed,
  set: () => {}
})

const profileForm = reactive({
  nickname: '',
  avatar: '',
  phone: '',
  enterpriseName: ''
})

const bindPhoneForm = reactive({
  phone: '',
  code: ''
})

function initForm() {
  if (userStore.userInfo) {
    profileForm.nickname = userStore.userInfo.nickname
    profileForm.avatar = userStore.userInfo.avatar
    profileForm.phone = userStore.userInfo.phone
    profileForm.enterpriseName = userStore.userInfo.enterpriseName
  }
}

async function handleUpdateProfile() {
  saving.value = true
  try {
    await updateUserInfo({
      nickname: profileForm.nickname,
      avatar: profileForm.avatar,
      enterpriseName: profileForm.enterpriseName
    })
    await userStore.fetchUserInfo()
    ElMessage.success('更新成功')
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    saving.value = false
  }
}

async function handleAvatarUpload(options: any) {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const res = await uploadImage(formData)
    profileForm.avatar = res.data.url
    await handleUpdateProfile()
  } catch {
    ElMessage.error('上传失败')
  }
}

onMounted(() => {
  initForm()
})
</script>

<style scoped lang="scss">
.page-header {
  margin-bottom: 20px;
  h2 { margin: 0; }
}

.settings-section {
  padding: 24px;
  margin-bottom: 20px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-color-light);
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    cursor: pointer;
    border-radius: 50%;
  }
}
</style>
