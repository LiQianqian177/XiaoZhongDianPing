<script setup>
import {ref, onMounted, computed} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { useUserStore } from '@/stores/user';
const route = useRoute();
const router = useRouter();
const shop = ref(null);
const loading = ref(true);
const error = ref(null);
const currentImageIndex = ref(0);

const comments = ref([]);
const newComment = ref('');
const replyingTo = ref(null);
const newReply = ref('');
const loadingComments = ref(false);
const userStore = useUserStore();
const userId = ref(userStore.id);
const commentError = ref('');
const commentSuccess = ref('');
const couponAwarded = ref(false);
// 图片列表 - 使用完整的URL路径

const availableImages = ['/1.png', '/2.png', '/3.png'];

onMounted(async () => {
  try {
    const shopId = route.params.id;
    // 调用API获取商家详情
    const response = await axios.get(`http://localhost:8080/api/merchants/${shopId}`);
    shop.value = response.data;
    const packagesResponse = await axios.get(`http://localhost:8080/api/groupbuys/merchant/${shopId}`);
    shop.value.packages = packagesResponse.data;
    const picturesResponse = await axios.get(`http://localhost:8080/api/merchants/${shopId}/images`);
    shop.value.images = picturesResponse.data;
    console.log('商家详情数据:', shop.value);
    await loadComments(shopId);
  } catch (err) {
    console.error('获取商家信息错误:', err);
    error.value = err.message;
  } finally {
    loading.value = false;
  }
});

const loadComments = async (merchantId) => {
  loadingComments.value = true;
  try{
    const response = await axios.post(`http://localhost:8080/api/reviews/merchant-reviews`, {
      merchantId: merchantId
    });
    comments.value = response.data;
    if(userId.value){
      checkCouponEligibility();
    }
  }catch(err){
    console.error('获取评论失败：',err);
  }finally {
    loadingComments.value = false;
  }
};
const checkCouponEligibility = async () => {
  if(!userId.value){
    return;
  }
  try{
    const response = await axios.post(`http://localhost:8080/api/reviews/awards`,{
      userId: userId.value
    });
    couponAwarded.value = response.data;
  }catch(err){
    console.error('检查优惠券状态失败',err);
  }
};
const submitComment = async () => {
  if(newComment.value.trim().length ==0){
    commentError.value = '评论内容不能为空';
    return;
  }
  if(newComment.value.trim().length<15){
    commentError.value = '评论内容至少需要15个字';
    return;
  }
  if(!userId.value){
    commentError.value = '请先登录后再评论';
    return;
  }
  try{
    const shopId = route.params.id;
    const response = await axios.post(`http://localhost:8080/api/reviews/reviews`,{
      userId: userId.value,
      merchantId: shopId,
      parentId: null,
      content: newComment.value,
      createdAt: new Date().toISOString()
    });
    commentError.value = '';
    commentSuccess.value = '评论成功发表';

    newComment.value = '';
    await loadComments(shopId);
    setTimeout(()=>{
      commentSuccess.value = '';
    },3000);
  }catch(err){
    console.error('提交评论失败:',err);
    commentError.value = '提交评论失败，请稍后再试';
  }
};
const startReply = (commentId,parentUserName) => {
  replyingTo.value = {commentId,parentUserName};
  newReply.value = `@${parentUserName}`;
};
const cancelReply = () => {
  replyingTo.value = null;
  newReply.value = '';
}
const submitReply = async () => {
  if(!newReply.value.trim()){
    commentError.value = '回复内容不能为空';
    return;
  }
  if(!userId.value){
    commentError.value = '请先登录再提问';
    return;
  }
  try{
    const shopId = route.params.id;
    const response = await axios.post(`http://localhost:8080/api/reviews/reviews`,{
      userId: userId.value,
      merchantId: shopId,
      parentId: replyingTo.value.commentId,
      content: newReply.value,
      createdAt: new Date().toISOString(),
    });
    commentError.value = '';
    commentSuccess.value = '回复发表成功';
    newReply.value = '';
    replyingTo.value = null;
    await loadComments(shopId);
    setTimeout(()=>{
      commentSuccess.value = '';
    },3000);
  }catch(err){
    console.error('提交回复失败',err);
    commentError.value = '提交回复失败，请稍后再试';
  }
};
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
};
const canShowComments = computed(()=> !loading.value&&!error.value&& shop.value);
const toggleReplies = (comment) => {
  if(comment.showReplies == undefined){
    comment.showReplies = true;
  }else{
    comment.showReplies = !comment.showReplies;
  }
};
const goBack = () => {
  router.back(); // 返回上一页
};

