<template>
  <!-- 搜索区域保持不变 -->
  <div class="search-container">
    <div class="search-box">
      <i class="el-icon-search search-icon"></i>
      <input v-model="keyword" @keyup.enter="handleSearch" placeholder="输入关键词，查找您喜爱的美食..." @focus="showHistory=true"
             @blur="hideHistory" />
      <button @click="handleSearch">搜索</button>
    </div>

    <!-- 搜索历史 -->
    <ul v-if="searchHistory.length && showHistory" class="search-history">
      <li class="history-header">搜索历史</li>
      <li v-for="(item, index) in searchHistory" :key="index" @click="selectHistory(item)" class="history-item">
        <i class="el-icon-time"></i>
        <span>{{ item }}</span>
      </li>
    </ul>
  </div>

  <!-- 过滤器卡片 -->
  <div class="filter-card">
    <div class="filter-header">
      <h3>高级筛选</h3>
      <span class="filter-hint">调整以下选项获得更精准的结果</span>
    </div>

    <div class="filters">
      <div class="filter-line">
        <div class="filter-group">
          <span class="filter-label">排序方式：</span>
          <el-select v-model="sortBy" placeholder="选择排序方式" class="filter-item" @change="handleFilterChange">
            <el-option label="综合排序" value="default"></el-option>
            <el-option label="评分最高" value="rating"></el-option>
            <el-option label="人均消费最低" value="price_asc"></el-option>
            <el-option label="人均消费最高" value="price_desc"></el-option>
            <el-option label="菜品价格最低" value="dishprice_asc"></el-option>
            <el-option label="菜品价格最高" value="dishprice_desc"></el-option>
          </el-select>
        </div>

        <div class="filter-group">
          <span class="filter-label">最低评分：</span>
          <!-- 修改滑块，添加防抖动处理 -->
          <el-slider
            v-model="minRating"
            :min="0"
            :max="5"
            :step="0.5"
            show-stops
            class="filter-slider"
            @change="handleFilterChange"
          ></el-slider>
          <span class="rating-value">{{ minRating }}分</span>
        </div>
      </div>

      <div class="filter-line">
        <div class="filter-group">
          <span class="filter-label">价格区间：</span>
          <el-input-number v-model="minPrice" :min="0" class="filter-item" size="small" @change="handleFilterChange" />
          <span>至</span>
          <el-input-number v-model="maxPrice" :min="0" class="filter-item" size="small" @change="handleFilterChange" />
          <span>元</span>
        </div>

        <div class="filter-group">
          <span class="filter-label">人均消费：</span>
          <el-input-number v-model="minAvgCost" :min="0" class="filter-item" size="small" @change="handleFilterChange" />
          <span>至</span>
          <el-input-number v-model="maxAvgCost" :min="0" class="filter-item" size="small" @change="handleFilterChange" />
          <span>元</span>
        </div>
      </div>
    </div>
  </div>

  <!-- 搜索通知 -->
  <transition name="fade">
    <div v-if="showNotification" class="search-notification" :class="{ 'success': notificationSuccess }">
      <i :class="notificationSuccess ? 'el-icon-success' : 'el-icon-loading'"></i>
      <div class="notification-content">
        <h3>{{ notificationTitle }}</h3>
        <p>{{ notificationMessage }}</p>
      </div>
      <button class="close-btn" @click="hideNotification">
        <i class="el-icon-close"></i>
      </button>
    </div>
  </transition>

  <!-- 搜索结果 -->
  <div v-if="showResults" class="results-container">
    <div class="results-header">
      <h2 class="results-title">搜索结果: <span class="keyword-highlight">{{ keyword }}</span></h2>

      <!-- 添加返回首页按钮 -->
      <button class="back-to-home" @click="backToHome">
        <i class="el-icon-back"></i> 返回首页
      </button>
    </div>

    <ul class="search-results">
      <li v-if="results.length === 0" class="no-results">
        <i class="el-icon-warning-outline"></i>
        <p>没有找到相关商家</p>
        <p class="try-again">请尝试其他关键词或调整筛选条件</p>
      </li>
      <li v-else v-for="(shop, index) in results" :key="index" class="shop-card" @click="goToShopDetail(shop.id)">
        <div class="shop-header">
          <h3 class="shop-name">{{ shop.name }}</h3>
          <span class="shop-rating">
            <i class="el-icon-star-on"></i>
            {{ shop.rating.toFixed(1) }}
          </span>
        </div>

        <p class="shop-description">{{ shop.description }}</p>

        <div class="shop-info">
          <p><i class="el-icon-location"></i> 地址: {{ shop.address }}</p>
          <p><i class="el-icon-phone"></i> 联系方式： {{ shop.phone }}</p>
          <p class="avg-cost"><i class="el-icon-money"></i> 人均：¥{{ shop.avgCost.toFixed(2) }}</p>
        </div>

        <button class="view-detail-btn">查看详情</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted, ref} from "vue";
