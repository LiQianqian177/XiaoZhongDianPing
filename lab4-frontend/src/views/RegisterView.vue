<script setup>
import { RouterLink } from 'vue-router'
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElNotification } from 'element-plus'
const router = useRouter()
const userStore = useUserStore()

// 用户名实时校验
const name = ref('');

const nameValid = function () {
  if (name.value.length < 4 || name.value.length > 20) return false;
  if (!/^[a-zA-Z0-9_]+$/.test(name.value)) return false;
  return true;
}

const nameHint = computed(() => {
  if (name.value.length === 0) return '';
  else if (name.value.length < 4 || name.value.length > 20) return '用户名长度应在4-20之间';
  else if (!/^[a-zA-Z0-9_]+$/.test(name.value)) return '用户名只能包含字母、数字和下划线';
  else return '√';
});

const nameHintClass = computed(() => {
  if (nameValid()) return 'valid';
  else return 'wrong';
});


// 密码实时校验
const password = ref('');

const passwordValid = function () {
  if (password.value.length < 6 || password.value.length > 20) return false;
  if (!/^[a-zA-Z0-9_]+$/.test(password.value)) return false;
  if (!/[a-zA-Z]/.test(password.value) || !/[0-9]/.test(password.value)) return false;
  return true;
}

const passwordHint = computed(() => {
  if (password.value.length === 0) return '';
  else if (password.value.length < 6 || password.value.length > 20) return '密码长度应在6-20之间';
  else if (!/^[a-zA-Z0-9_]+$/.test(password.value)) return '密码只能包含字母、数字和下划线';
  else if (!/[a-zA-Z]/.test(password.value) || !/[0-9]/.test(password.value)) return '密码必须包含字母和数字';
  else if (password.value.length < 10) return '密码强度：弱';
  else if (password.value.length < 15) return '密码强度：中';
  else return '密码强度：强';
});

const passwordHintClass = computed(() => {
  if (passwordValid()) return 'valid';
  else return 'wrong';
});

// 增强密码强度计算的可视化效果
const passwordStrengthLevel = computed(() => {
  if (password.value.length === 0 || !passwordValid()) return '';
  if (password.value.length < 10) return 'weak';
  if (password.value.length < 15) return 'medium';
  return 'strong';
});

// 计算不同强度下的进度条宽度
const strengthBarWidth = computed(() => {
  const level = passwordStrengthLevel.value;
  if (level === 'weak') return '33%';
  if (level === 'medium') return '66%';
  if (level === 'strong') return '100%';
  return '0%';
});


// 验证码
const verificationCode = ref('');
const captchaSrc = ref('');
const isLoading = ref(false);
const cooldownTime = ref(0); // 冷却时间（秒）
const cooldownInterval = ref(null);
const errorMessage = ref('');

// 带倒计时的验证码刷新函数
const refreshCaptcha = async () => {
  // 如果正在冷却中，不执行任何操作
  if (cooldownTime.value > 0) {
    return;
  }

  // 设置加载状态
  isLoading.value = true;
  errorMessage.value = '';

  try {
    // 更新验证码
    const timestamp = Date.now();
    captchaSrc.value = `http://localhost:8080/api/captcha?t=${timestamp}`;

    // 设置冷却时间
    cooldownTime.value = 3;

    // 启动冷却倒计时
    if (cooldownInterval.value) {
      clearInterval(cooldownInterval.value);
    }

    cooldownInterval.value = setInterval(() => {
      cooldownTime.value--;
      if (cooldownTime.value <= 0) {
        clearInterval(cooldownInterval.value);
        cooldownInterval.value = null;
      }
    }, 1000);

    // 模拟图片加载完成
    await new Promise(resolve => setTimeout(resolve, 500));
  } catch (error) {
    console.error('获取验证码失败:', error);
    if (error.response && error.response.status === 429) {
      errorMessage.value = '请求过于频繁，请等待几分钟后再试';
    } else {
      errorMessage.value = '获取验证码失败，请稍后再试';
    }
  } finally {
    isLoading.value = false;
  }
};

// 提交注册
const isSubmitting = ref(false);

const submitRegister = async () => {
  if (isSubmitting.value) {
    return;
  }

  if (!nameValid() || !passwordValid()) {
    errorMessage.value = '用户名和密码格式不正确！';
    return;
  }

  if (!verificationCode.value) {
    errorMessage.value = '请输入验证码！';
    return;
  }

  isSubmitting.value = true;
  errorMessage.value = '';

  try {
    const result = await userStore.register(name.value, password.value, verificationCode.value);

    if (result.success) {
      ElNotification({
        title: '成功',
        message: '用户注册成功！即将跳转到登录页面',
        type: 'success',
        duration: 3000,
        position: 'top-right'
      });

      // 注册成功后直接跳转，不刷新验证码
      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } else {
      console.log('注册失败:', result.message);

      // 使用 ElNotification 显示错误通知
      ElNotification({
        title: '注册失败',
        message: result.message || "未知错误",
        type: 'error',
        duration: 4000,
        position: 'top-right'
      });

      // 更新表单下方的错误消息
      errorMessage.value = result.message || "未知错误";

      // 只有在验证码相关错误或默认情况才刷新验证码
      if (!result.message ||
        result.message.includes("验证码") ||
        result.message.includes("captcha") ||
        result.message.includes("验证")) {
        cooldownTime.value = 0;
        refreshCaptcha();
      }
    }
  } catch (error) {
    console.error('注册过程中发生异常:', error);

    let errorMsg = "网络繁忙，请稍后再试！";

    if (error.response) {
      if (error.response.status === 429) {
        errorMsg = "请求过于频繁，请等待几分钟后再试！";
      } else if (error.response.data && error.response.data.message) {
        errorMsg = error.response.data.message;
      }
    }

    // 显示错误通知
    ElNotification({
      title: '注册失败',
      message: errorMsg,
      type: 'error',
      duration: 4000,
      position: 'top-right'
    });

    errorMessage.value = errorMsg;

    // 在大多数错误情况下刷新验证码
    if (error.response && error.response.status !== 429) {
      cooldownTime.value = 0;
      refreshCaptcha();
    }
  } finally {
    console.log('注册流程结束');
    isSubmitting.value = false;
  }
};

