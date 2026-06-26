import type { FormItemRule } from 'element-plus'

/** 手机号验证 */
export const phoneValidator: FormItemRule = {
  pattern: /^1[3-9]\d{9}$/,
  message: '请输入正确的手机号',
  trigger: 'blur'
}

/** 验证码验证 */
export const smsCodeValidator: FormItemRule = {
  pattern: /^\d{4,6}$/,
  message: '请输入4-6位数字验证码',
  trigger: 'blur'
}

/** 必填验证 */
export function requiredValidator(message: string = '此字段为必填项'): FormItemRule {
  return { required: true, message, trigger: 'blur' }
}

/** 长度验证 */
export function lengthValidator(min: number, max: number, message?: string): FormItemRule {
  return {
    min,
    max,
    message: message || `长度在 ${min} 到 ${max} 个字符`,
    trigger: 'blur'
  }
}

/** URL验证 */
export const urlValidator: FormItemRule = {
  pattern: /^https?:\/\/.+/,
  message: '请输入正确的URL地址',
  trigger: 'blur'
}

/** 金额验证 */
export const amountValidator: FormItemRule = {
  pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
  message: '请输入正确的金额',
  trigger: 'blur'
}

/** 邮箱验证 */
export const emailValidator: FormItemRule = {
  type: 'email',
  message: '请输入正确的邮箱地址',
  trigger: 'blur'
}

/** 身份证验证 */
export const idCardValidator: FormItemRule = {
  pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
  message: '请输入正确的身份证号',
  trigger: 'blur'
}
