import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import axios from 'axios'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// Configure axios with base URL
// axios.defaults.baseURL = 'http://localhost:8080'

// Add axios interceptor to handle authentication tokens
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

/* Seems useless
// Create API service for global access
const apiService = {
  // User authentication
  register: (userData) => axios.post('/users/register', userData),
  login: (credentials) => axios.post('/users/login', credentials),
  getCaptcha: () => axios.get('/api/captcha'),
  getLoggedUser: () => axios.get('/users/loggedUser'),
  logout: () => axios.get('/users/logout'),
}
app.config.globalProperties.$api = apiService
*/

const app = createApp(App)

app.use(createPinia())
app.use(router)

// Make axios available globally
app.config.globalProperties.$http = axios

app.use(ElementPlus)

app.mount('#app')
