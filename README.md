# fivechess

## 介绍
使用JavaFx实现的五子棋游戏

## 软件架构
1. 前端数据存储：sqlite
2. 前端展示：JavaFx
3. 前后端网络请求：smart-socket

## 安装教程
安装JDK 8+运行环境即可

## 截图

## 流程说明
### 1、登录流程
```mermaid
sequenceDiagram
  participant 客户端
  participant 服务端

  客户端 ->> 服务端 : 建立连接
  服务端 -->> 客户端 : 连接成功
  客户端 ->> 客户端 : 登录信息录入
  客户端 ->> + 服务端 : 请求登录
  服务端 ->> - 服务端 : 认证用户信息
  服务端 --x 客户端 : 登录结果
```

### 2、游戏邀请
```mermaid
sequenceDiagram
  participant 用户A
  participant 服务器
  participant 用户B

  用户A ->> 服务器 : 请求与用户B进行游戏
  服务器 ->> 服务器 : 判断用户A和用户B状态
  服务器 -->> 用户A : 等待用户B响应
  服务器 ->> + 用户B : 用户A邀请你进行游戏
  用户B -->> - 服务器 : 用户B响应邀请
  服务器 --x 用户A : 用户B的响应结果
```

## 参与贡献
1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
