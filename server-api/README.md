# server-api

面向用户的接口服务 port: 8770

## Start

> http://127.0.0.1:8771/name

当配置中心服务 `server-config` 存活的时候，会读取 `server-api-dev.properties` 中的 name 值

-> xiaop1ng

如果 `server-config` 不幸 dump 掉了，则会读取到 `application.yml` 中的 name 值

-> test

需要**注意**的是：Spring 的 @Value 值是一次性注入的。