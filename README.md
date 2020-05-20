# cloud

## 模块

- server-eureka 服务注册中心
- server-config 配置中心
- server-api 用户接口服务
- nacos

## NACOS 部署

```
# download
wget https://github.com/alibaba/nacos/releases/download/1.3.0-beta/nacos-server-1.3.0-BETA.zip
unzip nacos-server-1.3.0-BETA.zip
cd nacos/bin
# Start
sh startup.sh -m standalone
```

link
http://127.0.0.1:8848/nacos

缺省用户名密码为 nacos/nacos
