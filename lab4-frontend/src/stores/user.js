import { defineStore } from "pinia";
import axios from "axios";

export const useUserStore = defineStore("user", {
  state: () => ({
    loggedIn: false,
    username: "",
    id: null,
    newUserCouponClaimed: false
  }),
  actions: {
    async register(name, password, verificationCode) {
      // 避免短时间内多次请求
      if (this._isRegistering) {
        return { success: false, message: "请求处理中，请稍候..." };
      }

      this._isRegistering = true;

      try {
        const response = await axios.post("http://localhost:8080/users/register", {
          name,
          password,
          verificationCode,
        }, { withCredentials: true });
        console.log('注册响应:', response.data);
        if (response.data.id) {
          return { success: true };
        } else {
          return { success: false, message: response.data.message };
        }
      } catch (error) {
        console.error("注册错误:", error);
        if (error.response) {
          // 服务器返回错误状态码
          const { data } = error.response;
          return {
            success: false,
            message: data
          };
        }
      } finally {
        // 延迟重置状态，防止频繁请求
        setTimeout(() => {
          this._isRegistering = false;
        }, 1000);
      }
    },

    async login(name, password) {
      // 避免短时间内多次请求
      if (this._isLoggingIn) {
        return { success: false, message: "登录请求处理中，请稍候..." };
      }

      this._isLoggingIn = true;

      try {
        const response = await axios.post("http://localhost:8080/users/login", {
          name,
          password
        }, { withCredentials: true });

        if (response.data.token) {
          const check = await this.checkLogin();
          return { success: check };
        } else {
          return { success: false, message: response.data.message || "登录失败" };
        }
      } catch (error) {
        console.error("登录错误:", error);

        // 特别处理 429 错误
        if (error.response && error.response.status === 429) {
          return {
            success: false,
            message: "请求频率过高，请稍后再试"
          };
        }

        return {
          success: false,
          message: error.response?.data?.message || "网络繁忙，请稍后再试！"
        };
      } finally {
        // 延迟重置状态，防止频繁请求
        setTimeout(() => {
          this._isLoggingIn = false;
        }, 1000);
      }
    },

    async checkLogin(){
      try {
        const response = await axios.get("http://localhost:8080/users/loggedUser", { withCredentials: true });

        if (response.data.name) {
          this.loggedIn = true;
          this.username = response.data.name;
          this.id = response.data.userID;
          return true;
        } else {
          this.clearUserData();
          return false;
        }
      } catch (error) {
        console.error("会话验证错误:", error);
        // 如果是认证错误，清除用户数据
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
          this.clearUserData();
        }
        return false;
      }
    },

    async logout() {

      try {
        await axios.get("http://localhost:8080/users/logout", { withCredentials: true });
      } catch (error) {
        console.error("登出错误:", error);
      } finally {
        this.clearUserData();
      }
    },

    clearUserData() {
      this.loggedIn = false;
      this.username = "";
      this.id = null;
    }
  }
});