import axios from "axios";
import { ElNotification } from "element-plus";
import { useRouter } from "vue-router";
import { useSearchStore } from "@/stores/storefind.js";

const keyword = ref("");
const results = ref([]);
const showHistory = ref(false);
const showResults = ref(false);
const searchHistory = ref(JSON.parse(localStorage.getItem("searchHistory")) || []);
const searchHistoryLimit = 5; // 限制搜索历史数量
const router = useRouter();
const searchStore = useSearchStore();

// 添加返回首页函数
const backToHome = () => {
  // 清空搜索结果
  results.value = [];
  searchStore.setResults([]);
  searchStore.setKeyword("");
  keyword.value = "";
  showResults.value = false;

  // 重置筛选条件
  sortBy.value = "default";
  minRating.value = 0;
  minPrice.value = null;
  maxPrice.value = null;
  minAvgCost.value = null;
  maxAvgCost.value = null;

  // 显示返回提示
  showSearchNotification("已返回首页", "正在显示所有商家", true);
};
// 排序 & 筛选参数
const sortBy = ref("default"); // 排序方式
const minRating = ref(0); // 评分最低分
const minPrice = ref(null);
const maxPrice = ref(null);
const minAvgCost = ref(null);
const maxAvgCost = ref(null);

// 添加通知状态
const showNotification = ref(false);
const notificationTitle = ref("");
const notificationMessage = ref("");
const notificationSuccess = ref(false);
let searchDebounceTimer = null;
let isSearchInProgress = false;

// 防抖函数 - 用于筛选条件变化
const debounce = (fn, delay) => {
  return (...args) => {
    clearTimeout(searchDebounceTimer);
    searchDebounceTimer = setTimeout(() => {
      fn(...args);
    }, delay);
  };
};

// 处理筛选条件变化
const handleFilterChange = debounce(() => {
  if (keyword.value.trim() && !isSearchInProgress) {
    handleSearch();
  }
}, 800); // 800ms的延迟

// 显示通知
const showSearchNotification = (title, message, success = false) => {
  notificationTitle.value = title;
  notificationMessage.value = message;
  notificationSuccess.value = success;
  showNotification.value = true;

  // 3秒后自动隐藏
  setTimeout(() => {
    hideNotification();
  }, 3000);
};

// 隐藏通知
const hideNotification = () => {
  showNotification.value = false;
};

const selectHistory = (item) => {
  keyword.value = item;
  handleSearch();
};

const hideHistory = () => {
  setTimeout(() => {  // 延迟隐藏, 避免点击搜索历史时无法触发
    showHistory.value = false;
  }, 200);
};

const updateSearchHistory = (newKeyword) => {
  let history = [...searchHistory.value]; // 复制当前历史
  // 如果已存在，移除旧的位置
  const existingIndex = history.indexOf(newKeyword);
  if (existingIndex > -1) {
    history.splice(existingIndex, 1);
  }
  // 添加到顶部
  history.unshift(newKeyword);
  // 限制数量
  if (history.length > searchHistoryLimit) {
    history = history.slice(0, searchHistoryLimit);
  }

  searchHistory.value = history;
  localStorage.setItem("searchHistory", JSON.stringify(history));
};