// 设置当前显示的主图
const setMainImage = (index) => {
  currentImageIndex.value = index;
};

// 获取菜品背景样式
const getDishStyle = (dish) => {
  const imageIndex = dish.id % availableImages.length;
  const imagePath = availableImages[imageIndex];
  return {
    backgroundImage: `url(${imagePath})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center'
  };
};
// 获取套餐样式
const getPackageStyle = (pkg) => {
  const imageIndex = pkg.id % availableImages.length;
  const imagePath = availableImages[imageIndex];
  return {
    backgroundImage: `url(${imagePath})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center'
  };
};
const goToPackage = (packageId) => {
  router.push(`/package/${packageId}`);
};

// 格式化营业时间显示
const formatBusinessHours = (hours) => {
  if (!hours) return '暂无营业时间信息';
  return hours;
};
</script>

<template>
  <div class="shop-detail-container">
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <span>正在加载商家信息...</span>
    </div>

    <div v-else-if="error" class="error">
      <i class="el-icon-warning-outline"></i>
      <p>加载失败: {{ error }}</p>
      <button class="retry-button" @click="goBack">返回</button>
    </div>

    <div v-else-if="shop" class="shop-detail">
      <!-- 返回按钮 -->
      <div class="back-button">
        <button @click="goBack">← 返回搜索</button>
      </div>

      <!-- 商家头部信息卡片 -->
      <div class="shop-info-card">
        <div class="shop-header">
          <h1>{{ shop.name }}</h1>
          <div class="shop-rating">
            <i class="el-icon-star-on"></i>
            <span>{{ shop.rating.toFixed(1) }}</span>
          </div>
        </div>

        <div class="shop-basic-info">
          <p class="shop-description">{{ shop.description }}</p>

          <div class="info-grid">
            <div class="info-item">
              <i class="el-icon-location"></i>
              <span>{{ shop.address }}</span>
            </div>

            <div class="info-item">
              <i class="el-icon-phone"></i>
              <span>{{ shop.phone }}</span>
            </div>

            <div class="info-item">
              <i class="el-icon-time"></i>
              <span>营业时间：{{ formatBusinessHours(shop.businessHours) }}</span>
            </div>

            <div class="info-item">
              <i class="el-icon-money"></i>
              <span>人均消费：¥{{ shop.avgCost.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 商家图片轮播 -->
      <div class="content-section">
        <h2 class="section-title">
          <i class="el-icon-picture"></i>
          商家图片
        </h2>

        <div class="shop-images" v-if="shop.images && shop.images.length > 0">
          <div class="shop-main-image">
            <img
              :src="'http://localhost:8080/'+shop.images[currentImageIndex].imageUrl"
              :alt="shop.name"
              @error="$event.target.src = '/favicon.ico'"
            >
          </div>
          <div class="shop-thumbnails" v-if="shop.images.length > 1">
            <div
              v-for="(image, index) in shop.images"
              :key="image.id"
              class="shop-thumbnail"
              :class="{ active: index === currentImageIndex }"
              @click="setMainImage(index)"
            >
              <img
                :src="'http://localhost:8080/'+image.imageUrl"
                :alt="`${shop.name}图片${index+1}`"
                @error="$event.target.src = '/favicon.ico'"
              >
            </div>
          </div>
        </div>

        <div v-else class="no-images">
          暂无商家图片
        </div>
      </div>

      <!-- 团购套餐 -->
      <div class="content-section" v-if="shop.packages && shop.packages.length > 0">
        <h2 class="section-title">
          <i class="el-icon-shopping-bag-1"></i>
          团购套餐
        </h2>

        <div class="package-container">
          <div
            v-for="pkg in shop.packages"
            :key="pkg.id"
            class="package-card"
            @click="goToPackage(pkg.id)"
          >
            <div class="package-image" :style="getPackageStyle(pkg)"></div>
            <div class="package-content">
              <h3>{{pkg.title}}</h3>
              <p class="package-desc">{{pkg.description}}</p>
              <div class="package-footer">
                <span class="package-price">¥{{ pkg.price.toFixed(2) }}</span>
                <span class="package-sales">已售 {{pkg.sales}}</span>
              </div>
              <button class="view-package-btn">查看详情</button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="content-section">
        暂无团购套餐信息
      </div>

      <!-- 菜品列表 -->
      <div class="content-section" v-if="shop.dishes && shop.dishes.length > 0">
        <h2 class="section-title">
          <i class="el-icon-dish"></i>
          菜品列表
        </h2>

        <div class="dishes-container">
          <div v-for="dish in shop.dishes" :key="dish.id" class="dish-card">
            <div class="dish-image" :style="getDishStyle(dish)"></div>
            <div class="dish-content">
              <h3>{{ dish.name }}</h3>
              <p>{{ dish.description }}</p>
              <div class="dish-footer">
                <span class="dish-price">¥{{ dish.price.toFixed(2) }}</span>
                <div class="dish-tags">
                  <span class="dish-category">{{ dish.category }}</span>
                  <span :class="dish.isAvailable ? 'dish-available' : 'dish-unavailable'">
                    {{ dish.isAvailable ? "可点" : "售罄" }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="no-dishes">
        暂无菜品信息
      </div>

      <div class="content-section" v-if="canShowComments">
        <h2 class="section-title">
          <i class="el-icon-chat-line-round"></i>
          用户评论
        </h2>
        <!--优惠卷奖励提示-->
        <div v-if="userId" class="coupon-reward-info">
          <div v-if="couponAwarded" class="coupon-awarded">
            <i class="el-icon-present"></i>
            恭喜！您已获得参与评论的8折优惠券奖励，请前往“我的优惠券”查看。
          </div>
          <div v-else class="coupon-not-awarded">
            <i class="el-icon-s-opportunity"></i>
            撰写3条15字以上的有效点评，即可获得一张8折优惠券（最高抵扣20元）！
          </div>
        </div>

        <div v-if="loadingComments" class="loading">
          <div class="loading-spinner"></div>
          <span>正在加载评论...</span>
        </div>

        <div v-else>
          <!-- 评论列表 -->
          <div v-if="comments.length > 0" class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-card">
              <!-- 主评论 -->
              <div class="comment-header">
                <div class="user-info">
                  <span class="avatar">{{ comment.userName.charAt(0) }}</span>
                  <strong>{{ comment.userName }}</strong>
                </div>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-actions">
                <button class="reply-btn" @click="startReply(comment.id, comment.userName)">
                  <i class="el-icon-chat-dot-round"></i> 回复
                </button>
              </div>

              <!-- 递归组件：渲染评论的回复 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="comment-replies">
                <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                  <div class="reply-header">
                    <div class="user-info">
                      <span class="avatar">{{ reply.userName.charAt(0) }}</span>
                      <strong>{{ reply.userName }}</strong>
                    </div>
                    <span class="reply-date">{{ formatDate(reply.createdAt) }}</span>
                  </div>
                  <div class="reply-content">{{ reply.content }}</div>
                  <div class="reply-actions">
                    <button class="reply-btn" @click="startReply(reply.id, reply.userName)">
                      <i class="el-icon-chat-dot-round"></i> 回复
                    </button>
                  </div>

                  <!-- 递归渲染子回复 -->
                  <div v-if="reply.replies && reply.replies.length > 0" class="nested-replies">
                    <div v-for="childReply in reply.replies" :key="childReply.id" class="nested-reply-item">
                      <div class="reply-header">
                        <div class="user-info">
                          <span class="avatar">{{ childReply.userName.charAt(0) }}</span>
                          <strong>{{ childReply.userName }}</strong>
                        </div>
                        <span class="reply-date">{{ formatDate(childReply.createdAt) }}</span>
                      </div>
                      <div class="reply-content">{{ childReply.content }}</div>
                      <div class="reply-actions">
                        <button class="reply-btn" @click="startReply(childReply.id, childReply.userName)">
                          <i class="el-icon-chat-dot-round"></i> 回复
                        </button>
                      </div>

                      <!-- 第三级以后的回复使用折叠处理，点击展开 -->
                      <div v-if="childReply.replies && childReply.replies.length > 0" class="nested-replies collapsible">
                        <div class="view-more-replies" @click="toggleReplies(childReply)">
                          <span v-if="!childReply.showReplies">
                            <i class="el-icon-arrow-down"></i> 查看更多回复 ({{ childReply.replies.length }})
                          </span>
                          <span v-else>
                            <i class="el-icon-arrow-up"></i> 收起回复
                          </span>
                        </div>
                        <div v-if="childReply.showReplies" class="deep-nested-replies">
                          <div v-for="deepReply in childReply.replies" :key="deepReply.id" class="deep-nested-reply-item">
                            <div class="reply-header">
                              <div class="user-info">
                                <span class="avatar">{{ deepReply.userName.charAt(0) }}</span>
                                <strong>{{ deepReply.userName }}</strong>
                              </div>
                              <span class="reply-date">{{ formatDate(deepReply.createdAt) }}</span>
                            </div>
                            <div class="reply-content">{{ deepReply.content }}</div>
                            <div class="reply-actions">
                              <button class="reply-btn" @click="startReply(deepReply.id, deepReply.userName)">
                                <i class="el-icon-chat-dot-round"></i> 回复
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="no-comments">
            <i class="el-icon-chat-line-round"></i>
            <p>暂无评论，快来说说您的看法吧！</p>
          </div>

          <!-- 添加评论 -->
          <div class="add-comment">
            <h3><i class="el-icon-edit"></i> 写评论</h3>
            <textarea
              v-model="newComment"
              placeholder="分享您的就餐体验，您的评论将帮助其他用户了解这家商户... (至少15字)"
              :class="{ 'textarea-error': commentError && !newComment.trim() }"
            ></textarea>
            <div class="comment-footer">
              <div class="comment-tips">
                <span :class="{ 'text-warning': newComment.length < 15, 'text-success': newComment.length >= 15 }">
                  {{ newComment.length }}/15字
                </span>
              </div>
              <button
                class="submit-btn"
                @click="submitComment"
                :disabled="!userId"
              >
                发表评论
              </button>
            </div>
            <p v-if="!userId" class="login-tip">请先<router-link to="/login">登录</router-link>后再评论</p>
            <p v-if="commentError" class="error-message">{{ commentError }}</p>
            <p v-if="commentSuccess" class="success-message">{{ commentSuccess }}</p>
          </div>

          <!-- 回复框 -->
          <div v-if="replyingTo" class="reply-box">
            <div class="reply-box-header">
              <h4>回复 @{{ replyingTo.parentUserName }}</h4>
              <button class="close-btn" @click="cancelReply">×</button>
            </div>
            <textarea
              v-model="newReply"
              :placeholder="`回复 @${replyingTo.parentUserName}...`"
              autofocus
            ></textarea>
            <div class="reply-box-footer">
              <button class="cancel-btn" @click="cancelReply">取消</button>
              <button class="submit-btn" @click="submitReply">提交回复</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.shop-detail-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

/* 加载状态样式 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #409EFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误状态样式 */
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  color: #f56c6c;
}

.error i {
  font-size: 48px;
  margin-bottom: 15px;
}

.retry-button {
  margin-top: 20px;
  padding: 8px 20px;
  background: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.retry-button:hover {
  background: #f78989;
}

/* 返回按钮样式 */
.back-button {
  margin-bottom: 20px;
}

.back-button button {
  padding: 10px 20px;
  background: #f2f2f2;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  width: fit-content;
}

.back-button button:hover {
  background: #e0e0e0;
  transform: translateX(-3px);
}

/* 商家信息卡片 */
.shop-info-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.shop-header h1 {
  margin: 0;
  font-size: 26px;
  color: #303133;
}

.shop-rating {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background: #fff8e6;
  border-radius: 20px;
  font-weight: bold;
}

.shop-rating i {
  color: #ff9800;
  margin-right: 5px;
  font-size: 18px;
}

.shop-rating span {
  color: #ff9800;
  font-size: 18px;
}

.shop-basic-info {
  margin-top: 15px;
}

.shop-description {
  font-size: 16px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 15px;
  color: #606266;
}

.info-item i {
  margin-right: 10px;
  color: #909399;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 内容部分通用样式 */
.content-section {
  background: white;
  border-radius: 10px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 20px;
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #409EFF;
}

/* 商家图片样式 */
.shop-images {
  margin-bottom: 20px;
}

.shop-main-image {
  width: 100%;
  height: 400px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.shop-main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.shop-main-image:hover img {
  transform: scale(1.03);
}

.shop-thumbnails {
  display: flex;
  overflow-x: auto;
  gap: 12px;
  padding-bottom: 5px;
  padding: 5px;
}

.shop-thumbnail {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
  opacity: 0.7;
}

.shop-thumbnail:hover {
  border-color: #409EFF;
  opacity: 0.9;
}

.shop-thumbnail.active {
  border-color: #409EFF;
  opacity: 1;
}

.shop-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-images {
  text-align: center;
  padding: 40px;
  color: #909399;
  background: #f5f7fa;
  border-radius: 8px;
}

/* 团购套餐样式 */
.package-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.package-card {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  background: white;
  cursor: pointer;
}

.package-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.package-image {
  width: 100%;
  height: 180px;
  background-color: #f5f5f5;
  background-size: cover;
  background-position: center;
}

.package-content {
  padding: 16px;
}

.package-content h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
  color: #303133;
}

.package-desc {
  font-size: 14px;
  color: #606266;
  margin: 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.package-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  margin-bottom: 15px;
}

.package-price {
  font-weight: bold;
  color: #f56c6c;
  font-size: 18px;
}

.package-sales {
  color: #909399;
  font-size: 13px;
}

.view-package-btn {
  width: 100%;
  padding: 8px 0;
  background: #ecf5ff;
  color: #409EFF;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.view-package-btn:hover {
  background: #409EFF;
  color: white;
}

/* 菜品列表样式 */
.dishes-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.dish-card {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  background: white;
}

.dish-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.dish-image {
  width: 100%;
  height: 180px;
  background-color: #f5f5f5;
  background-size: cover;
  background-position: center;
}

.dish-content {
  padding: 16px;
}

.dish-content h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
  color: #303133;
}

.dish-content p {
  font-size: 14px;
  color: #606266;
  margin: 10px 0;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.dish-price {
  font-weight: bold;
  color: #f56c6c;
  font-size: 18px;
}

.dish-tags {
  display: flex;
  gap: 8px;
}

.dish-category {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 10px;
}

.dish-available {
  font-size: 12px;
  color: #67c23a;
  background: #f0f9eb;
  padding: 2px 8px;
  border-radius: 10px;
}

.dish-unavailable {
  font-size: 12px;
  color: #f56c6c;
  background: #fef0f0;
  padding: 2px 8px;
  border-radius: 10px;
}

.no-dishes {
  text-align: center;
  padding: 40px;
  color: #909399;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
/*评论区样式*/
.comments-list {
  margin-bottom: 30px;
}

.comment-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s;
}

.comment-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.comment-header, .reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #409EFF;
  color: white;
  border-radius: 50%;
  margin-right: 10px;
  font-weight: bold;
}

.comment-date, .reply-date {
  font-size: 12px;
  color: #909399;
}

.comment-content, .reply-content {
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 12px;
  word-break: break-word;
}

.comment-actions, .reply-actions {
  margin-top: 10px;
}

.reply-btn {
  background: none;
  border: none;
  color: #909399;
  font-size: 13px;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
}

.reply-btn i {
  margin-right: 4px;
}

.reply-btn:hover {
  color: #409EFF;
}

.comment-replies {
  margin-top: 15px;
  padding-left: 20px;
  border-left: 2px solid #f0f2f5;
}

.reply-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
}

.reply-item:last-child {
  border-bottom: none;
}

.nested-replies {
  margin-top: 10px;
  margin-left: 20px;
  border-left: 2px solid #f0f2f5;
  padding-left: 15px;
}

.nested-reply-item, .deep-nested-reply-item {
  padding: 10px 0;
  border-bottom: 1px dashed #f0f2f5;
}

.nested-reply-item:last-child, .deep-nested-reply-item:last-child {
  border-bottom: none;
}

.view-more-replies {
  display: inline-block;
  padding: 8px 12px;
  margin: 10px 0;
  color: #409EFF;
  font-size: 13px;
  cursor: pointer;
  background: #ecf5ff;
  border-radius: 15px;
  transition: all 0.2s;
}

.view-more-replies:hover {
  background: #dbedff;
}

.no-comments {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 30px;
}

.no-comments i {
  font-size: 36px;
  margin-bottom: 15px;
  display: block;
}

.add-comment {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.add-comment h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 18px;
  display: flex;
  align-items: center;
}

.add-comment h3 i {
  margin-right: 8px;
  color: #409EFF;
}

.add-comment textarea, .reply-box textarea {
  width: 100%;
  min-height: 100px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 10px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  transition: border 0.2s;
}

.add-comment textarea:focus, .reply-box textarea:focus {
  outline: none;
  border-color: #409EFF;
}

.textarea-error {
  border-color: #f56c6c !important;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-tips {
  font-size: 13px;
  color: #909399;
}

.text-warning {
  color: #e6a23c;
}

.text-success {
  color: #67c23a;
}

.submit-btn {
  padding: 10px 20px;
  background: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover {
  background: #2c8af8;
}

.submit-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.error-message {
  color: #f56c6c;
  font-size: 13px;
  margin-top: 10px;
}

.success-message {
  color: #67c23a;
  font-size: 13px;
  margin-top: 10px;
}

.login-tip {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.login-tip a {
  color: #409EFF;
  text-decoration: none;
}

.reply-box {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: white;
  padding: 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.reply-box-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.reply-box-header h4 {
  margin: 0;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 22px;
  color: #909399;
  cursor: pointer;
}

.reply-box-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.cancel-btn {
  padding: 8px 16px;
  background: #f2f2f2;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

/* 优惠券奖励提示 */
.coupon-reward-info {
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 8px;
  font-size: 14px;
}

.coupon-awarded {
  background: #f0f9eb;
  color: #67c23a;
  display: flex;
  align-items: center;
}

.coupon-not-awarded {
  background: #fdf6ec;
  color: #e6a23c;
  display: flex;
  align-items: center;
}

.coupon-reward-info i {
  font-size: 18px;
  margin-right: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .shop-detail-container {
    padding: 10px;
  }

  .shop-info-card, .content-section {
    padding: 15px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .shop-main-image {
    height: 250px;
  }

  .dishes-container, .package-container {
    grid-template-columns: 1fr;
  }

  .shop-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .shop-header h1 {
    margin-bottom: 10px;
  }

  .shop-rating {
    align-self: flex-start;
  }
}
</style>
