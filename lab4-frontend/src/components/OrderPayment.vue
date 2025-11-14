<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import axios from 'axios';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const userId = userStore.id;
const emit = defineEmits(['close', 'order-success']);
const props = defineProps({
  order: {
    type: Object,
    required: true
  },
  visible: {
    type: Boolean,
    default: false
  },
});

const loading = ref(false);
const coupons = ref([]);
const selectedCoupon = ref(null);
const errorMessage = ref('');
const packageDetail = ref({});

// 获取套餐详情
const fetchPackageDetail = async () => {
  try {
    const response = await axios.get(`/api/orders/${props.order.orderId}/details`);
    packageDetail.value = response.data;
  } catch (err) {
    console.error('获取套餐详情失败:', err);
    errorMessage.value = '获取套餐详情失败';
  }
};

// 对单张优惠券计算应用后的价格
const couponPrice = function(coupon){
  return coupon ? Math.max(0, packageDetail.value.totalPrice - coupon.discount) : packageDetail.value.totalPrice;
};

// 计算优惠后价格
const finalPrice = computed(() => {
  return couponPrice(selectedCoupon.value);
});

// 获取用户可用优惠券
const fetchUserCoupons = async () => {
  try {
    loading.value = true;
    coupons.value = [];

    const response = await axios.post('http://localhost:8080/api/coupons/userCoupon', {
      userId: userId
    });

    // 过滤出可用的优惠券
    for (let i = 0; i < response.data.length; i++) {
      try {
        const useResponse = await axios.post('http://localhost:8080/api/coupons/couponUse', {
          orderId: props.order.orderId,
          userCouponId: response.data[i].userCouponId
        });
        coupons.value.push(response.data[i].coupon);
        coupons.value[i].userCouponId = response.data[i].userCouponId;
        // useResponse.data is like "Discount amount: 15.00"
        coupons.value[i].discount = useResponse.data.substring(useResponse.data.indexOf(':') + 1).trim();
        console.log(useResponse.data);
      } catch (err) {
        console.log('优惠券'+i+'不可用:', err);
      }
    }

    // 默认选择减免金额最高的优惠券
    if (coupons.value.length > 0) {
      selectHighestCoupon();
    }
  } catch (err) {
    console.error('获取优惠券错误:', err);
    coupons.value = [];
  } finally {
    loading.value = false;
  }
};

// 选择减免金额最高的优惠券
const selectHighestCoupon = () => {
  if (coupons.value.length === 0) return;

  selectedCoupon.value = coupons.value.reduce((highest, current) => {
    return couponPrice(current) < couponPrice(highest) ? current : highest;
  }, coupons.value[0]);
};

// 选择优惠券
const selectCoupon = (coupon) => {
  selectedCoupon.value = coupon;
};

// 取消选择优惠券
const clearCoupon = () => {
  selectedCoupon.value = null;
};

// 提交订单
const submitOrder = async () => {
  try {
    loading.value = true;
    errorMessage.value = '';

    // 使用优惠券
    const couponId = selectedCoupon.value ? selectedCoupon.value.userCouponId : null;
    const discount = selectedCoupon.value ? selectedCoupon.value.discount : 0;
    await axios.post('http://localhost:8080/api/orders/pay', {
      orderId: props.order.orderId,
      userCouponId: couponId,
      discount: discount
    });

    // 订单提交成功
    emit('order-success', props.order.orderId);
  } catch (err) {
    console.error('提交订单错误:', err);

    // 检查是否是未登录错误
    if (err.response && err.response.status === 401) {
      errorMessage.value = '请先登录后再进行购买';
    } else {
      errorMessage.value = '订单提交失败: ' + (err.response?.data?.message || err.message);
    }
  } finally {
    loading.value = false;
  }
};

// 取消下单
const cancelOrder = () => {
  emit('close');
};

// 当组件显示时加载优惠券
onMounted(() => {
  if (props.visible) {
    fetchPackageDetail();
    fetchUserCoupons();
  }
});

// 监听visible属性变化，当显示时加载优惠券
watch(() => props.visible, (newValue) => {
  if (newValue) {
    fetchPackageDetail();
    fetchUserCoupons();
  }
});
</script>