// 在组件挂载时获取验证码
onMounted(() => {
  refreshCaptcha();
});

// 在组件卸载前清除所有计时器
onBeforeUnmount(() => {
  if (cooldownInterval.value) {
    clearInterval(cooldownInterval.value);
  }
});
</script>


<template>
  <div class="reg-box">
    <h1>注册</h1>
    <form @submit.prevent="submitRegister">
      <div class="textbox">
        <input type="text" placeholder="用户名" v-model="name" />
      </div>
      <div :class="nameHintClass">{{ nameHint }}</div>

      <div class="textbox">
        <input type="password" placeholder="密码" v-model="password" />
      </div>
      <!-- 密码强度视觉指示器 -->
      <div v-if="password" class="password-strength">
        <div class="strength-bar">
          <div
            class="strength-bar-fill"
            :style="{ width: strengthBarWidth }"
            :class="passwordStrengthLevel"
          ></div>
        </div>
      </div>
      <div :class="passwordHintClass">{{ passwordHint }}</div>

      <div class="verify">
        <input type="text" placeholder="验证码" v-model="verificationCode" />
        <div class="captcha-container">
          <img
            v-if="!isLoading && captchaSrc"
            class="captcha"
            :src="captchaSrc"
            @click="refreshCaptcha"
            :class="{ 'disabled': cooldownTime > 0 }"
            alt="验证码"
          />
          <div v-else-if="isLoading" class="loading">加载中...</div>
          <div v-if="cooldownTime > 0" class="cooldown">{{ cooldownTime }}s</div>
        </div>
      </div>

      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

      <button
        type="submit"
        class="btn"
        :disabled="isSubmitting"
      >
        {{ isSubmitting ? '注册中...' : '注册' }}
      </button>
    </form>

    <div class="login-link">
      已有账号？<RouterLink to="/login">登录</RouterLink>
    </div>
  </div>
</template>


<style scoped>
.captcha-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 80px;
  min-height: 40px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.loading {
  font-size: 12px;
  color: #666;
}

.captcha {
  cursor: pointer;
  max-width: 80px;
  max-height: 40px;
  transition: opacity 0.3s ease;
}

.captcha.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cooldown {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 2;
}

.wrong {
  color: red;
  font-size: 12px;
  margin-bottom: 8px;
}

.valid {
  color: green;
  font-size: 12px;
  margin-bottom: 8px;
}

.error-message {
  color: red;
  margin: 8px 0;
  font-size: 14px;
  text-align: center;
}

/* 密码强度样式 */
.password-strength {
  margin: 5px 0;
}

.strength-bar {
  height: 4px;
  background-color: #e0e0e0;
  border-radius: 5px;
  margin-bottom: 5px;
}

.strength-bar-fill {
  height: 100%;
  border-radius: 5px;
  transition: width 0.3s ease;
}

.strength-bar-fill.weak {
  background-color: #f44336; /* 红色 */
}

.strength-bar-fill.medium {
  background-color: #ffa726; /* 橙色 */
}

.strength-bar-fill.strong {
  background-color: #4caf50; /* 绿色 */
}

.reg-box {
  width: 300px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: var(--color-text);

  background: #ebeff1;
  border: 4px;
  padding: 40px;
  border-radius: 12px;

  box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;
}

.reg-box h1 {
  float: left;
  font-size: 40px;
  border-bottom: 6px solid #4caf50;
  margin-bottom: 20px;
  padding: 13px 0;
}

.textbox {
  width: 100%;
  overflow: hidden;
  font-size: 20px;
  padding: 8px 0;
  margin: 4px 0;
  border-bottom: 1px solid #4caf50;
}

.textbox i {
  width: 26px;
  float: left;
  text-align: center;
}

.textbox input {
  border: none;
  outline: none;
  background: none;
  color: var(--color-text);
  font-size: 18px;
  width: 80%;
  float: left;
  margin: 0 10px;
}

.btn {
  width: 60%;
  background: #799ccb;
  box-shadow: rgba(0, 0, 0, 0.15) 1.95px 1.95px 2.6px;
  border: 0px;
  border-radius: 8px;
  color: var(--color-text);
  padding: 5px;
  font-size: 18px;
  cursor: pointer;
  margin: 12px 0;
  margin-left: 20%;
  transition: background-color 0.3s ease;
}

.btn:disabled {
  background-color: #b0c4de;
  cursor: not-allowed;
}

.verify {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 15px 0;
}

.verify input {
  overflow: hidden;
  padding: 8px 0;
  border: none;
  border-bottom: 1px solid #4caf50;
  outline: none;
  background: none;
  color: var(--color-text);
  font-size: 18px;
  width: 60%;
  margin: 0 10px;
}

.login-link {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}
</style>
