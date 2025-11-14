<template>
  <div class="coupon-container">
    <!-- 页面标题 -->
    <div class="header">
      <h1>新人专享礼包</h1>
      <p class="sub-title">以下优惠券三选一，立即领取开启美食之旅</p>
    </div>

    <!-- 优惠券列表 -->
    <div class="coupon-list">
      <el-card
        v-for="coupon in coupons"
        :key="coupon.id"
        class="coupon-card"
        :class="{ 'disabled': coupon.disabled }"
      >
        <div class="coupon-content">
          <!-- 左侧金额区域 -->
          <div class="coupon-left" :style="typeStyle(coupon.type)">
            <div class="amount">
              <template v-if="coupon.type === 'discount'">
                <span class="discount">{{ coupon.discount * 10 }}</span>
                <span class="unit">折</span>
              </template>
              <template v-else>
                <span class="symbol">¥</span>
                <span class="number">{{ coupon.amount }}</span>
              </template>
            </div>
            <div class="condition" v-if="coupon.condition">满{{ coupon.condition }}元可用</div>
          </div>

          <!-- 右侧信息区域 -->
          <div class="coupon-right">
            <div class="coupon-header">
              <h3 class="title">{{ coupon.title }}</h3>
              <el-tag
                v-if="coupon.remaining <= 10 && coupon.remaining > 0"
                type="danger"
                size="small"
              >
                仅剩{{ coupon.remaining }}张
              </el-tag>
            </div>

            <ul class="feature-list">
              <li v-for="(feature, index) in coupon.features" :key="index">
                <el-icon><checked /></el-icon>
                {{ feature }}
              </li>
            </ul>

            <div class="validity">
              <el-icon><clock /></el-icon>
              有效期：领取后{{ coupon.validDays }}天内有效
            </div>

            <!-- 操作按钮 -->
            <div class="action">
              <el-button
                :type="getButtonType(coupon)"
                :disabled="coupon.disabled || coupon.remaining <= 0"
                @click="handleReceive(coupon)"
                round
              >
                {{ getButtonText(coupon) }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 角标 -->
        <div v-if="coupon.tag" class="corner-tag" :style="typeStyle(coupon.type)">
          {{ coupon.tag }}
        </div>
      </el-card>
    </div>

    <!-- 操作提示 -->
    <div class="action-tips">
      <el-button type="text" @click="goToMyCoupons">
        <el-icon><tickets /></el-icon>
        查看我的卡包
      </el-button>
      <el-button type="text" @click="goToHome">
        <el-icon><shopping-cart /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Checked, Clock, Tickets, ShoppingCart } from '@element-plus/icons-vue'
import router from '@/router'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 优惠券数据
const coupons = ref([
  {
    id: 1,
    type: 'discount',
    title: 'KFC9折券',
    discount: 0.9,
    condition: 10,
    features: [
      '仅限KFC南区店使用',
      '可与套餐优惠叠加',
      '周一至周五可用'
    ],
    validDays: 7,
    received: false,
    tag: '爆款推荐'
  },
  {
    id: 2,
    type: 'cash',
    title: '奶茶免单券',
    amount: 15,
    features: [
      '奶茶品类专用',
      '无门槛直接抵扣',
      '支持所有奶茶门店'
    ],
    validDays: 7,
    received: false,
    tag: '手慢无'
  },
  {
    id: 3,
    type: 'cash',
    title: '100元通用券',
    amount: 100,
    condition: 200,
    features: [
      '全品类通用券',
      '大额满减优惠',
      '限时超值福利'
    ],
    validDays: 1,
    received: false,
    tag: '限量神券'
  }
])

// 获取按钮样式
const getButtonType = (coupon) => {
  if (coupon.received) return 'info'
  if (coupon.remaining <= 0) return 'danger'
  return coupon.type === 'discount' ? 'warning' : 'success'
}

// 获取按钮文字
const getButtonText = (coupon) => {
  if (coupon.received) return '已领取'
  if (coupon.remaining <= 0) return '已抢光'
  return '立即领取'
}

// 类型样式
const typeStyle = (type) => {
  return {
    backgroundColor: type === 'discount' ? '#ffba00' : '#ee2a7b',
    color: '#fff'
  }
}

// 模拟领取操作
const handleReceive = async (coupon) => {
  if (coupon.received || coupon.remaining <= 0) return

  try {
    await axios.post('/api/coupons/couponClaim', {
      couponId: coupon.id,
      userId: userStore.id
    })

    coupon.received = true
    coupon.remaining -= 1

    ElMessage.success({
      message: `成功领取 ${coupon.title}`,
      duration: 2000
    })
    userStore.newUserCouponClaimed = true
    setTimeout(() => {
      router.push('/')
    }, 2000)
  } catch (error) {
    console.log(error);
    const message = error.response.data
    if (message.indexOf('fully claimed') >= 0){
      ElMessage.error({
        message: '来晚了一步，已经被领完啦',
        duration: 2000
      })
    } else if (message.indexOf('already claimed') >= 0) {
      ElMessage.error({
        message: '您已经领取过新人优惠券了',
        duration: 2000
      })
      userStore.newUserCouponClaimed = true
      setTimeout(() => {
        router.push('/')
      }, 2000)
    } else if (message.indexOf('new user') >= 0) {
      ElMessage.error({
        message: '您不是新用户，无法领取新人优惠券',
        duration: 2000
      })
      userStore.newUserCouponClaimed = true
    } else {
      ElMessage.error({
        message: '领取失败，请稍后再试',
        duration: 2000
      })
    }
  }
}

// 路由跳转
const goToMyCoupons = () => {
  router.push('/mycoupons')
}

const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.coupon-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.sub-title {
  color: #666;
  margin-top: 10px;
}

.coupon-list {
  display: grid;
  gap: 30px;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
}

.coupon-card {
  position: relative;
  overflow: hidden;
  transition: transform 0.3s;
}

.coupon-card:hover {
  transform: translateY(-5px);
}

.coupon-content {
  display: flex;
  height: 250px;
}

.coupon-left {
  flex: 0 0 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
}

.amount {
  text-align: center;
}

.discount {
  font-size: 48px;
  font-weight: bold;
  line-height: 1;
}

.unit {
  font-size: 20px;
}

.symbol {
  font-size: 24px;
  vertical-align: top;
}

.number {
  font-size: 48px;
  font-weight: bold;
}

.condition {
  margin-top: 10px;
  font-size: 12px;
}

.coupon-right {
  flex: 1;
  padding: 15px;
  display: flex;
  flex-direction: column;
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.title {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.feature-list {
  flex: 1;
  margin: 10px 0;
  padding: 0;
  list-style: none;
  font-size: 13px;
  color: #666;
}

.feature-list li {
  margin: 8px 0;
  display: flex;
  align-items: center;
}

.feature-list .el-icon {
  margin-right: 5px;
  color: #67C23A;
}

.validity {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
}

.action {
  margin-top: 15px;
  text-align: center;
}

.corner-tag {
  position: absolute;
  right: -30px;
  top: 10px;
  width: 100px;
  padding: 5px 0;
  text-align: center;
  transform: rotate(45deg);
  font-size: 12px;
}

.disabled {
  opacity: 0.6;
  filter: grayscale(0.8);
}

.action-tips {
  margin-top: 30px;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 30px;
}
</style>