<template>
  <div class="order-confirm-container" v-if="visible">
    <div class="order-confirm-overlay" @click="cancelOrder"></div>
    <div class="order-confirm-modal">
      <div class="order-confirm-header">
        <h2>支付订单</h2>
        <button class="close-button" @click="cancelOrder">×</button>
      </div>

      <!-- 套餐信息确认 -->
      <div class="package-info">
        <h3>{{ props.order.groupbuyTitle }}</h3>
        <div class="package-price" v-if="packageDetail.totalPrice">
          <span>原价: ¥{{ packageDetail.totalPrice.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 优惠券选择 -->
      <div class="coupon-section">
        <h3>选择优惠券</h3>
        <div v-if="loading" class="loading-coupons">
          正在加载优惠券...
        </div>
        <div v-else-if="coupons.length === 0" class="no-coupons">
          您暂无可用的优惠券
        </div>
        <div v-else class="coupon-list">
          <div class="coupon-option">
            <label>
              <input
                type="radio"
                name="coupon"
                :value="null"
                :checked="selectedCoupon === null"
                @change="clearCoupon"
              >
              不使用优惠券
            </label>
          </div>
          <div
            v-for="coupon in coupons"
            :key="coupon.id"
            class="coupon-option"
          >
            <label>
              <input
                type="radio"
                name="coupon"
                :value="coupon.id"
                :checked="selectedCoupon && selectedCoupon.id === coupon.id"
                @change="selectCoupon(coupon)"
              >
              <div class="coupon-info">
                <span class="coupon-amount">¥{{ coupon.amount?.toFixed(2) }}</span>
                <span class="coupon-name">{{ coupon.name }}</span>
                <span class="coupon-validity">{{ coupon.validityPeriod }}</span>
              </div>
            </label>
          </div>
        </div>
      </div>

      <!-- 订单金额 -->
      <div class="order-total" v-if="packageDetail.totalPrice">
        <div class="total-label">最终金额:</div>
        <div class="total-amount">¥{{ finalPrice.toFixed(2) }}</div>
      </div>

      <!-- 错误信息 -->
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <!-- 提交按钮 -->
      <div class="action-buttons">
        <button
          class="cancel-button"
          @click="cancelOrder"
        >
          取消
        </button>
        <button
          class="submit-button"
          @click="submitOrder"
          :disabled="loading"
        >
          {{ loading ? '提交中...' : '确认支付' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-confirm-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.order-confirm-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.order-confirm-modal {
  position: relative;
  width: 90%;
  max-width: 500px;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  max-height: 80vh;
  overflow-y: auto;
}

.order-confirm-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.order-confirm-header h2 {
  margin: 0;
  font-size: 20px;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.close-button:hover {
  color: #333;
}

.package-info {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.package-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
}

.package-price {
  font-size: 16px;
  color: #f56c6c;
}

.coupon-section {
  margin-bottom: 20px;
}

.coupon-section h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
}

.loading-coupons, .no-coupons {
  padding: 10px;
  text-align: center;
  color: #909399;
}

.coupon-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #eee;
  border-radius: 4px;
}

.coupon-option {
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.coupon-option:last-child {
  border-bottom: none;
}

.coupon-option label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.coupon-option input {
  margin-right: 10px;
}

.coupon-info {
  display: flex;
  flex-direction: column;
}

.coupon-amount {
  font-weight: bold;
  color: #f56c6c;
  font-size: 16px;
}

.coupon-name {
  font-size: 14px;
  margin: 2px 0;
}

.coupon-validity {
  font-size: 12px;
  color: #909399;
}

.order-total {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-top: 1px solid #eee;
  margin-bottom: 20px;
}

.total-label {
  font-size: 16px;
}

.total-amount {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.error-message {
  padding: 10px;
  background-color: #fef0f0;
  color: #f56c6c;
  border-radius: 4px;
  margin-bottom: 15px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-button, .submit-button {
  padding: 10px 20px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 16px;
}

.cancel-button {
  background-color: #f2f2f2;
  color: #333;
}

.submit-button {
  background-color: #f56c6c;
  color: white;
}

.submit-button:hover {
  background-color: #f78989;
}

.submit-button:disabled {
  background-color: #f9c0c0;
  cursor: not-allowed;
}
</style>
