### 本项目用于推送官网当天更新的工作信息到个人邮箱中。

#### 1、使用指南

修改application-dev.yaml中的邮箱配置和打开redis服务器，然后启动项目，在浏览器输入以下url

```
localhost:8090/Job/getData?to=你的邮箱&type=三种类型&key=关键字
```

- 三种类型选择。应届校招，暑期实习，日常实习
- 关键字默认为java。如果你想输入c++的话，由于URL解码的原因，+会变为空格，使用%2B代替+。

#### 2、项目需完善点

- 如何让每个定时是独立的，即一个用户可能会开启不同的推送，不同的用户推送也要区分

- 发送邮件界面太粗糙

- 用户如何关闭这个定时发送功能 

- 当前配置的公司太少

- 没有一键投递简历的功能

- 。。。。。。

  

#### 3、项目时间线

| 时间      | 项目功能                 |
| --------- | ------------------------ |
| 2025-3-10 | 创建项目                 |
| 2025-3-20 | 完成阿里系职位推送       |
| 2025-3-21 | 完成美团职位推送         |
| 2025-3-27 | 完成京东和小红书职位推送 |
| 2025-3-30 | 完成快手职位推送         |
