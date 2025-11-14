import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import { useUserStore } from '@/stores/user'
import ShopDetail from '../components/ShopDetail.vue'
import PackageDetail from "@/components/PackageDetail.vue";
import HomeView from '../views/HomeView.vue'
import OrderDetail from '@/components/OrderDetail.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/shops/:id',
      name: 'shop-detail',
      component: ShopDetail,
      props: true
    },
    {
      path: '/package/:id',
      name: 'package-detail',
      component: PackageDetail,
    },
    {
      path: '/mycoupons',
      name: 'mycoupons',
      component: () => import('../views/MyCouponsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/myorders',
      name: 'myorders',
      component: () => import('../views/MyOrdersView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/newusercoupon',
      name: 'newusercoupon',
      component: () => import('../views/NewUserCouponView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/order/:id',
      name: 'order-detail',
      component: OrderDetail,
      props: true,
      meta: { requiresAuth: true }
    }
  ],
})

// 修改路由守卫，只对需要验证的路由进行守卫
router.beforeEach(async (to) => {
  const userStore = useUserStore();
  await userStore.checkLogin();

  if (to.meta.requiresAuth && !userStore.loggedIn) {
    return '/login'; // 重定向到登录页
  }
})

export default router
