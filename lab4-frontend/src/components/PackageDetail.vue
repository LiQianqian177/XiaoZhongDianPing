<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import OrderConfirm from '../components/OrderConfirm.vue';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const router = useRouter();
const packageDetail = ref(null);
const loading = ref(true);
const error = ref(null);
const orderConfirmVisible = ref(false);
const userStore = useUserStore();
const userStatus = ref({
  isLoggedIn: userStore.loggedIn
});

onMounted(async () => {
  try {
    // 同时获取用户登录状态和套餐详情
    await Promise.all([
      fetchPackageDetail()
    ]);
  } catch (err) {
    console.error('初始化错误:', err);
    error.value = err.message;
  } finally {
    loading.value = false;
  }
});

// 获取套餐详情

const fetchPackageDetail = async () => {
  try {
    const packageId = route.params.id;
    // 调用API获取套餐详情
    const response = await axios.get(`http://localhost:8080/api/groupbuys/${packageId}`);
    packageDetail.value = response.data;
    console.log(packageDetail.value);
  } catch (err) {
    console.error('获取套餐信息错误:', err);
    error.value = err.message;
  }
};

const goBack = () => {
  router.back(); // 返回上一页
};

// 购买套餐
const buyPackage = () => {
  // 检查用户是否已登录
  if (!userStatus.value.isLoggedIn) {
    // 未登录，重定向到登录页面，并设置返回URL
    router.push(`/login?redirect=${encodeURIComponent(router.currentRoute.value.fullPath)}`);
    return;
  }

  // 已登录，显示下单确认组件
  orderConfirmVisible.value = true;
};

// 订单提交成功
const handleOrderSuccess = (orderId) => {
  // 关闭下单确认框
  orderConfirmVisible.value = false;

  // 显示成功消息
  alert('下单成功！订单号：' + orderId);

  // 跳转到订单详情页
  router.push(`/order/${orderId}`);
};
</script>

<template>
  <div class="package-detail-container">
    <div v-if="loading" class="loading">
      加载中...
    </div>
    <div v-else-if="error" class="error">
      加载失败: {{ error }}
    </div>
    <div v-else-if="packageDetail" class="package-detail">
      <!-- 返回按钮 -->
      <div class="back-button">
        <button @click="goBack">← 返回商家</button>
      </div>

      <!-- 套餐主图 -->
      <div class="package-image-container">
        <img src="/1.png" />
      </div>

      <!-- 套餐标题和价格 -->
      <div class="package-header">
        <h1>{{ packageDetail.title }}</h1>
        <div class="package-price-container">
          <span class="package-price">¥{{ packageDetail.price.toFixed(2) }}</span>
          <span class="package-original-price" v-if="packageDetail.originalPrice">
            原价: <s>¥{{ packageDetail.originalPrice.toFixed(2) }}</s>
          </span>
        </div>
      </div>

      <!-- 销量信息 -->
      <div class="package-sales">
        <span>已售 {{ packageDetail.sales }} 份</span>
      </div>

      <!-- 套餐描述 -->
      <div class="package-description">
        <h2>套餐描述</h2>
        <p>{{ packageDetail.description }}</p>
      </div>

      <!-- 套餐内容 -->
      <div class="package-content">
        <h2>套餐内容</h2>
          <span class="item-name">{{ packageDetail.content }}</span>
      </div>

      <!-- 使用规则 -->
      <div class="package-rules" v-if="packageDetail.rules && packageDetail.rules.length > 0">
        <h2>使用规则</h2>
        <ul>
          <li v-for="(rule, index) in packageDetail.rules" :key="index">
            {{ rule }}
          </li>
        </ul>
      </div>

      <!-- 有效期 -->
      <div class="package-validity" v-if="packageDetail.validityPeriod">
        <h2>有效期</h2>
        <p>{{ packageDetail.validityPeriod }}</p>
      </div>

      <!-- 购买按钮 -->
      <div class="buy-button-container">
        <button class="buy-button" @click="buyPackage">
          <span>立即下单</span>
        </button>
        <div class="login-hint" v-if="!userStatus.isLoggedIn">
          (需要先登录才能购买)
        </div>
      </div>
    </div>

    <!-- 订单确认组件 -->
    <OrderConfirm
      :package-detail="packageDetail"
      :visible="orderConfirmVisible"
      @close="orderConfirmVisible = false"
      @order-success="handleOrderSuccess"
    />
  </div>
</template>

<style scoped>
.package-detail-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.loading, .error {
  text-align: center;
  padding: 40px;
  font-size: 18px;
}

.error {
  color: #f56c6c;
}

.back-button {
  margin-bottom: 20px;
}

.back-button button {
  padding: 8px 16px;
  background: #f2f2f2;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.back-button button:hover {
  background: #e6e6e6;
}

.package-image-container {
  width: 100%;
  height: 300px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 20px;
}

.package-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.package-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.package-header h1 {
  margin: 0;
  font-size: 24px;
  flex: 1;
}

.package-price-container {
  text-align: right;
}

.package-price {
  font-weight: bold;
  color: #f56c6c;
  font-size: 24px;
  display: block;
}

.package-original-price {
  color: #909399;
  font-size: 14px;
}

.package-sales {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.package-description, .package-content, .package-rules, .package-validity {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.package-description h2, .package-content h2, .package-rules h2, .package-validity h2 {
  font-size: 18px;
  margin-bottom: 10px;
}

.package-description p {
  font-size: 16px;
  line-height: 1.5;
}

.package-content ul, .package-rules ul {
  padding-left: 20px;
}

.package-content li, .package-rules li {
  margin-bottom: 8px;
  font-size: 16px;
}

.item-name {
  font-weight: bold;
}

.item-quantity {
  color: #909399;
  margin-left: 10px;
}

.buy-button-container {
  text-align: center;
  margin-top: 30px;
}

.buy-button {
  padding: 12px 30px;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 18px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.buy-button:hover {
  background-color: #f78989;
}

.buy-button:disabled {
  background-color: #f9c0c0;
  cursor: not-allowed;
}

.login-hint {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .package-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .package-price-container {
    text-align: left;
    margin-top: 10px;
  }

  .package-image-container {
    height: 200px;
  }
}
</style>
