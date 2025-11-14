# lab3_backend

### GroubuyController
1. 通过商家ID 查询团购
- 51可替换成具体的商家ID
![img.png](img.png)
- 返回结果
![img_1.png](img_1.png)

2. 通过团购ID 查询具体的团购信息
- 1 可替换成具体的团购ID
![img_2.png](img_2.png)
- 返回结果
![img_4.png](img_4.png)

### OrderController
1. 创建订单
- 请求参数，需要用户ID，商家ID，团购ID以及优惠券ID（可选）
![img_5.png](img_5.png)
- 返回结果，这里没有返回具体的二维码，只是16位券码，需要前端根据券码去生成二维码
![img_6.png](img_6.png)

2.查询用户的所有历史订单
- 通过用户ID查询，这里1可以替换成具体的用户ID
![img_7.png](img_7.png)
- 返回结果
![img_8.png](img_8.png)

3.通过订单ID查询具体的订单信息
- 这里的4可以替换成具体的订单ID
![img_9.png](img_9.png)
- 返回结果
![img_10.png](img_10.png)

### CouponController

