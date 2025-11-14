<template>
  <div class="order-detail-container">

    <!-- 返回按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="goToOrderList">返回我的订单</el-button>
      <el-button type="primary" @click="goToHome">返回首页</el-button>
    </div>

    <!-- 头部标题 -->
    <div class="header">
      <h1>订单详情</h1>
      <el-tag :type="statusTagType" effect="dark" size="large">
        {{ formatStatus(orderInfo.status) }}
      </el-tag>
    </div>

    <!-- 主体内容 -->
    <el-card shadow="hover" class="main-card">
      <div class="detail-container">
        <!-- 左栏：订单信息 -->
        <div class="left-section">
          <el-descriptions title="订单信息" :column="1" border>
            <el-descriptions-item label="订单编号">
              {{ orderInfo.orderId }}
            </el-descriptions-item>
            <el-descriptions-item label="团购项目">
              {{ orderInfo.groupbuyTitle }}
            </el-descriptions-item>
            <el-descriptions-item label="支付时间" v-if="orderInfo.status !== 'UNPAID'">
              {{ formatDate(orderInfo.orderTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="券前金额" v-if="orderInfo.status === 'UNPAID'">
              <span class="price">¥{{ orderInfo.totalPrice.toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item descriptions-item label="支付金额" v-if="orderInfo.status !== 'UNPAID'">
              <span class="price">¥{{ orderInfo.totalPrice.toFixed(2) }}</span>
            </el-descriptions-item>
            <button class="buy-button" @click="goToPay" v-if="orderInfo.status === 'UNPAID'">
              <span>立即支付</span>
            </button>
          </el-descriptions>
        </div>

        <!-- 右栏：二维码和店铺信息 -->
        <div class="right-section">
          <el-card shadow="never" class="qrcode-card">
            <div class="qrcode-header" v-if="orderInfo.status === 'PAID'">
              <span class="voucher-code">{{ orderInfo.voucherCode }}</span>
              <span class="tips">到店出示二维码核销</span>
            </div>

            <!-- 二维码组件 -->
            <div class="qrcode-wrapper" v-if="orderInfo.status === 'PAID'">
              <qrcode-vue
                :value="orderInfo.voucherCode"
                :size="180"
                level="H"
                class="qrcode"
              />
            </div>

            <div class="store-info">
              <el-icon><shop /></el-icon>
              <span>{{ orderInfo.storeName }}</span>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>

</template>

<script>
import { Shop } from '@element-plus/icons-vue'
import QrcodeVue from 'qrcode.vue'
import axios from 'axios'

export default {
  components: {
    QrcodeVue,
    Shop
  },
  data() {
    return {
      orderInfo: {  // 默认的模拟数据
        orderId: 4,
        voucherCode: "1912992723595774",
        groupbuyTitle: "葡萄系列3拉1",
        storeName: "菜百道奶茶（五角场中心店）",
        orderTime: "2025-05-04T20:46:49.077557",
        totalPrice: 11.0,
        status: "PAID"
      }
    }
  },
  computed: {
    statusTagType() {
      const statusMap = {
        PAID: 'success',
        UNPAID: 'danger',
        REFUNDED: 'info'
      }
      return statusMap[this.orderInfo.status] || 'info'
    }
  },
  methods: {
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString()
    },
    formatStatus(status) {
      const statusText = {
        PAID: '已支付',
        UNPAID: '未支付',
        REFUNDED: '已退款'
      }
      return statusText[status] || status
    },
    goToOrderList() {
      this.$router.push('/myorders')
    },
    goToHome(){
      this.$router.push('/')
    },
    goToShopDetail(){
      this.$router.push('/shopDetail')
    }
  },
  mounted() {
    const orderId = this.$route.params.id
    const response = axios.get(`http://localhost:8080/api/orders/${orderId}/details`)
    response.then(res => {
      this.orderInfo = res.data
      console.log(this.orderInfo)
    }).catch(err => {
      console.error('获取订单详情失败:', err)
    })
  }
}
</script>

<style scoped>
.order-detail-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
}

.header h1 {
  margin: 0;
  color: #303133;
}

.main-card {
  margin-bottom: 20px;
}

.detail-container {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
}

.qrcode-card {
  text-align: center;
  padding: 20px;
}

.qrcode-header {
  margin-bottom: 15px;
}

.voucher-code {
  display: block;
  font-size: 18px;
  font-weight: 500;
  color: #409EFF;
  letter-spacing: 2px;
}

.tips {
  color: #909399;
  font-size: 12px;
}

.qrcode-wrapper {
  padding: 15px;
  background: #f8f8f8;
  border-radius: 8px;
  margin: 20px 0;
}

.store-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #606266;
}

.price {
  color: #F56C6C;
  font-size: 18px;
  font-weight: bold;
}

.action-bar {
  text-align: center;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-container {
    grid-template-columns: 1fr;
  }

  .right-section {
    order: -1;
  }
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

</style>