const handleSearch = async () => {
  if (isSearchInProgress) return; // 如果搜索正在进行中，则不重复搜索

  let keywordTrim = keyword.value.trim();
  if (!keywordTrim){
    ElNotification({
      title: "提示",
      message: "请输入搜索关键词",
      type: "warning",
      position: 'top-right'
    });
    return;
  }

  isSearchInProgress = true; // 标记搜索开始

  const keywordList = keywordTrim.split(/\s+/).filter(Boolean); // 以空格分割关键词, 去除空字符串
  keywordTrim = keywordList.join(" "); // 重新组合成字符串, 以便加入搜索历史

  var actualMinPrice = minPrice.value;
  var actualMaxPrice = maxPrice.value;
  if (actualMinPrice && actualMaxPrice && actualMinPrice > actualMaxPrice) {
    [actualMinPrice, actualMaxPrice] = [actualMaxPrice, actualMinPrice];
  }
  var actualMinAvgCost = minAvgCost.value;
  var actualMaxAvgCost = maxAvgCost.value;
  if (actualMinAvgCost && actualMaxAvgCost && actualMinAvgCost > actualMaxAvgCost) {
    [actualMinAvgCost, actualMaxAvgCost] = [actualMaxAvgCost, actualMinAvgCost];
  }

  try {
    updateSearchHistory(keywordTrim);
    results.value = [];
    showResults.value = false;  // 清空之前的搜索结果

    // 显示搜索中通知
    showSearchNotification("搜索中", "正在查找相关商家...", false);

    const response = await axios.post("http://localhost:8080/api/merchants/filterAndSort",
      JSON.stringify({
        keywords: keywordList,
        isSortByOverall: sortBy.value === "default",
        isSortByRating: sortBy.value === "rating",
        isSortByPrice: sortBy.value === "price_asc" || sortBy.value === "price_desc",
        isSortByDishPrice: sortBy.value === "dishprice_asc" || sortBy.value === "dishprice_desc",
        minRating: minRating.value > 0 ? minRating.value : null,
        minPrice: actualMinPrice || null,
        maxPrice: actualMaxPrice || null,
        minAvgCost: actualMinAvgCost || null,
        maxAvgCost: actualMaxAvgCost || null,
      }),
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    if (sortBy.value.includes("desc")) response.data.reverse(); // 如果是降序, 则反转结果
    searchStore.setResults(response.data)
    searchStore.setKeyword(keywordTrim)

    results.value = response.data;
    showResults.value = true;

    // 显示搜索完成通知
    showSearchNotification(
      "搜索完成",
      `找到 ${response.data.length} 个相关商家`,
      true
    );

  } catch (error){
    showSearchNotification("搜索失败", "网络繁忙，请稍后再试", false);
    console.log(error.message);
  } finally {
    isSearchInProgress = false; // 标记搜索结束
  }
};

onMounted(() => {
  if (searchStore.results.length > 0) {
    results.value = searchStore.results
    showResults.value = true
    keyword.value = searchStore.keyword
  }
});

// 移除直接的watch监听，改为使用handleFilterChange函数

const goToShopDetail = (shopId) => {
  router.push(`/shops/${shopId}`);
};
</script>

<style scoped>
.search-container {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto 30px;
  z-index: 10;
}

.search-box {
  display: flex;
  position: relative;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border-radius: 50px;
  overflow: hidden;
}

.search-icon {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #909399;
  font-size: 20px;
}

input {
  flex: 1;
  padding: 16px 20px 16px 50px;
  font-size: 16px;
  border: none;
  outline: none;
  background: white;
}

button {
  padding: 0 30px;
  font-size: 16px;
  background-color: #409EFF;
  color: white;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
  font-weight: 500;
}

button:hover {
  background-color: #66b1ff;
}

/* 搜索通知样式 */
.search-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  padding: 15px 20px;
  min-width: 300px;
  max-width: 450px;
  border-left: 4px solid #409EFF;
}

.search-notification.success {
  border-left-color: #67C23A;
}

.search-notification i {
  font-size: 24px;
  margin-right: 15px;
  color: #409EFF;
}

.search-notification.success i {
  color: #67C23A;
}

