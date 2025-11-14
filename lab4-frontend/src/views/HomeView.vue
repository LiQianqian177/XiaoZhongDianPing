<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import SearchBar from '@/components/SearchBar.vue';
import axios from 'axios';
import { useSearchStore } from '@/stores/storefind.js';

const userStore = useUserStore();
const searchStore = useSearchStore();
const router = useRouter();
const allMerchants = ref([]);
const loading = ref(true);
const error = ref(null);

// 获取所有商家数据
const fetchAllMerchants = async () => {
  try {
    loading.value = true;
    // 使用 filterAndSort 接口，但不设置筛选条件
    const response = await axios.post("http://localhost:8080/api/merchants/filterAndSort",
      JSON.stringify({
        keywords: [],
        isSortByOverall: true,
        isSortByRating: false,
        isSortByPrice: false,
        isSortByDishPrice: false,
        minRating: null,
        minPrice: null,
        maxPrice: null,
        minAvgCost: null,
        maxAvgCost: null,
      }),
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    allMerchants.value = response.data;
  } catch (err) {
    console.error('获取商家列表错误:', err);
    error.value = '获取商家列表失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};

// 判断是否显示全部商家（没有搜索结果时显示）
const showAllMerchants = computed(() => {
  return searchStore.results.length === 0 || !searchStore.keyword;
});

// 监听搜索状态的变化
watch(
  () => searchStore.results.length,
  (newValue) => {
    // 当搜索结果被清空时（通过返回首页按钮）
    if (newValue === 0 && loading.value === false) {
      // 确保有数据可以显示
      if (allMerchants.value.length === 0) {
        fetchAllMerchants();
      }
    }
  }
);

// 跳转到商家详情页
const goToShopDetail = (shopId) => {
  router.push(`/shops/${shopId}`);
};

const logout = async () => {
  await userStore.logout();
  router.push('/');
};

const goToLogin = () => {
  router.push('/login');
};

const goToRegister = () => {
  router.push('/register');
};

const goToMyOrders = () => {
  router.push('/myorders');
};

const goToMyCoupons = () => {
  router.push('/mycoupons');
};

const goToNewUserCoupon = () => {
  router.push('/newusercoupon');
};

onMounted(() => {
  fetchAllMerchants();
});
</script>

<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="logo">
        <h2>美食发现</h2>
      </div>
      <div v-if="userStore.loggedIn" class="user-info">
        <span>欢迎, {{ userStore.username }}</span>
        <el-button class="highlight-button" @click="goToNewUserCoupon" v-if="!userStore.newUserCouponClaimed">🎁新人优惠券</el-button>
        <el-button type="success" @click="goToMyOrders">我的订单</el-button>
        <el-button type="success" @click="goToMyCoupons">我的券包</el-button>
        <el-button type="primary" @click="logout">退出登录</el-button>
      </div>
      <div v-else class="login-options">
        <el-button type="primary" @click="goToLogin">登录</el-button>
        <el-button type="success" @click="goToRegister">注册</el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 页面标题 -->
      <div v-if="showAllMerchants" class="welcome-section">
        <h1 class="page-title">发现周边美食</h1>
        <p class="subtitle">探索当地美食，发现舌尖上的惊喜</p>
      </div>

      <!-- 搜索栏组件 -->
      <SearchBar />

      <!-- 商家列表 -->
      <div class="merchants-section">
        <div v-if="loading" class="loading-state">
          <el-spin>加载商家列表中...</el-spin>
        </div>

        <div v-else-if="error" class="error-state">
          {{ error }}
        </div>

        <div v-else>
          <!-- 所有商家列表 -->
          <ul class="merchants-list" v-if="showAllMerchants">
            <li v-if="allMerchants.length === 0" class="no-merchants">
              暂无商家信息
            </li>
            <li v-else v-for="merchant in allMerchants" :key="merchant.id"
                class="merchant-card" @click="goToShopDetail(merchant.id)">
              <div class="merchant-header">
                <h3 class="merchant-name">{{ merchant.name }}</h3>
                <span class="merchant-rating">
                  <i class="el-icon-star-on"></i> {{ merchant.rating.toFixed(1) }}
                </span>
              </div>

              <p class="merchant-description">{{ merchant.description }}</p>

              <div class="merchant-info">
                <p><i class="el-icon-location"></i> 地址:{{ merchant.address }}</p>
                <p><i class="el-icon-phone"></i> 联系电话:{{ merchant.phone }}</p>
                <p><i class="el-icon-money"></i> 人均: ¥{{ merchant.avgCost.toFixed(2) }}</p>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <footer class="footer">
      <p>© 2025 美食发现 - 让您轻松找到好味道</p>
    </footer>
  </div>
</template>

<style scoped>
/* 样式代码保持不变 */
.home-page {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  background-color: #f8f9fa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏样式 */
.top-navbar {
  background-color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo h2 {
  margin: 0;
  color: #409EFF;
  font-weight: 600;
}

.user-info, .login-options {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  font-weight: 500;
  color: #606266;
}

/* 主内容区样式 */
.main-content {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 30px 20px;
  flex-grow: 1;
}

/* 欢迎区域的淡入效果 */
.welcome-section {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-title {
  text-align: center;
  margin: 10px 0 5px;
  font-size: 32px;
  color: #303133;
  font-weight: 600;
}

.subtitle {
  text-align: center;
  color: #909399;
  margin-top: 0;
  margin-bottom: 30px;
  font-size: 16px;
}

/* 商家列表样式 */
.merchants-section {
  margin-top: 40px;
}

.loading-state, .error-state {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  background: white;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.error-state {
  color: #f56c6c;
}

.merchants-list {
  list-style: none;
  padding: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

.no-merchants {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  font-size: 18px;
  background: white;
  border-radius: 8px;
  color: #909399;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.merchant-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  position: relative;
  padding: 20px;
  border-top: 4px solid #409EFF;
}

.merchant-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.merchant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.merchant-name {
  margin: 0;
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.merchant-rating {
  font-size: 18px;
  font-weight: bold;
  color: #ff9800;
  background: #fff8e6;
  padding: 4px 10px;
  border-radius: 20px;
}

.merchant-description {
  padding: 0;
  font-size: 15px;
  color: #606266;
  margin: 12px 0;
  line-height: 1.5;
}

.merchant-info {
  margin-top: 15px;
}

.merchant-info p {
  margin: 10px 0;
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.merchant-info i {
  margin-right: 8px;
  color: #909399;
}

/* 页脚样式 */
.footer {
  background: #2c3e50;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: 40px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .merchants-list {
    grid-template-columns: 1fr;
  }

  .top-navbar {
    flex-direction: column;
    gap: 10px;
    padding: 15px;
  }

  .user-info, .login-options {
    width: 100%;
    justify-content: center;
  }

  .page-title {
    font-size: 26px;
  }
}



.highlight-button {
  background-color: #ff9800 !important;
  color: white !important;
  font-weight: bold;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 152, 0, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(255, 152, 0, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 152, 0, 0);
  }
}
</style>
