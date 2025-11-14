<template>
  <div class="coupon-container">
    <h2 style="margin: 10px 20px 20px 0px;">我的优惠券</h2>
    <el-button class="return-button" @click="router.push('/')">返回首页</el-button>
    <div v-if="loading" class="loading-state">
      加载中...
    </div>
    <div v-else-if="error" class="error-state">
      {{ error }}
    </div>
    <div v-else-if="userCoupons.length === 0" class="empty-state">
      您还没有优惠券，快去领取吧！
    </div>
    <div v-else class="coupon-grid">
      <div
        v-for="userCoupon in userCoupons"
        :key="userCoupon.userCouponId"
        class="coupon-card"
        :class="{ 'expired': isExpired(userCoupon.coupon) }"
      >
        <div class="coupon-header">
          <h3>{{ userCoupon.coupon.name }}</h3>
          <span class="status-badge" :class="userCoupon.status.toLowerCase()">
            {{ renderStatus(userCoupon.status) }}
          </span>
        </div>
        <p v-if="userCoupon.coupon.description" class="description">{{ userCoupon.coupon.description }}</p>
        <p class="type">{{ renderCouponType(userCoupon.coupon) }}</p>
        <p v-if="userCoupon.coupon.usageThreshold > 0">门槛：满{{ userCoupon.coupon.usageThreshold.toFixed(2) }}元可用</p>
        <p v-if="userCoupon.coupon.maxDiscountAmount">最多抵扣：{{ userCoupon.coupon.maxDiscountAmount.toFixed(2) }}元</p>
        <p v-if="userCoupon.coupon.applicableCategory">适用品类：{{ userCoupon.coupon.applicableCategory }}</p>
        <p v-if="userCoupon.coupon.applicableStore">适用店铺：{{ userCoupon.coupon.applicableStore }}</p>
        <p class="expiry">有效期：{{ formatDateRange(userCoupon.coupon.validFrom, userCoupon.coupon.validTo) }}</p>
        <p v-if="userCoupon.coupon.newUserCoupon" class="new-user-tag">新用户专享</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';
import { useUserStore } from '@/stores/user';
import {useRouter} from "vue-router";
const router = useRouter();
const userStore = useUserStore();
const userCoupons = ref([]);
const loading = ref(true);
const error = ref('');

onMounted(async () => {
  await fetchUserCoupons();
});

async function fetchUserCoupons() {
  // 重置状态
  loading.value = true;
  error.value = '';

  // 检查用户是否登录
  const isLoggedIn = await userStore.checkLogin();

  if (!isLoggedIn) {
    error.value = '请先登录后查看您的优惠券';
    loading.value = false;
    return;
  }

  // 获取当前用户ID
  const userId = userStore.id;

  if (!userId) {
    error.value = '无法获取用户信息，请重新登录';
    loading.value = false;
    return;
  }

  try {
    // 调用API获取用户优惠券
    const response = await axios.post(
      'http://localhost:8080/api/coupons/userCoupon',
      { userId: userId },
      {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true
      }
    );

    console.log(response.data);
    // 直接使用返回的数据，不需要额外处理
    userCoupons.value = response.data;
  } catch (err) {
    if (err.response && err.response.status === 404){
      userCoupons.value = [];
    } else {
      console.error('获取优惠券失败:', err);
      error.value = '获取优惠券失败，请稍后再试';

      // 如果是401或403错误，提示用户登录
      if (err.response && (err.response.status === 401 || err.response.status === 403)) {
        error.value = '请先登录后查看您的优惠券';
      }
    }
  } finally {
    loading.value = false;
  }
}

// 根据优惠券类型渲染显示文本
function renderCouponType(coupon) {
  if (coupon.type.indexOf('DISCOUNT') >= 0)
    return `${coupon.amount / 10}折`; // 假设90.00代表9折
  else if (coupon.type.indexOf('TO_FIXED') >= 0)
    return `特价${coupon.amount.toFixed(2)}元`;
  else if (coupon.type.indexOf('FIXED') >= 0)
    return `减${coupon.amount.toFixed(2)}元`;
  else
    return '';
}

// 格式化日期区间
function formatDateRange(startDate, endDate) {
  const start = new Date(startDate);
  const end = new Date(endDate);

  return `${start.toLocaleDateString()} - ${end.toLocaleDateString()}`;
}

// 检查优惠券是否过期
function isExpired(coupon) {
  const now = new Date();
  const validTo = new Date(coupon.validTo);
  return now > validTo;
}

// 渲染优惠券状态
function renderStatus(status) {
  switch (status) {
    case 'UNUSED':
      return '未使用';
    case 'USED':
      return '已使用';
    case 'EXPIRED':
      return '已过期';
    default:
      return status;
  }
}
</script>

<style scoped>
.coupon-container {
  padding: 20px;
  position: relative;
}

.return-button {
  position: absolute;
  right: 20px;
  top: 20px;
}
.coupon-container {
  padding: 20px;
}

.coupon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.coupon-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 16px;
  background-color: #fff8e1;
  box-shadow: 0 4px 8px rgba(0,0,0,0.05);
  transition: transform 0.2s;
  position: relative;
  overflow: hidden;
}

.coupon-card:hover {
  transform: translateY(-4px);
}

.coupon-card.expired {
  background-color: #f5f5f5;
  color: #999;
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.coupon-header h3 {
  margin: 0;
  flex: 1;
}

.status-badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status-badge.unused {
  background-color: #4caf50;
  color: white;
}

.status-badge.used {
  background-color: #9e9e9e;
  color: white;
}

.status-badge.expired {
  background-color: #f44336;
  color: white;
}

.description {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
}

.type {
  font-weight: bold;
  color: #e91e63;
  font-size: 18px;
  margin: 10px 0;
}

.expiry {
  font-size: 0.9em;
  color: #555;
  margin-top: 12px;
}

.new-user-tag {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #ff9800;
  color: white;
  padding: 4px 8px;
  font-size: 12px;
  border-bottom-left-radius: 8px;
}

.loading-state, .error-state, .empty-state {
  padding: 20px;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-top: 20px;
}

.error-state {
  color: #e91e63;
}

.empty-state {
  color: #777;
  padding: 40px;
}

@media (max-width: 768px) {
  .coupon-grid {
    grid-template-columns: 1fr;
  }
}
</style>
