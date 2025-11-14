<template>
  <div class="orders-page">
    <div class="horizontal-line">
      <h2>我的订单</h2>
      <el-button class="return-button" @click="router.push('/')">返回首页</el-button>
    </div>
    <div class="orders-grid" v-if="orders.length > 0">
      <div
        v-for="order in orders"
        :key="order.orderId"
        class="order-card"
      >
        <div class="order-content" @click="router.push('/order/' + order.orderId)">
          <h3>{{ order.groupbuyTitle }}</h3>
          <p><strong>商家：</strong>{{ order.storeName }}</p>
          <p><strong>状态：</strong>{{ statusToText(order.status) }}</p>
          <p v-if="order.status != 'UNPAID'"><strong>支付时间：</strong>{{ formatTime(order.orderTime) }}</p>
        </div>
        <div class="order-actions" v-if="order.status === 'UNPAID'">
          <el-button type="primary" @click.stop="openPaymentModal(order)">立即支付</el-button>
        </div>
      </div>
    </div>
    <div v-else>
      <p class="empty-hint">暂无订单记录</p>
    </div>

    <!-- 支付弹窗 -->
    <OrderPayment
      v-if="showPaymentModal"
      :order="selectedOrder"
      :visible="showPaymentModal"
      @close="showPaymentModal = false"
      @order-success="handleOrderSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import OrderPayment from '@/components/OrderPayment.vue'  // 新增引入组件

const userStore = useUserStore()

const router = useRouter()
const orders = ref([])
const userId = userStore.id
const showPaymentModal = ref(false)
const selectedOrder = ref(null)

const fetchOrders = async () => {
  try {
    const res = await axios.get(`/api/orders/my-orders?userId=${userId}`)
    orders.value = res.data
    console.log('订单数据:', orders.value)
  } catch (err) {
    console.error('获取订单失败:', err)
  }
}

const formatTime = (raw) => {
  const date = new Date(raw)
  return date.toLocaleString()
}

const statusToText = (status) => {
  const statusMap = {
    PAID: '已支付',
    UNPAID: '未支付',
    USED: '已使用',
    CANCELLED: '已退款',
  }
  return statusMap[status] || status
}

// 打开支付弹窗
const openPaymentModal = (order) => {
  selectedOrder.value = order
  showPaymentModal.value = true
}

// 处理订单支付成功
const handleOrderSuccess = (orderId) => {
  showPaymentModal.value = false
  // 刷新订单列表
  fetchOrders()
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-page {
  padding: 24px;
  max-width: 960px;
  margin: auto;
}

.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(45%, 1fr));
  gap: 20px;
  margin-top: 16px;
}

.order-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.order-content {
  cursor: pointer;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.order-card h3 {
  margin: 0 0 8px;
  color: #333;
}

.order-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.empty-hint {
  text-align: center;
  color: #999;
  font-size: 16px;
  margin-top: 40px;
}

.return-button {
  margin-bottom: 20px;
  background-color: #409eff;
  color: white;
  border-radius: 4px;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
}
.return-button:hover {
  background-color: #66b1ff;
}

.horizontal-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
  padding-bottom: 16px;
}
.horizontal-line h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}
</style>
