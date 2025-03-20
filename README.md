### 本项目用于推送官网当天更新的工作信息到个人邮箱中。

#### 1、使用指南

1.1、在/common/src/main/java/com/example/workpush/utils/JavaMailUntil.java文件中填入自己的账号和密码，对应着的服务器名也需要修改

![FIG1](C:\Users\LiYang\Pictures\Camera Roll\FIG1.png)

1.2、修改web-api模块下的service包下的发件人和收件人

![FIG2](C:\Users\LiYang\Pictures\Camera Roll\FIG2.png)

1.3、修改application-dev.yaml中的搜索关键词



#### 2、项目需完善点

- 部署项目，让用户只需传入参数即可自己使用（对账号密码进行加密，保证安全）

- 关键参数（账号、密码等）需修改代码，需移到配置文件中

- 发送邮件界面太粗糙

- 没有一键投递简历的功能

- 。。。。。。

  

#### 3、项目时间线

| 时间      | 项目功能           |
| --------- | ------------------ |
| 2025-3-10 | 创建项目           |
| 2025-3-20 | 完成阿里系职位推送 |
