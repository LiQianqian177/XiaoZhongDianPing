<script setup>
import { ref } from "vue";
import { RouterLink, useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const name = ref('');
const password = ref('');
const errorMessage = ref('');

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

const submitLogin = async () => {
  if (!name.value || !password.value) {
    errorMessage.value = "用户名和密码不能为空";
    return;
  }

  const result = await userStore.login(name.value, password.value);
  if (result.success) {
    // 如果有重定向参数，则重定向到指定页面，否则返回首页
    const redirectPath = route.query.redirect || '/';
    router.push(redirectPath);
  } else {
    errorMessage.value = result.message || "网络繁忙，请稍后再试";
  }
};
</script>

<template>
  <div class="login-box">
    <h1>登录</h1>
    <form @submit.prevent="submitLogin">
      <div class="textbox">
        <input type="text" placeholder="用户名" v-model="name" />
      </div>
      <div class="textbox">
        <input type="password" placeholder="密码" v-model="password" />
      </div>
      <input type="submit" class="btn" value="登录" />
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    </form>

    <div>
      还没有账号？<RouterLink to="/register">注册</RouterLink>
    </div>
    <div class="back-to-home">
      <RouterLink to="/">返回首页</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.login-box {
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

.login-box h1 {
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
  margin: 8px 0;
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
}

.error {
  color: red;
  margin-top: 6px;
}

.back-to-home {
  margin-top: 15px;
  text-align: center;
}
</style>