.notification-content {
  flex: 1;
}

.notification-content h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.notification-content p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.close-btn {
  background: transparent;
  border: none;
  color: #909399;
  padding: 0;
  margin-left: 10px;
  cursor: pointer;
  font-size: 16px;
}

.close-btn:hover {
  color: #606266;
  background: transparent;
}

/* 淡入淡出效果 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 搜索历史 */
.search-history {
  list-style: none;
  padding: 0;
  position: absolute;
  z-index: 20;
  width: 100%;
  background: white;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  margin-top: 5px;
  overflow: hidden;
}

.history-header {
  padding: 12px 15px;
  font-size: 14px;
  font-weight: 500;
  color: #909399;
  background: #f5f7fa;
  border-bottom: 1px solid #e6e6e6;
}

.history-item {
  padding: 12px 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: background-color 0.2s;
}

.history-item i {
  color: #909399;
  font-size: 14px;
}

.history-item:hover {
  background-color: #f0f7ff;
}

/* 筛选器卡片 */
.filter-card {
  max-width: 900px;
  margin: 0 auto 30px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.filter-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.filter-hint {
  font-size: 13px;
  color: #909399;
}

.filters {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-line {
  display: flex;
  flex-wrap: wrap;
  gap: 25px;
  justify-content: space-between;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 300px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  width: 80px;
}

.filter-item {
  max-width: 130px;
}

.filter-slider {
  max-width: 200px;
}

.rating-value {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  width: 45px;
  text-align: center;
}

/* 搜索结果 */
.results-container {
  max-width: 800px;
  margin: 40px auto;
}

/* 结果标题和返回按钮布局 */
.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ebeef5;
}

.results-title {
  font-size: 22px;
  color: #303133;
  margin: 0;
}

.back-to-home {
  background: #f0f2f5;
  border: none;
  color: #606266;
  padding: 8px 16px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  transition: all 0.3s;
  cursor: pointer;
}

.back-to-home:hover {
  background: #e0e2e5;
  color: #303133;
}

.keyword-highlight {
  color: #409EFF;
  font-weight: 600;
}

.search-results {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.no-results {
  padding: 40px;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.no-results i {
  font-size: 48px;
  color: #909399;
  margin-bottom: 15px;
}

.no-results p {
  margin: 5px 0;
  font-size: 18px;
  color: #606266;
}

.try-again {
  font-size: 14px !important;
  color: #909399 !important;
}

/* 无结果时的返回按钮 */
.back-btn {
  background: #409EFF;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  margin-top: 15px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #66b1ff;
}

.shop-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  position: relative;
  border-left: 4px solid #409EFF;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.shop-name {
  margin: 0;
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.shop-rating {
  padding: 4px 10px;
  background: #fff8e6;
  border-radius: 16px;
  font-weight: bold;
  color: #ff9800;
  display: flex;
  align-items: center;
  gap: 5px;
}

.shop-description {
  font-size: 15px;
  color: #606266;
  line-height: 1.5;
  margin: 12px 0;
}

.shop-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin: 15px 0;
}

.shop-info p {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.shop-info i {
  color: #909399;
}

.avg-cost {
  font-weight: 500;
  color: #f56c6c !important;
}

.view-detail-btn {
  background: #ecf5ff;
  color: #409EFF;
  border: 1px solid #d9ecff;
  width: 100%;
  margin-top: 15px;
  padding: 10px;
  border-radius: 6px;
  font-size: 15px;
  transition: all 0.3s;
}

.view-detail-btn:hover {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-line {
    flex-direction: column;
    gap: 15px;
  }

  .filter-group {
    min-width: auto;
  }

  .shop-info {
    grid-template-columns: 1fr;
  }

  .search-box {
    flex-direction: column;
    border-radius: 10px;
  }

  .search-box input {
    border-radius: 10px 10px 0 0;
    padding: 14px 20px 14px 50px;
  }

  .search-box button {
    border-radius: 0 0 10px 10px;
    padding: 12px 30px;
  }

  .filter-slider {
    max-width: 150px;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .back-to-home {
    align-self: flex-start;
  }
}
</style>
