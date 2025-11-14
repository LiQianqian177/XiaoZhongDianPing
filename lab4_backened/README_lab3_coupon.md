# 关于前端需要调用的接口
## 1.创建一张优惠券
~~~
POST http://localhost:8080/api/coupons/couponCreat
~~~
例：
~~~
{
  "userId": null,
  "name": "测试优惠券4",
  "description": "为了测试",
  "type": "DISCOUNT",
  "amount": 90.0,
  "usageThreshold": 0,
  "maxDiscountAmount": null,
  "applicableCategory": null,
  "applicableStore": null,
  "validFrom": "2025-05-01T00:00:00Z",
  "validTo": "2025-06-30T23:59:59Z",
  "totalQuantity": 10,
  "quantityPerUser": 1,
  "isNewUserCoupon": false
}
~~~
## 2.用户领取优惠券
~~~
POST http://localhost:8080/api/coupons/couponClaim
~~~
例：
~~~
{
  "userId": 1,
  "couponId": 4
}
~~~
## 3.用户使用优惠券（对团购）
~~~
POST http://localhost:8080/api/coupons/couponUse
~~~
例：
~~~
{
  "userCouponId": 2,
  "orderId": 10
}
~~~
## 4.通过用户id查询用户领取的优惠券
~~~
POST http://localhost:8080/api/coupons/userCoupon
~~~
例：
~~~
{
  "userId": 1
}
~~~
##### tip：用户需要先领取优惠券，才能使用