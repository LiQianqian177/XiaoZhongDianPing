# 关于前端需要调用的接口
## 根据关键词搜索商家
```
GET http://localhost:8080/api/merchants/search?keywords=火锅&keywords=烧烤
```

## 筛选和排序商家
```
POST http://localhost:8080/api/merchants/filterAndSort
Content-Type: application/json

{
  "minRating": 3.5,
  "maxAverageCost": 100,
  "isOpen": true,
  "sortBy": "rating",
  "sortDirection": "desc"
}
```

## 根据ID获取商家详情
```
GET http://localhost:8080/api/merchants/123
```
*注：此处实际商家ID可在数据库中查到*

## 获取商家图片URl合集
```
http://localhost:8080/api/merchants/2851/images

```
*注：2851处应填入实际ID*

## 显示商家或其菜品图片
```
http://localhost:8080/uploads/merchant-images/1.jpg
```

*注：1处应填入实际ID,即num.jpg*

-----------------0404添加-------------------------------
# 使用本地数据库
1. 在自己的本地数据库下创建数据库 XiaoZhongDianPing
2. 在ApplicationProperties(resources下那个)修改用户名和密码为自己的
3. 可以用了