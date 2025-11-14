# 关于前端需要调用的接口
## 用户注册
POST http://localhost:8080/users/register
{
    "name": " ",  
    "password": " ",
    "verificationCode": " "
}

## 用户登录
POST http://localhost:8080/users/login
{
    "name": "  ",
    "password": " "
}

## 请求验证码
GET http://localhost:8080/api/captcha

## 查询已登录用户信息
GET http://localhost:8080/users/loggedUser
Authorization: Bearer 对应的token

## 用户注销
GET http://localhost:8080/users/logout
Authorization: Bearer 对应的token

## 调用步骤推荐
1. 请求验证码
   GET http://localhost:8080/api/captcha
2. 读出验证码上的字符
3. 调用用户注册接口
   POST http://localhost:8080/users/register
    按类似形式填入以下信息（本例已注册，请勿重复注册）：
   Content-Type: application/json
    {
    "name": "abcd",
    "password": "aaa1111",
    "verificationCode": "2c5b"
    }
4. 调用登录接口
   POST http://localhost:8080/users/login
   Content-Type: application/json
    {
    "name": "abcd",
    "password": "aaa1111"
    }

